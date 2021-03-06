package net.ankkatalo.janken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.ankkatalo.janken.Strategy.Certainty;

/**
 * Class responsible for game related issues like selecting the winner,
 * managing different strategies and selecting the best strategy for each
 * situation. 
 * */
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
		mStrategies.add(new PreviousCpuBeaterStrategy());
		mStrategies.add(new PreviousPlayerBeaterStrategy());
	}


	/**
	 * Updates the history and calls updateStrategy for all strategies
	 * 
	 * @param playerItem the item player previously gave
	 * @param cpuItem the item cpu predicted for last round
	 * */
	public void updateStrategies(Item playerItem, Item cpuItem) {

		if (playerItem.beats(cpuItem) == WinType.BEATS) {
			mCurrentStrategyLossStreak++;
		}

		// update player and cpu histories
		Strategy.setPlayerHistory(Strategy.playerHistory()  + playerItem.shortName());
		Strategy.setCpuHistory(Strategy.cpuHistory()  + cpuItem.shortName());		

		// run individual strategy updates
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
		 *   - ask all strategies for their certainties. if any has better than
		 *     guess, use it
		 * 	 - otherwise use "current strategy". if there isn't one, pick it 
		 *     randomly
		 *   - pick a response. if it returns null, roundrobin to next
		 *     strategy which becomes the current strategy. At some point there will be
		 *     a strategy that's guaranteed to return non-null (random strategy)
		 *   - keep using the current strategy until it has failed twice a row
		 *     in which case pick a new strategy randomly
		 *     
		 *     
		 * another plan:
		 *   - every 3rd round, ask from strategies for certainty
		 *     -> if any has, this will be the current strategy for the next
		 *        2 rounds unless it has just failed for the last 2 rounds
		 *     -> otherwise, pick a strategy by random   
		 *   - if a strategy returns null, roundrobin to next strategy until
		 *     there will be non-null (from random strategy)  
		 * */
		Random random = new Random();
		int nextIndex = -1;


		// on start of every round, ask strategies if they have any certainties
		//
		// first, group strategies by their certainties
		List<Integer> guessing = new ArrayList<Integer>();
		List<Integer> some = new ArrayList<Integer>(); 
		List<Integer> very = new ArrayList<Integer>();
		for (int i = 0; i < mStrategies.size(); ++i) {
			Certainty cert = mStrategies.get(i).certainty();
			if (cert == Certainty.SOME) {
				some.add(i);
			} else if (cert == Certainty.VERY) {
				very.add(i);
			} else {
				guessing.add(i);
			}
		}

		// first check the very certains
		while (very.size() > 0) {
			nextIndex = very.remove(0);
			// if loss streak is on, avoid selecting same strategy even if
			// it has a good certainty
			if (mCurrentStrategyLossStreak >= 2 && 
					nextIndex == mCurrentStrategyIndex) {
				nextIndex = -1;
			} 				
		}

		// then the same procedure to group with some certainty
		while (nextIndex < 0 && some.size() > 0) {
			nextIndex = some.remove(0);
			if  (mCurrentStrategyLossStreak >= 2 && 
					nextIndex == mCurrentStrategyIndex ) {
				nextIndex = -1;
			}
		}

		// then, if we found a strategy with any certainty, use that
		if (nextIndex >= 0) {
			mCurrentStrategyIndex = nextIndex;
		}



		// no need to change strategy if we have already picked a new one
		if (mCurrentStrategyLossStreak >= 2 && nextIndex != -1) {
			mCurrentStrategyLossStreak = 0;
		}

		// if the loss streak is on, force changing the strategy
		if (mCurrentStrategyLossStreak >= 2) {

			boolean certain = false;
			for (int i = 0; i < mStrategies.size(); ++i) {
				if (mStrategies.get(i).certainty().compareTo(Certainty.GUESS) > 0 ) {
					mCurrentStrategyIndex = i;
					certain = true;
					break;
				}
			}
			if (!certain) {
				mCurrentStrategyIndex = random.nextInt(mStrategies.size());
			} 			
			mCurrentStrategyLossStreak = 0;
		}


		// get initial response
		Strategy s = mStrategies.get(mCurrentStrategyIndex);
		Item response = s.selectResponse();

		// ...and if it's not available, keep trying until we get one
		while (response == null) {

			boolean certain = false;
			for (int i = 0; i < mStrategies.size(); ++i) {
				if (mStrategies.get(i).certainty().compareTo(Certainty.GUESS) > 0 ) {
					mCurrentStrategyIndex = i;
					certain = true;
					break;
				}
			}
			if (!certain) {
				mCurrentStrategyIndex = (mCurrentStrategyIndex + 1) % mStrategies.size();
			} 

			s = mStrategies.get(mCurrentStrategyIndex);
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


	/**
	 * Clears the learning data (history) from the strategies
	 * */
	public void clearHistory() {
		for (Strategy s : mStrategies) {
			s.clearHistory();
		}		
	}

	/**
	 * Returns all the items player has given in a concatenated shorthand form.
	 * For example if player has given items Rock->Scissors->Paper->Paper this
	 * this will return "RSPP"
	 * 
	 * @returns players history
	 * */
	public String playerHistory() {
		return Strategy.playerHistory();
	}
	/**
	 * Returns all the items cpu has given in a concatenated shorthand form.
	 * For example if cpu has given items Rock->Scissors->Paper->Paper this
	 * this will return "RSPP"
	 * 
	 * @returns cpu history
	 * */
	public String cpuHistory() {
		return Strategy.cpuHistory();
	}


	/**
	 * Sets the learning data for player and cpu in concatenated shorthand form.
	 * @see playerHistory
	 * @see cpuHistory
	 * */
	public void setHistory(String playerHistory, String cpuHistory) {		
		Strategy.setPlayerHistory(playerHistory);
		Strategy.setCpuHistory(cpuHistory);
	}

	/**
	 * Initializes all strategies by calling initStrategy for them
	 * 
	 * @see Strategy
	 * */
	public void initStrategies() {

		for (Strategy s : mStrategies) {
			s.initStrategy();
		}

	}

}
