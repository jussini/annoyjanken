package net.ankkatalo.janken;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class that all the Strategies must extend. Most importantly
 * will predict the best response (according to the implementing strategy) for 
 * the players next move. 
 * */
public abstract class Strategy {

	/**
	 * level of certainty a strategy thinks it can guess correctly
	 * GUESS   - strategy can pick a response but it thinks its very close to random
	 * SOME    - strategy can pick a response thats' probably slightly better
	 *           than a random guess
	 * VERY    - strategy can pick a response it's quite certain to be correct
	 * */
	public enum Certainty {GUESS, SOME, VERY}

	/**
	 * sample space, what different symbols players choose from
	 * */ 
	protected List<Item> mSampleSpace = new ArrayList<Item>();

	/** history of user selections */
	protected static String mPlayerHistory = "";

	/** history of cpu selections */
	protected static String mCpuHistory = "";

	/**
	 * Subclass specific method that sets the strategy ready to give responses
	 * */
	public abstract void initStrategy();		

	/**
	 * Returns the Item the implemented Strategy thinks is the best response or 
	 * null if Strategy can't give any.
	 * */
	public abstract Item selectResponse();

	/**
	 * Returns the level of certainty the implemented Strategy thinks its
	 * response will be correct
	 * */
	public abstract Certainty certainty();

	/**
	 * Name of the strategy, eg. "Random" or "PreviousPlayerBeater"
	 * */
	public abstract String name();

	public Strategy() {
		mSampleSpace.add(new Item.RockItem());
		mSampleSpace.add(new Item.PaperItem());
		mSampleSpace.add(new Item.ScissorsItem());
	}

	/**
	 * Takes the last items player and cpu have given so that it can give a
	 * good response for next round
	 * */
	public abstract void updateStrategy(Item playerItem, Item cpuItem);

	/**
	 * Clears the player and cpu history, nullifying all learning data
	 * */
	public void clearHistory() {
		mPlayerHistory = "";
		mCpuHistory = "";
		initStrategy();
	}

	/**
	 * Returns the player history in a concatenated shorthand form
	 * */
	public static String playerHistory() {
		return mPlayerHistory;
	}

	/**
	 * Returns the cpu history in a concatenated shorthand form
	 * */
	public static String cpuHistory() {
		return mCpuHistory;
	}

	/**
	 * sets the player history
	 * @param playerHistory player history in a concatenated shorthand form
	 * */
	public static void setPlayerHistory(String playerHistory) {
		mPlayerHistory = playerHistory;
	}

	/**
	 * sets the cpu history
	 * @param cpuHistory cpu history in a concatenated shorthand form
	 * */
	public static void setCpuHistory(String cpuHistory) {
		mCpuHistory = cpuHistory;
	}
}
