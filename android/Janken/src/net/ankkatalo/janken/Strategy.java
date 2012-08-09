package net.ankkatalo.janken;

import java.util.ArrayList;
import java.util.List;

public abstract class Strategy {

	// sample space, what different symbols players choose from
	protected List<Item> mSampleSpace = new ArrayList<Item>();
	
	// history of user selections
	protected static String mPlayerHistory = "";
	// history of cpu selections
	protected static String mCpuHistory = "";

	
	public abstract void initStrategy();		
	
	public abstract Item selectResponse();
	
	public Strategy() {
		mSampleSpace.add(new Item.RockItem());
		mSampleSpace.add(new Item.PaperItem());
		mSampleSpace.add(new Item.ScissorsItem());
	}
		
	public abstract void updateStrategy(Item playerItem, Item cpuItem);
	
	public void clearHistory() {
		mPlayerHistory = "";
		initStrategy();
	}
	
	public static String playerHistory() {
		return mPlayerHistory;
	}
	
	public static String cpuHistory() {
		return mCpuHistory;
	}
	
	public static void setPlayerHistory(String playerHistory) {
		mPlayerHistory = playerHistory;
	}
	
	public static void setCpuHistory(String cpuHistory) {
		mCpuHistory = cpuHistory;
	}
}
