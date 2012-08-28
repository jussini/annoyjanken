package net.ankkatalo.janken;

/**
 * Class for storing name and Item each player (human or cpu) has
 * */
public class Player {

	private Item mItem;
	private String mName;

	public Player(String name, Item item) {
		mItem = item;
		setName(name);
	}

	public void setItem(Item item) {
		mItem = item;
	}

	public Item item() {
		return mItem;
	}

	public String name() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

}
