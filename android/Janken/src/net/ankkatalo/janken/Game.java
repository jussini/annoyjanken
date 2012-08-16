package net.ankkatalo.janken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
	
	
	private List<Strategy> mStrategies = new ArrayList<Strategy>();
	
	public enum WinType {BEATS, LOSES, TIE}
	
	// init number of losses to 2 to force a new strategy to be picked on 
	// first round
	private int mCurrentStrategyLossStreak = 2; 
	private int mCurrentStrategyIndex = 0;
	/**  
	 * constructor
	 * */
	public Game() {
		mStrategies.add(new FrequencyStrategy());
		mStrategies.add(new RandomStrategy());
		mStrategies.add(new PreviousBeaterStrategy());
	}

	public void updateStrategies(Item playerItem, Item cpuItem) {
		
		if (playerItem.beats(cpuItem) == WinType.BEATS) {
			mCurrentStrategyLossStreak++;
		}
		
		for (Strategy s : mStrategies) {
			s.updateStrategy(playerItem, cpuItem);
		}
	} 
	
	/**
	 * Decides what to use as a response. Works by trying to guess the best
	 * response based on player's previous activities.
	 * 
	 * */
	public Item selectResponse() {		
		/*
		 * plan:
		 * 	 - pick the first strategy randomly
		 *   - pick a response. if it returns null, roundrobin to next strategy
		 *     which becomes the current strategy. At some point there will be
		 *     a strategy that's guaranteed to return non-null (random strategy)
		 *   - keep using the current strategy until it has failed twice a row
		 *     in which case pick a new strategy randomly
		 * */
		Random random = new Random();
		
		if (mCurrentStrategyLossStreak >= 2) {
			System.out.println(String.format("loss streak %d, swich strategy", mCurrentStrategyLossStreak));
			mCurrentStrategyIndex = random.nextInt(mStrategies.size());
			mCurrentStrategyLossStreak = 0;
		}
		
		System.out.println(String.format("Using strategy %d", mCurrentStrategyIndex));
		Item response = mStrategies.get(mCurrentStrategyIndex).selectResponse();
		
		while (response == null) {
			System.out.println(String.format("Response from %d was null", mCurrentStrategyIndex));
			mCurrentStrategyIndex = (mCurrentStrategyIndex + 1) % mStrategies.size();
			response = mStrategies.get(mCurrentStrategyIndex).selectResponse();
		}
		
		return response;		
	}
	
	
	/**
	 * Quickly decides which of the player1 and player2 wins. Works by comparing
	 * the items the players have.
	 * 
	 * @return player1 or player2 if either of them wins or null if it's a tie
	 * */
	public Player selectWinner(Player player1, Player player2) {		
		if (player1.item().beats(player2.item()) == WinType.BEATS) {
			return player1;
		}
		if (player2.item().beats(player1.item()) == WinType.BEATS) {
			return player2;
		}
		return null; // ugh, this is ugly
	}
	
	
	public void clearHistory() {
		for (Strategy s : mStrategies) {
			s.clearHistory();
		}		
	}
	
	public String playerHistory() {
		return Strategy.playerHistory();
	}
	
	public String cpuHistory() {
		return Strategy.cpuHistory();
	}

	
	public void setHistory(String playerHistory, String cpuHistory) {		
		Strategy.setPlayerHistory(playerHistory);
		Strategy.setCpuHistory(cpuHistory);
	}

	public void initStrategies() {
		
		for (Strategy s : mStrategies) {
			s.initStrategy();
		}
				
	}
		
}
