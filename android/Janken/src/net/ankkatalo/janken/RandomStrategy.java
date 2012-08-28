package net.ankkatalo.janken;

import java.util.Random;

/**
 * Strategy that will return with an random item.
 * */
public class RandomStrategy extends Strategy {


	public RandomStrategy() {
		super();
	}

	@Override
	public void initStrategy() {
		// NOP
	}

	@Override
	/**
	 * Picks a response by a random
	 * */
	public Item selectResponse() {

		Random random = new Random();

		int index = random.nextInt(mSampleSpace.size());
		return mSampleSpace.get(index);

	}

	@Override
	public void updateStrategy(Item playerItem, Item cpuItem) {
		// TODO Auto-generated method stub		
	}

	@Override
	public Certainty certainty() {
		return Certainty.GUESS;
	}

	@Override
	public String name() {
		return "Random";
	}

}

