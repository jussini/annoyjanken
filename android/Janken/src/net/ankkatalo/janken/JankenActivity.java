package net.ankkatalo.janken;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class JankenActivity extends Activity {
	
	private static JankenGame mGame = new JankenGame();
	
	private static JankenStats mStats = new JankenStats();
	
	private static String mJankenText = "Let's play!";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	TextView textView = (TextView) findViewById(R.id.textView);
    	textView.setText(mJankenText); 
        
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
    	
    	TextView textView = (TextView) findViewById(R.id.textView);
    	textView.setText(gameTextBuilder);
    	mJankenText = gameTextBuilder.toString();
        	
    }

}

