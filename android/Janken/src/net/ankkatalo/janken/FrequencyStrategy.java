package net.ankkatalo.janken;

import java.util.HashMap;

public class FrequencyStrategy extends Strategy {

	// size of the window cpu player should use when determining user tendensies
	private int mWindowSize = 5;

	// hash map for easily seeking how many times, n, the sample queue
	// w1,w2..w_windowsize-1 has been followed by w_windowsize. Keys are in form
	// "w1w2w3..w_windosize"
	private HashMap<String, Integer> mFreqMap = new HashMap<String, Integer> ();
	
	public FrequencyStrategy() {
		super();
	}
	
	@Override
	public void initStrategy() {
		rebuildFreqs();
	}
	
	@Override
	public Item selectResponse() {
		
		// on first rounds, we just can't pick a response
		if (mPlayerHistory.length() < mWindowSize) {
			return null;
		}
		
		// pick the last mWindowSize - 1 items from the history
		int featureStart = mPlayerHistory.length() - mWindowSize +1;
		String featureBase = mPlayerHistory.substring (featureStart);

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
	    
	    // if there is no statistic difference, don't return anything
	    if (freqRock == freqPaper && freqPaper == freqScissors) {
	    	//System.out.println(String.format("Equal chance %d %d %d", freqRock, freqPaper, freqScissors));
	    	return null;
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
		if (mPlayerHistory.length() < mWindowSize) {
			return; 
		}
		
		// otherwise, update freqs
		String feature = mPlayerHistory.substring(mPlayerHistory.length() - mWindowSize);
		//System.out.println("Updating freqs with " + feature + " from " + mPlayerHistory);
		if (mFreqMap.containsKey(feature)) {
			mFreqMap.put(feature, mFreqMap.get(feature)+1);
		} else {
			mFreqMap.put(feature, 1);
		}
	}
	
	@Override
	public void updateStrategy(Item playerItem, Item cpuItem) {
		updateFreqs();
	}
	
	/**
	 * recreates the frequency table from a string of history. This is mainly
	 * meant for restoring application state
	 * 
	 * */
	public void rebuildFreqs() {
				
		// roll the history string		
		for (int i = 0; i < mPlayerHistory.length(); ++i) {

			// avoid out-indexing problems
			if (i+mWindowSize > mPlayerHistory.length()) {
				return; 
			}
			
			// pick the feature size of mWindowSize staring from current index
			String feature = mPlayerHistory.substring(i, i+mWindowSize);
			
			// otherwise add or update 
			if (mFreqMap.containsKey(feature)) {
				mFreqMap.put(feature, mFreqMap.get(feature)+1);
			} else {
				mFreqMap.put(feature, 1);
			}			
		}
		
	}

	@Override
	public Certainty certainty() {
		return Certainty.GUESS;
	}

	@Override
	public String name() {
		return "Frequency";
	}
	
}
