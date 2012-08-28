package net.ankkatalo.janken;

public abstract class Item {
	private String mName;
	private String mShortName;

	public enum ItemType {ROCK, PAPER, SCISSORS};

	public Item (String name, String shortName) {
		mName = name;
		mShortName = shortName;
	}

	/**
	 * @return long name for the item (eg. "rock", "paper", "scissors")
	 * */
	public String name() {
		return mName;
	}

	/**
	 * @return short name for the item (eg. "R", "P", "S")
	 * */
	public String shortName() {
		return mShortName;
	}

	/**
	 * Decides whether this item will beat the other
	 * @return JankenGame.WinType that tells whether this item BEATS, LOSES  or 
	 *         TIEs with the other
	 * */
	public abstract Game.WinType beats(Item other);


	public static class RockItem extends Item {		
		public RockItem() {
			super("Rock", "R");
		}

		public Game.WinType beats(Item other) {
			if (other.getClass() == Item.ScissorsItem.class) {
				return Game.WinType.BEATS;
			} else if (other.getClass() == this.getClass()) {
				return Game.WinType.TIE;
			}
			return Game.WinType.LOSES;
		}
	}

	public static class PaperItem extends Item {

		public PaperItem() {
			super("Paper", "P");
		}

		public Game.WinType beats(Item other) {
			if (other.getClass() == Item.RockItem.class) {
				return Game.WinType.BEATS;
			} else if (other.getClass() == this.getClass()) {
				return Game.WinType.TIE;
			}
			return Game.WinType.LOSES;
		}
	}

	public static class ScissorsItem extends Item {

		public ScissorsItem() {
			super("Scissors", "S");
		}

		public Game.WinType beats(Item other) {
			if (other.getClass() == Item.PaperItem.class) {
				return Game.WinType.BEATS;
			} else if (other.getClass() == this.getClass()) {
				return Game.WinType.TIE;
			}
			return Game.WinType.LOSES;
		}
	}
}


