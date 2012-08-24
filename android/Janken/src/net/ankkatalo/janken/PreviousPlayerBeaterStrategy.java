package net.ankkatalo.janken;


/**
 * Strategy that assumes player will always give an item that would beat their
 * previous item and responds accordingly
 * */
public class PreviousPlayerBeaterStrategy extends Strategy {

	@Override
	public void initStrategy() {
		// TODO Auto-generated method stub
	}

	@Override
	public Item selectResponse() {

		
		// if we don't have any history, we can't make a choice.
		if (mPlayerHistory.length() < 1) {
			return null;
		} 
		
		String previous = mPlayerHistory.substring(mPlayerHistory.length() -1 );
				
		// for rock, assume player picks paper, so response with scissors
		if (previous.equalsIgnoreCase("R")) {
			return new Item.ScissorsItem();
		}
		
		// for paper, assume player picks scissors, so response with rock
		if (previous.equalsIgnoreCase("P")) {
			return new Item.RockItem();
		}
		
		// for scissors, assume player picks rock, so response with paper
		if (previous.equalsIgnoreCase("S")) {
			return new Item.PaperItem();
		}
		
		// yes, we should never get here, but just in case history has something
		// surprising, try to play it casual...
		return null;
		
	}

	@Override
	public Certainty certainty() {
		
		// if, for the past n rounds the player has at least 0.7*n times picked
		// an item that beats their last round selection, return SOME and for
		// every time, return VERY.
		
		int window = 4;
		if (mPlayerHistory.length() < window + 1) {
			return Certainty.GUESS;
		}
		
		int hits = 0;
		for (int i=0; i < window; ++i) {
			int prev_i = mPlayerHistory.length() - (window + 1) + i;
			String prev = mPlayerHistory.substring(prev_i, prev_i+1);
			String next = mPlayerHistory.substring(prev_i+1, prev_i + 2);

			if (prev.equalsIgnoreCase("R") && next.equalsIgnoreCase("P")) {
				++hits;
			} else if (prev.equalsIgnoreCase("P") && next.equalsIgnoreCase("S")) {
				++hits;
			} else if (prev.equalsIgnoreCase("S") && next.equalsIgnoreCase("R")) {
				++hits;
			} else {
				// no, we really should not get here. but we don't have real
				// error handling anyways, so just let it slip.							
			}			
		}
		
		if (hits == window) {
			return Certainty.VERY;
		} else if ((float)hits / (float)window > 0.7 ) {
			return Certainty.SOME;
		} 
		
		return Certainty.GUESS;
	}

	@Override
	public String name() {
		return "PreviousPlayerBeater";
	}

	@Override
	public void updateStrategy(Item playerItem, Item cpuItem) {
		// TODO Auto-generated method stub

	}

}
