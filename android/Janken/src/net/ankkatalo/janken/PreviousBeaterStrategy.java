package net.ankkatalo.janken;

public class PreviousBeaterStrategy extends Strategy {

	@Override
	public void initStrategy() {
		// TODO Auto-generated method stub
	}

	@Override
	/**
	 * selectResponse assumes player picks something that will beat the last
	 * response by cpu. therefore strategy will pick somehting that will beat
	 * that.
	 * */
	public Item selectResponse() {
		
		// if we don't have any history, we can't make a choice.
		if (mCpuHistory.length() < 1) {
			return null;
		} 
		
		String previousCpu = mCpuHistory.substring(mCpuHistory.length() -1 );
		// for rock, assume player picks paper, so response with scissors
		if (previousCpu.equalsIgnoreCase("R")) {
			return new Item.ScissorsItem();
		}
		
		// for paper, assume player picks scissors, so response with rock
		if (previousCpu.equalsIgnoreCase("P")) {
			return new Item.RockItem();
		}
		
		// for scissors, assume player picks rock, so response with paper
		if (previousCpu.equalsIgnoreCase("S")) {
			return new Item.PaperItem();
		}
		
		// yes, we should never get here, but just in case history has something
		// surprising...
		return null;
	}

	@Override
	public Certainty certainty() {
		// TODO Auto-generated method stub
		return Certainty.GUESS;
	}

	@Override
	public void updateStrategy(Item playerItem, Item cpuItem) {
		// TODO Auto-generated method stub		
	}

	
	
}
