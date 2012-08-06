package net.ankkatalo.janken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game {

	// sample space, what different symbols players choose from
	private List<Item> mSampleSpace = new ArrayList<Item>();
	
	// size of the window cpu player should use when determining user tendensies
	private int mWindowSize = 5;

	// hash map for easily seeking how many times, n, the sample queue
	// w1,w2..w_windowsize-1 has been followed by w_windowsize. Keys are in form
	// "w1w2w3..w_windosize"
	private HashMap<String, Integer> mFreqMap = new HashMap<String, Integer> ();

	// history of user selections
	private String mHistory = "";
	
	public enum WinType {BEATS, LOSES, TIE}
	
	
	public Game() {
		mSampleSpace.add(new Item.RockItem());
		mSampleSpace.add(new Item.PaperItem());
		mSampleSpace.add(new Item.ScissorsItem());
	}


	public void updateHistory(Item item) {
			mHistory = mHistory + item.shortName();		
	} 
	
	/**
	 * Decides what to use as a response. Works by trying to guess the best
	 * response based on player's previous activities.
	 * 
	 * */
	public Item selectResponse() {
		
		// used if we need to pick random response
		Random random = new Random();
		
		// on first rounds pick the response just randomly as there isn't enough
		// history data to make statistical choice
		if (mHistory.length() < mWindowSize) {
			System.out.println("Short history");
			int index = random.nextInt(mSampleSpace.size());
			return mSampleSpace.get(index);
		}
		
		// pick the last mWindowSize - 1 items from the history
		int featureStart = mHistory.length() - mWindowSize +1;
		String featureBase = mHistory.substring (featureStart);

	    String rockFeature = featureBase + "R";
	    String paperFeature = featureBase + "P";
	    String scissorsFeature = featureBase + "S";
		
	    // simple logic: pick the one response that wins the input player will
	    // statistically pick
	    Integer freqRock = 0; 
	    Integer freqPaper = 0;
	    Integer freqScissors = 0;

	    if (mFreqMap.containsKey(rockFeature)) {
	    	freqRock = mFreqMap.get(rockFeature);
	    }
	    if (mFreqMap.containsKey(paperFeature)) {
	    	freqPaper = mFreqMap.get(paperFeature);
	    }
	    if (mFreqMap.containsKey(scissorsFeature)) {
	    	freqScissors = mFreqMap.get(scissorsFeature);
	    }
	    
	    // if there is no statistic difference, pick response randomly
	    if (freqRock == freqPaper && freqPaper == freqScissors) {
	    	System.out.println(String.format("Equal chance %d %d %d", freqRock, freqPaper, freqScissors));
	    	int index = random.nextInt(mSampleSpace.size());
	    	return mSampleSpace.get(index);
	    }
	    
	    // by default assume user will give rock, so response with paper...
	    Item response = new Item.PaperItem();
	    int maxFreq = freqRock;
	    // ... but if the most probable is paper, response with scissors...
	    if (freqPaper > maxFreq) {
	    	maxFreq = freqPaper;
	    	response = new Item.ScissorsItem();	    	
	    }
	    // and lastly, if the most probable item is scissors, response with rock
		if (freqScissors > maxFreq) {
			response = new Item.RockItem();
    	}
	    
	    return response;		
	}
	
	
	public void updateFreqs() {
		
		// if we don't have enough history, there's nothing to update
		if (mHistory.length() < mWindowSize) {
			return; 
		}
		
		// otherwise, update freqs
		String feature = mHistory.substring(mHistory.length() - mWindowSize);
		System.out.println("Updating freqs with " + feature + " from " + mHistory);
		if (mFreqMap.containsKey(feature)) {
			mFreqMap.put(feature, mFreqMap.get(feature)+1);
		} else {
			mFreqMap.put(feature, 1);
		}
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
		mHistory = "";
		mFreqMap.clear();
	}
	
	public String history() {
		return mHistory;
	}
	
	public void setHistory(String history) {
		mHistory = history;
	}
	

	/**
	 * recreates the frequency table from a string of history. This is mainly
	 * meant for restoring application state
	 * 
	 * */
	public void rebuildFreqs() {
				
		// roll the history string		
		for (int i = 0; i < mHistory.length(); ++i) {

			// avoid out-indexing problems
			if (i+mWindowSize > mHistory.length()) {
				return; 
			}
			
			// pick the feature size of mWindowSize staring from current index
			String feature = mHistory.substring(i, i+mWindowSize);
			
			// otherwise add or update 
			if (mFreqMap.containsKey(feature)) {
				mFreqMap.put(feature, mFreqMap.get(feature)+1);
			} else {
				mFreqMap.put(feature, 1);
			}			
		}
		
	}
}
