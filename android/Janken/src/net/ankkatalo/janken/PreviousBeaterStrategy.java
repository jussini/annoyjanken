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
		
		// lets take a look at the last 4 rounds. if during the last four rounds
		// player has picked item that beats the previous response by cpu,
		// it looks quite a lot like a player strategy

		int featureSize = 4;
		
		// if there is not enough history, then tell we're only guessing
		if (mCpuHistory.length() < (featureSize + 1) || mPlayerHistory.length() < featureSize) {
			return Certainty.GUESS;
		}
		
		int cs = mCpuHistory.length() - (featureSize + 1);
		int ce = mCpuHistory.length()-1;
		String cpuPart = mCpuHistory.substring(cs, ce);
		
		int ps = mPlayerHistory.length() - featureSize;
		int pe = mPlayerHistory.length();
		String playerPart = mPlayerHistory.substring(ps, pe);

		// build a string which would counter the previous cpu items
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cpuPart.length(); ++i) {
			if (cpuPart.substring(i, i+1).equalsIgnoreCase("R")) {
				sb.append("P");
			} else if (cpuPart.substring(i, i+1).equalsIgnoreCase("P")) {
				sb.append("S");
			} else if (cpuPart.substring(i, i+1).equalsIgnoreCase("S")) {
				sb.append("R");
			} else {
				// oh my, we should not never get here, but if that happens
				// just bail quietly.
				// TODO: this is really not smart. if we get here, then the 
				// history may be broken and we could end up in very
				// awkward situations, which should not go silently
				return Certainty.GUESS;				
			}
		}
		
		// if the player string would counter previous selections, then 
		// we might have some certainty of player strategy
		if (sb.toString().equalsIgnoreCase(playerPart)) {
			System.out.println("PreviousBeater has some certainty");
			return Certainty.VERY;
		}
		
		// otherwise...
		return Certainty.GUESS;
	}

	@Override
	public void updateStrategy(Item playerItem, Item cpuItem) {
		// TODO Auto-generated method stub		
	}

	@Override
	public String name() {
		return "PreviousBeater";
	}

	
	
}
