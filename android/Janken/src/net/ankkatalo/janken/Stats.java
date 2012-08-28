package net.ankkatalo.janken;

/**
 * Class responsible for storing game stats (wins/loses/ties)
 * */
public class Stats {

	// game statistics
	private int mCPUWins = 0;
	private int mPlayerWins = 0;
	private int mTies = 0;

	public int totalGames() {
		return mCPUWins + mPlayerWins + mTies;
	}

	public void setCPUWins(int wins) {
		mCPUWins = wins;
	}

	public void addCPUWins() {
		mCPUWins++;
	}

	public int CPUWins() {
		return mCPUWins;
	}

	public float CPUPercentage() {
		return (float) (mCPUWins * 100.0 / ((float)(totalGames()) + 0.00001));
	}

	public void setPlayerWins(int wins) {
		mPlayerWins = wins;
	}

	public void addPlayerWins() {
		mPlayerWins++;
	}

	public int playerWins() {
		return mPlayerWins;
	}

	public float playerPercentage() {
		return (float) (mPlayerWins * 100.0 / ((float)(totalGames()) + 0.00001));
	}

	public void setTies(int ties) {
		mTies = ties;
	}

	public void addTies() {
		mTies++;
	}

	public int ties() {
		return mTies;
	}

	public float tiePercentage() {
		return (float) (mTies * 100.0 / ((float)(totalGames()) + 0.00001));
	}

	public void clearStats() {
		mCPUWins = 0;
		mPlayerWins = 0;
		mTies = 0;		
	}
}
