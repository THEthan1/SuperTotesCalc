package ethan.glick.supestotescalc;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class InstructionsActivity extends Activity implements OnClickListener{

	private String AudioFile = "Instructions.mp3";
	private MediaPlayer m_player;
	private AssetFileDescriptor afd;
	TextView instructView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions);
		
		m_player = new MediaPlayer();
		
		instructView = (TextView) findViewById(R.id.tvShowInstruction);
		instructView.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
getMenuInflater().inflate(R.menu.mymenu, menu);
		
		if (Music.isMuted())
			menu.getItem(1).setTitle("Unmute Music");
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.instructions)
		{
			Intent intent = new Intent(this, InstructionsActivity.class);
			startActivity(intent);
		}
		else if (id == R.id.mute) //forwards to instructions page
		{
			Music.swapMute();
			if (Music.isMuted())
			{
				m_player.pause();
				item.setTitle("Unmute Music");
			}
					
			else
			{
				m_player.start();
				item.setTitle("Mute Music");
			}
		}
		else if (id == R.id.help)
		{
			try 
			{
				String url = "tel:0527412235";
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
				startActivity(intent); 
			} 
			catch (ActivityNotFoundException e)
			{
				Toast.makeText(getBaseContext(), "Call Failed...", Toast.LENGTH_LONG).show();
		    }
		}
		else if (id == R.id.exit)
		{
			// start main activity
			Intent exitIntent = new Intent(this,HomeScreenActivity.class);
			exitIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(exitIntent);
			HomeScreenActivity.exitHandler.sendEmptyMessage(0);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    
	    if (!Music.isMuted())
	    	m_player.stop();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    
		 try //starts background music
		 {
				afd = getAssets().openFd(AudioFile);
				m_player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				m_player.setLooping(true);
				m_player.prepare();
		 } 
		 catch (Exception e) 
		 {
				e.printStackTrace();
		 }
	
		 if (!Music.isMuted())
			 m_player.start();
	}

	//Instructions:
	
	//calculator
	String CalcInstruct = "Input numbers and select operations to create an equation.\n" +
			       "Click '=' to calculate the result.\n" +
			       "If you select an additional operation before clicking the '=' sign, " +
			       "the previous equation will automatically be calculated.\n" +
			       "The '%' command takes its left operand, " +
			       "and shows its percentage reletive to its right operand as a result.\n" +
			       "The 'AC' button will clear the screen, revert the current number to 0, and reset the 'ans' value to 0.\n" +
			       "Pressing the 'C' button will just clear the screen.\n" +
			       "Pressing the 'Quadratic Equation' image will send " +
			       "you to the 'Quadratic Solver' page.\n" +
			       "Your input value may not excede 12 digits.\n" +
			       "Please Note: Attempting to divide a number by '0' will generate a Math Error.\n" +
			       "Press the 'Home' button to return to the home screen.";
	//quadratic equation solver
	String QuadInstruct = "Input the A, B, and C values of your quadratic equation.\n" +
				   "Press 'Calculate' to get the result.\n" +
				   "An error message will be displayed if you input " +
				   "invalid or impossible values.\n" +
				   "Press the arrow button next to one of the answers to forward that answer to the calculator.\n" +
				   "Press the '<-Return' button to return to the Calculator.\n" +
				   "Press the 'Home' button to return to the home screen.";
	//prime number generator
	String PrimesInstruct = "Select one of the options 'Output', 'Find', or 'Check', " +
							"and input a whole number into the text field.\n" +
							"'Output' - will output X amount of Primes, " +
							"where X is the number you input into the text field.\n" +
							"'Find' - will output the Xth Prime number, " +
							"where X is the number you input into the text field.\n" +
							"'Check' - will check if the number you input into the text " +
							"field is a Prime number and will output a fitting result.\n" +
							"Please Note: The larger the input, the longer the processing time.\n" +
							"Press the 'Home' button to return to the home screen.";
	//question game
	String GameInstruct = "Select either 'Generate New' or 'Generate Saved'.\n" +
						  "'Generate New' - will generate random equations.\n" +
						  "'Genarate Saved' - will output previously saved equations.\n" +
						  "Move the circle marked 'Difficulty' in order to select how difficult you " +
						  "would like the equations to be (1 - 5).\n" +
						  "Type your answers into the answer bar (where it says 'answer here...').\n" +
						  "Press the 'Check' button to check if your answer is correct or not.\n" +
						  "If the answer is correct, the equation will turn green.\n" +
						  "If the answer is incorrect, the equation will turn red.\n" +
						  "Please Note: You can change your answer, even after pressing the 'Check' button.\n" +
						  "Press the 'Next' button to move on to another equation.\n" +
						  "Press the 'Save' button to save the current equation to your device.\n" +
						  "This equation will now be available in 'Generate Saved' mode.\n" +
						  "Press the 'Edit' button to go to the 'Saved Editor' page to edit your saved equations " +
						  "(see 'Saved Editor' for more details). \n" +
						  "Press the 'Play Time Trial!' button to play the Time Trial Game.\n\n" +
						  "Time Trial Game\n" +
						  "---------------\n" +
						  "You will have 30 seconds to solve as many equations as you can.\n" +
						  "The amount of time you have left is located in the top left corner of the screen.\n" +
						  "Press the 'Start' button to start the clock and begin the game.\n" +
						  "Input your answers and press 'Next' until you run out of time.\n" +
						  "When the game is over, press the 'Continue' button to continue.\n" +
						  "Press the 'Home' button to return to the home screen.";
	//saved list editor
	String SavedEditor = "Tap the equation you wish to remove.\n" +
						 "When promted with the text box, select 'Yes' to remove the selected eqution " +
						 "from the list, or 'No' to cancel the request.\n" +
						 "Press the '<-Return' button to return to the Question Game.\n" +
						 "Press the 'Home' button to return to the home screen.";
	
	@Override
	public void onClick(View v)
	{
		
		instructView.scrollTo(0, 0); //reverts scroll view to starting position
		String instructions; //the instruction set to be shown
		switch (v.getId()) //sets String instructions to relevant set
		{
		case R.id.btCalculator:
			instructions = CalcInstruct;
				break;
		case R.id.btQ_Solver:
			instructions = QuadInstruct;
				break;
		case R.id.btPrimes:
			instructions = PrimesInstruct;
				break;
		case R.id.btQgame:
			instructions = GameInstruct;
				break;
		case R.id.btQsender:
			instructions = SavedEditor;
			break;
		default:
			instructions = "NO INSTRUCTION SELECTED!";
				break;
		}
		
		instructView.setVisibility(View.VISIBLE);
		instructView.setText(instructions); //outputs instruction to scrollable view
	}
	
	public void onClickHome(View v)
	{
		instructView.setVisibility(View.INVISIBLE);
		
		//returns to home screen
		Intent intent = new Intent(this, HomeScreenActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP); //closes the activity completely
		startActivity(intent);															 //this is necessary for the exit 
																						 //button to work properly
	}
}
