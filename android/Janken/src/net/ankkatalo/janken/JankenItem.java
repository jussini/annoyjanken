package net.ankkatalo.janken;

public abstract class JankenItem {
	private String mName;
	private String mShortName;
	
	public enum ItemType {ROCK, PAPER, SCISSORS};
	
	public JankenItem (String name, String shortName) {
		mName = name;
		mShortName = shortName;
	}
	
	public String name() {
		return mName;
	}
	
	public String shortName() {
		return mShortName;
	}
	
	public abstract JankenGame.WinType beats(JankenItem other);
	
	
	public static class RockItem extends JankenItem {		
		public RockItem() {
			super("Rock", "R");
		}
		
		public JankenGame.WinType beats(JankenItem other) {
			if (other.getClass() == JankenItem.ScissorsItem.class) {
				return JankenGame.WinType.BEATS;
			} else if (other.getClass() == this.getClass()) {
				return JankenGame.WinType.TIE;
			}
			return JankenGame.WinType.LOSES;
		}
	}
	
	public static class PaperItem extends JankenItem {
		
		public PaperItem() {
			super("Paper", "P");
		}
		
		public JankenGame.WinType beats(JankenItem other) {
			if (other.getClass() == JankenItem.RockItem.class) {
				return JankenGame.WinType.BEATS;
			} else if (other.getClass() == this.getClass()) {
				return JankenGame.WinType.TIE;
			}
			return JankenGame.WinType.LOSES;
		}
	}
	
	public static class ScissorsItem extends JankenItem {
		
		public ScissorsItem() {
			super("Scissors", "S");
		}
		
		public JankenGame.WinType beats(JankenItem other) {
			if (other.getClass() == JankenItem.PaperItem.class) {
				return JankenGame.WinType.BEATS;
			} else if (other.getClass() == this.getClass()) {
				return JankenGame.WinType.TIE;
			}
			return JankenGame.WinType.LOSES;
		}
	}
}


