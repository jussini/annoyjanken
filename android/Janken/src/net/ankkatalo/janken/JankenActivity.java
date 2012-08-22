package net.ankkatalo.janken;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class JankenActivity extends Activity {
	
	/** Game object responsible of selecting response and picking the winner */
	private static Game mGame = new Game();
	
	/** to keep count of wins and losses*/
	private static Stats mStats = new Stats();
		
	private static int mPlayerGraphic = R.drawable.unknown;
	private static int mCpuGraphic = R.drawable.unknown;
	
	private final int STATS_DIALOG = 1;
	private final int CLEAR_HISTORY_ALERT = 2;
	private final int CLEAR_STATS_ALERT = 3;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        
    	String history = prefs.getString("History", "");
    	mGame.setHistory(history, "");
    	mGame.initStrategies();
    	    	    	
    	int cpuWins = prefs.getInt("CpuWins",0);
    	mStats.setCPUWins(cpuWins);
    	
    	int playerWins = prefs.getInt("PlayerWins", 0);
    	mStats.setPlayerWins(playerWins);
    	
    	int ties = prefs.getInt("Ties", 0);
    	mStats.setTies(ties);
        
    	mPlayerGraphic = prefs.getInt("PlayerGraphic", R.drawable.unknown);    	
    	ImageView playerView = (ImageView) findViewById(R.id.playerImageView);    	    	
    	playerView.setImageResource(mPlayerGraphic);

    	mCpuGraphic = prefs.getInt("CpuGraphic", R.drawable.unknown);    	
    	ImageView cpuView = (ImageView) findViewById(R.id.cpuImageView);
    	cpuView.setImageResource(mCpuGraphic);   	
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
  	  	ed.putString("History", mGame.playerHistory());
  	  	ed.putInt("CpuWins", mStats.CPUWins());
  	  	ed.putInt("PlayerWins", mStats.playerWins());
  	  	ed.putInt("Ties", mStats.ties());
  	  	ed.putInt("PlayerGraphic", mPlayerGraphic);
  	  	ed.putInt("CpuGraphic", mCpuGraphic);
        ed.commit();
    }
    
    
    public void onRockClicked(View view) {
    	onClicked(new Item.RockItem(), view);
    }
    
    public void onPaperClicked(View view) {
    	onClicked(new Item.PaperItem(), view);
    }
    
    public void onScissorsClicked(View view) {
    	onClicked(new Item.ScissorsItem(), view);
    }
    
    private void onClicked(Item item, View view) {
    	// get the response first
    	Item response = mGame.selectResponse();
    	
    	// update strategy based on player selection and the response from cpu
    	mGame.updateStrategies(item, response);    

    	// pick the winner
    	Player player = new Player("Player 1", item);
    	Player cpu    = new Player("CPU", response);
    	Player winner = mGame.selectWinner(player, cpu);
    	
    	// update correct graphics
    	int playerResource = getGraphics(player, cpu);
    	int cpuResource = getGraphics(cpu, player);
    	
    	ImageView playerView = (ImageView) findViewById(R.id.playerImageView);
    	playerView.setImageResource(playerResource);
    	mPlayerGraphic = playerResource;
    	ImageView cpuView = (ImageView) findViewById(R.id.cpuImageView);
    	cpuView.setImageResource(cpuResource);
    	mCpuGraphic = cpuResource;
    	
    	if (winner == null) {
    		mStats.addTies();    	    		
    	}
    	else if (winner == player) {
    		mStats.addPlayerWins();
    	} else if (winner == cpu) {
    		mStats.addCPUWins();
    	}     
                	
    }

    /**
     * returns the correct graphic resource for player1.
     * */
    private int getGraphics(Player player1, Player player2) {

    	Player winner = mGame.selectWinner(player1, player2);
    	    	
    	if (winner == null) {
    		if (player1.item().shortName().equals("R")) {
    			return R.drawable.rock_tie;
    		} else if(player1.item().shortName().equals("P")) {
    			return R.drawable.paper_tie;
    		} else {    			
    			return R.drawable.scissors_tie;
    		}
    	} 
    	else if (winner == player1 ) {
    		if (player1.item().shortName().equals("R")) {
    			return R.drawable.rock_win;
    		} else if(player1.item().shortName().equals("P")) {
    			return R.drawable.paper_win;
    		} else {
    			return R.drawable.scissors_win;
    		}
    	} else {
    		if (player1.item().shortName().equals("R")) {
    			return R.drawable.rock_lose;
    		} else if(player1.item().shortName().equals("P")) {
    			return R.drawable.paper_lose;
    		} else {
    			return R.drawable.scissors_lose;
    		}
    	}
    	
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
            case R.id.showStatsDialogItem:
            	showDialog(STATS_DIALOG);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
        
	
	protected Dialog onCreateDialog (int id, Bundle args) {
		
		switch(id) {
		case STATS_DIALOG:
			return getStatsDialog();
		case CLEAR_HISTORY_ALERT:
			return getClearHistoryDialog();
		case CLEAR_STATS_ALERT:
			return getClearStatsDialog();
		default:
			// what else could we do?
			return null;
		}
			
	}

	protected void onPrepareDialog (int id, Dialog dialog, Bundle args) {
		if (id == STATS_DIALOG) {

			// stats may update between every call of showdialog, so we should
			// update the dialog text here

	    	TextView text = (TextView) dialog.findViewById(R.id.totalValueField);
	    	text.setText(String.format("%d", mStats.totalGames()));
	    	text = (TextView) dialog.findViewById(R.id.playerValueField);
	    	text.setText(String.format("%d (%d%%)", 
	    			mStats.playerWins(), 
	    			(int)mStats.playerPercentage()));

	    	text = (TextView) dialog.findViewById(R.id.cpuValueField);
	    	text.setText(String.format("%d (%d%%)", 
	    			mStats.CPUWins(), 
	    			(int)mStats.CPUPercentage()));
	    		    	
	    	text = (TextView) dialog.findViewById(R.id.tiesValueField);
	    	text.setText(String.format("%d (%d%%)", 
	    			mStats.ties(), 
	    			(int)mStats.tiePercentage()));

	    	text = (TextView) dialog.findViewById(R.id.dataSizeValueField);
	    	text.setText(String.format("%d games", 
	    			mGame.playerHistory().length()));
	    	
		}
	}
	
	
    public Dialog getStatsDialog() {
    	// weird, docs say we should use getApplicationContext() here, but
    	// that ends up with BadTokenException O_o
    	// thanks, stackoverflow, for this hint.
    	Dialog dialog = new Dialog(this);

    	// title and image won't change between the occasions we show the dialog
    	// so we can set them here
    	dialog.setContentView(R.layout.stats_dialog);
    	dialog.setTitle(R.string.statsString);
    	ImageView image = (ImageView) dialog.findViewById(R.id.stats_dialog_image);
    	image.setImageResource(R.drawable.ic_launcher);
    	
    	return dialog;
	}

	
    public Dialog getClearHistoryDialog() {
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("This will erase all learned data. \nAre you sure you want to do this?")
    		   .setTitle("Clear History?")
    	       //.setCancelable(false)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                JankenActivity.this.clearHistoryConfirmed();
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	
    	AlertDialog alert = builder.create();
    	
    	return alert;        
    }

    
    public Dialog getClearStatsDialog() {
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("This will erase Wins/Loses statistics. \nAre you sure you want to do this?")
    		   .setTitle("Clear Stats?")
    	       //.setCancelable(false)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                JankenActivity.this.clearStatsConfirmed();
    	           }
    	       })
    	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	
    	AlertDialog alert = builder.create();
    	
    	return alert;        
    }
    
	
    public void clearStats() {
    	showDialog(CLEAR_STATS_ALERT);
    }
    
	protected void clearStatsConfirmed() {
    	mStats.clearStats();
    	
    	//Context context = getApplicationContext();
    	CharSequence text = "Win/Lose stats cleared!";
    	int duration = Toast.LENGTH_SHORT;

    	Toast toast = Toast.makeText(this, text, duration);
    	toast.show();    
    }

	
	public void clearHistory() {    	
    	showDialog(CLEAR_HISTORY_ALERT);    	    
    }

	
	protected void clearHistoryConfirmed() {
		
	   	mGame.clearHistory();
    	//Context context = getApplicationContext();
    	CharSequence text = "Learned data cleared!";
    	int duration = Toast.LENGTH_SHORT;

    	Toast toast = Toast.makeText(this, text, duration);
    	toast.show();

		
	}
    
}

