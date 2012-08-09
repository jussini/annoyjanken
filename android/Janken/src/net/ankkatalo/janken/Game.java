package net.ankkatalo.janken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game {
	
	
	private List<Strategy> mStrategies = new ArrayList<Strategy>();
	
	public enum WinType {BEATS, LOSES, TIE}
	
	/**  
	 * constructor
	 * */
	public Game() {
		mStrategies.add(new FrequencyStrategy());
	}

	public void updateStrategies(Item playerItem, Item cpuItem) {
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
		// TODO: pick the response from the best strategy
		return mStrategies.get(0).selectResponse();		
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
