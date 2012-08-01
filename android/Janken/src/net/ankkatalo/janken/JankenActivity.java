package net.ankkatalo.janken;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class JankenActivity extends Activity {
	
	/** Game object responsible of selecting response and picking the winner */
	private static JankenGame mGame = new JankenGame();
	
	/** to keep count of wins and losses*/
	private static JankenStats mStats = new JankenStats();
	
	/** Text that will be always shown on the R.id.textView, should only be
	 * changed using updateJankenText(String text) */
	private static String mJankenText = "Let's play!";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        
    	String history = prefs.getString("History", "");
    	mGame.setHistory(history);
    	mGame.rebuildFreqs();
    	
    	String jankenText = prefs.getString("GameText", "Let's play!");
    	updateJankenText (jankenText);
    	    	
    	int cpuWins = prefs.getInt("CpuWins",0);
    	mStats.setCPUWins(cpuWins);
    	
    	int playerWins = prefs.getInt("PlayerWins", 0);
    	mStats.setPlayerWins(playerWins);
    	
    	int ties = prefs.getInt("Ties", 0);
    	mStats.setTies(ties);
                
    }
    
    /** called either when activity is created (api 14+) or 
     * when menu button has been pressed (pre api 14)*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        SharedPreferences.Editor ed = getPreferences(MODE_PRIVATE).edit();
  	  	ed.putString("History", mGame.history());
  	  	ed.putString("GameText", mJankenText);
  	  	ed.putInt("CpuWins", mStats.CPUWins());
  	  	ed.putInt("PlayerWins", mStats.playerWins());
  	  	ed.putInt("Ties", mStats.ties());
        ed.commit();
    }
    
    
    public void onRockClicked(View view) {
    	onClicked(new JankenItem.RockItem(), view);
    }
    
    public void onPaperClicked(View view) {
    	onClicked(new JankenItem.PaperItem(), view);
    }
    
    public void onScissorsClicked(View view) {
    	onClicked(new JankenItem.ScissorsItem(), view);
    }
    
    private void onClicked(JankenItem item, View view) {
    	// get the response first
    	JankenItem response = mGame.selectResponse();
    	
    	// update history
    	mGame.updateHistory(item);
    	
    	// update freqs
    	mGame.updateFreqs();
    	
    	JankenPlayer player = new JankenPlayer("Player 1", item);
    	JankenPlayer cpu    = new JankenPlayer("CPU", response);
    	JankenPlayer winner = mGame.selectWinner(player, cpu);

    	if (winner == null) {
    		mStats.addTies();
    	}
    	else if (winner == player) {
    		mStats.addPlayerWins();
    	} else if (winner == cpu) {
    		mStats.addCPUWins();
    	} 
    	    	
    	StringBuilder gameTextBuilder = new StringBuilder();
		gameTextBuilder.append("Player: " + player.item().name());
		gameTextBuilder.append(" vs CPU: " + cpu.item().name() + ":\n");
    	if (winner != null) {
    		gameTextBuilder.append(winner.name() + " Wins!");
    	} else {
    		gameTextBuilder.append("It's a tie");
    	}
    	
    	gameTextBuilder.append("\n\n\n\n");
    	gameTextBuilder.append(String.format("Total Games: %d\n", mStats.totalGames()));
    	gameTextBuilder.append(String.format("Player Wins: %d (%d%%)\n", 
    			mStats.playerWins(), 
    			(int)mStats.playerPercentage()));
    	gameTextBuilder.append(String.format("CPU Wins: %d (%d%%)\n", 
    			mStats.CPUWins(), 
    			(int)mStats.CPUPercentage()));
    	gameTextBuilder.append(String.format("Ties: %d (%d%%)\n", 
    			mStats.ties(), 
    			(int)mStats.tiePercentage()));

    	
    	updateJankenText(gameTextBuilder.toString());        	
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clearStatsItem:
                clearStats();
                return true;
            case R.id.clearHistoryItem:
            	clearHistory();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    public void clearStats() {
    	mStats.clearStats();
    	updateJankenText("Stats cleared!");    	
    }
    
    
    public void clearHistory() {
    	mGame.clearHistory();
    	updateJankenText("History cleared!");  	
    }
    
    
    public void updateJankenText(String text) {
    	mJankenText = text;
    	TextView textView = (TextView) findViewById(R.id.textView);
    	textView.setText(mJankenText);
    }
}

