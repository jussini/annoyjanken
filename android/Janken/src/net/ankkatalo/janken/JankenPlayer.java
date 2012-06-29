package net.ankkatalo.janken;

public class JankenPlayer {

	private JankenItem mItem;
	private String mName;
	
	public JankenPlayer(String name, JankenItem item) {
		mItem = item;
		setName(name);
	}
	
	public void setItem(JankenItem item) {
		mItem = item;
	}
	
	public JankenItem item() {
		return mItem;
	}

	public String name() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

}
