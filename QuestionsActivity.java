package ethan.glick.supestotescalc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
 
public class QuestionsActivity extends Activity implements OnSeekBarChangeListener{

	private boolean genNew;
	private Integer difficulty;
	private int correctAns;
	private int Correct;
	private boolean inPlay;
	private int readIndex;
	private int CounterTime;
	private final String saveFile = "SavedQuestions"; //text file where saved questions are stored
	//text files for storing the high-score for each difficulty level
	private final String highScoreFile1 = "highScore1";
	private final String highScoreFile2 = "highScore2";
	private final String highScoreFile3 = "highScore3";
	private final String highScoreFile4 = "highScore4";
	private final String highScoreFile5 = "highScore5";
	private CounterClass timeKeeper;
	final int red = Color.rgb(255, 0, 0);
	final int black = Color.rgb(0, 0, 0);
	final int orange = Color.rgb(255, 134, 0);
	final int yellow = Color.rgb(255, 255, 0);
	final int green = Color.rgb(64, 207, 46);
	private MediaPlayer m_player; //background music player
	//sound effects players
	private MediaPlayer m_player1;
	private MediaPlayer m_player2;
	private MediaPlayer m_player3;
	private MediaPlayer m_player4;
	private AssetFileDescriptor afd;
	private AssetFileDescriptor afd1;
	private AssetFileDescriptor afd2;
	private AssetFileDescriptor afd3;
	private AssetFileDescriptor afd4;
	private String AudioFile = "Questions.mp3";
	private String FirstAudioFile = "Ticking.mp3";
	private String SecondAudioFile = "Pounding.mp3";
	private String ThirdAudioFile = "Ping.mp3";
	private String HighScoreClapping = "Applause.mp3";
	
	TextView time;
	TextView timer;
	Button check;
	RadioGroup generate;
	TextView ShowDifficulty;
	TextView difficulty_view;
	SeekBar seekBar; //for adjusting difficulty
	Button send;
	Button play;
	TextView question;
	Button next;
	EditText input;
	TextView ShowCorrect;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_questions);
		
		new File(saveFile);
		new File(highScoreFile1);
		new File(highScoreFile2);
		new File(highScoreFile3);
		new File(highScoreFile4);
		new File(highScoreFile5);
		
		ShowCorrect = (TextView) findViewById(R.id.tvCorrect);
		ShowCorrect.setText("Correct: 0");
		ShowCorrect.setTextColor(black);
		ShowCorrect.setVisibility(View.INVISIBLE);
		
		time = (TextView) findViewById(R.id.tvTime);
		check = (Button) findViewById(R.id.btCheck);
		generate = (RadioGroup) findViewById(R.id.rgGenerate);
		ShowDifficulty = (TextView) findViewById(R.id.tvShowDifficulty);	
		difficulty_view = (TextView) findViewById(R.id.tvDifficulty);
		seekBar = (SeekBar) findViewById(R.id.difSelect);
		send = (Button) findViewById(R.id.btSend);
		play = (Button) findViewById(R.id.btPlay);
		question = (TextView) findViewById(R.id.tvQuestion);
		next = (Button) findViewById(R.id.btNext);
		input = (EditText) findViewById(R.id.etAnswer);
		timer = (TextView) findViewById(R.id.tvTimer);
		
		inPlay = false; //if time trial is in effect or not
		genNew = true; //if user wishes to generate random questions or use saved ones
		difficulty = 1;
		correctAns = 0;
		Correct = 0;
		readIndex = 0;
		CounterTime = 30500; //30 seconds for the time trial (extra half second added for better visual)
		timeKeeper = new CounterClass(CounterTime, 1000);
		
		m_player = new MediaPlayer();
		m_player.setLooping(true);

		m_player1 = new MediaPlayer();
		m_player2 = new MediaPlayer();
		m_player3 = new MediaPlayer();
		m_player4 = new MediaPlayer();
		
		String str = generateNew(); //generate and output random equation
		calculate(str); //calculate the correct answer to the generated question
		
		seekBar.setOnSeekBarChangeListener(this);
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
	    	m_player.pause(); //pause background audio
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    
		 try //resume background audio
		 {
			afd = getAssets().openFd(AudioFile);
			m_player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			m_player.prepare();
		 } 
		 catch (Exception e)
		 {
			e.printStackTrace();
		 }
			
		 if (!Music.isMuted())
			 m_player.start();
	}
	
	@Override
	public void onStop() {
	    super.onStop();
	    //stops all audio that might be playing
	    if (m_player1.isPlaying()) m_player.stop();
	    if (m_player1.isPlaying()) m_player1.stop();
		else if (m_player2.isPlaying()) m_player2.stop();
		else if (m_player3.isPlaying()) m_player3.stop();
		else if (m_player4.isPlaying()) m_player4.stop();

		timeKeeper.cancel(); //stops timer  
	}
	
	
	public void onRadioClick(View v) //when radio button selection is made
	{
		
		if (v.getId() == (R.id.rbSaved)) //if the player selected to use saved questions
		{	
			genNew = false;
			difficulty_view.setVisibility(View.INVISIBLE);
			ShowDifficulty.setVisibility(View.INVISIBLE);
			seekBar.setVisibility(View.INVISIBLE);
			String str = generateSaved(); //gets questions from saved list
			if (str != null) //in case list is empty
					calculate(str); //calculates correct answer
		}
		else //if user wishes to have randomly generated questions
		{
			genNew = true;
			difficulty_view.setVisibility(View.VISIBLE);
			ShowDifficulty.setVisibility(View.VISIBLE);
			seekBar.setVisibility(View.VISIBLE);
			String str = generateNew(); //gets randomly generated question
			calculate(str); //calculates correct answer
		}
		
		input.setText(null); //clears input box
	}
	
	public void onNext(View v) //when user clicks the 'Next' button (text can change depending on occasion)
	{
		Button btn = (Button) v;
		
		if (btn.getText() == "Start") //on the occasion that the player is in the time trial and wishes to start
		{
			btn.setText("Next");
			timeKeeper.start(); //starts timer
			m_player1.start(); //starts ticking noise
			question.setVisibility(View.VISIBLE);
		}
		else if (inPlay) //on occasion that the player is in the middle for the time trial game
		{
			boolean correct = check(); //checks if answer is correct
			
			if (correct)
			{	//increments correct counter and shows new score on screen
				++Correct;
				ShowCorrect.setText("Correct: " + Correct);
			}
		}
		else if (btn.getText() == "Continue") //on occasion that user has finished the time trial game
		{
			btn.setText("Next");
			
			endPlay(); //returns screen to original format
		}
		
		String str; 
		question.setTextColor(black); //returns questions text color to black 
									  //(in case it was changes by clicking the 'check' button)
		if (genNew) str = generateNew(); //generates new random question (if user selected Generate Saved)
		else str = generateSaved(); //otherwise, outputs question from saved list
		
		if (str != null) 
				calculate(str); //calculates correct answer
		
		input.setText(null); //clears input/answer box
	}
	
	private String generateSaved()
	{
		String equation = null;
		String[] fields = null;
    	String strLine = null;
    	
    	
    	try {
    		//opens save file
			FileInputStream fis = openFileInput(saveFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
		
			strLine  = reader.readLine(); //reads questions from text file as one long string
			
			if (strLine != null) //if the file wasn't empty
			{
			    fields = strLine.split(";"); //separates long questions string into individual questions
			   
				equation = fields[readIndex]; //selects question at current index
			}
			else
			{	//tell user that the file was empty (if it is)
				Toast.makeText(getBaseContext(), "You have no saved equations...", Toast.LENGTH_SHORT).show();
			}
			//closes save file
			reader.close();
			isr.close();
			fis.close();
					
			question.setText(equation); //outputs question
		
			if (readIndex < (fields.length - 1)) ++readIndex; //if user has not gone through all the questions yet
															  //increases index for question reader
			else readIndex = 0; //otherwise, starts index from the beginning
			
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    	
    	return equation; //returns the new question for calculation
	}
	
	private String generateNew()
	{
		String equation = "";
		int vars; //number of operands
		int ops; //number of of possible operators
		int max; //maxim operand value
		int option; //number of the question possibility (2 types, only apples to certain difficulties)
		Random rand = new Random(); //starts up random number generator
		option = rand.nextInt(2); //determines which of the two possibilities will be used (1 or 2)
		
		switch (difficulty) //prepares bases for question based on difficulty
		{
		//level 1
		case 1:	vars = 2; 
				ops = 2;
				max = 10;
					break;
		//level 2
		case 2:	if (option == 1) //option 1
				{
					vars = 2;
					ops = 2;
					max = 100;
				}
				else //option 2
				{
					vars = 3;
					ops = 2;
					max = 10;
				}
					break;
		//level 3
		case 3:	vars = 2;
				ops = 3;
				max = 10;
					break;
		//level 4
		case 4:	if (option == 1) //option 1
				{
					vars = 3;
					ops = 3;
					max = 10;
				}
				else //option 2
				{
					vars = 2;
					ops = 3;
					max = 100;
				}
					break;
		//level 5
		case 5:	vars = 3;
				ops = 3;
				max = 100;
					break;
		default: //defaults to level 1
			vars = 2;
			ops = 2;
			max = 10;
				break;
		}
		
		for (int i = 1; i < vars; ++i) //creates an equation by putting a random number (between 0 and the max number)
		{							   //followed by a random operator 
					      			   //this may be done numerous times in succession, depending on the difficulty setting
			equation += Integer.toString(1 + rand.nextInt(max)) + getOperation(ops);
		}
		
		equation += Integer.toString(1 + rand.nextInt(max)); //puts random number as final operand in equation
		
		question.setText(equation); //outputs the equation
		
		return equation; //returns the new equation for calculation
	}
	
	private char getOperation(int ops)
	{//returns a random operator from 2 or 3 possibilities (depending on difficulty) 
		Random rand = new Random(); 
		int select = rand.nextInt(ops); //gets random number between 0 and the value previously assigned to options
		switch(select)
		{
		case 0: //available for level 1 and above
			return '+';
		case 1: //available for level 1 and above
			return '-';
		case 2: //available for level 3 and above
			return 'x';
		default: 
			return '+';
		}
	}
	
	//uses algorithm to determine answer to randomly generated equations
	private void calculate(String equation)
	{
		int ans = 0;
		String currentInt = "";
		int index = 0;
		correctAns = 0;
		
		//figures out the needed length of the arrays based on the difficulty of the equation.
		//(needed for generate from saved, where current difficulty could be different than question difficulty)
		int size = 0;
		for (int i = 0; i < equation.length(); ++i)
		{
			if (equation.charAt(i) == '+' || equation.charAt(i) == 'x' || equation.charAt(i) == '-')
				++size;
		}
		int[] nums = new int[(size + 1)]; //array of operands
		char[] ops = new char[(size)]; //array of operators
		
		
		for (int i = 0; i < equation.length(); ++i)
		{
			//if current position is an operator
			if (equation.charAt(i) == '+' || equation.charAt(i) == 'x' || equation.charAt(i) == '-') 
			{
				//if operator is '-'
				if (equation.charAt(i) == '-')
				{
					//puts current operand into the operand array, but as a negative
					nums[index] = Integer.parseInt(currentInt);
					//puts '+' operator into operator index
					//since adding a negative number is the same as subtracting
					ops[index] = '+';
				}
				
				else
				{
					//puts current operand into the operand array
					nums[index] = Integer.parseInt(currentInt);
					//puts current operator into the operator array
					ops[index] = equation.charAt(i);

					++i;
				}
				
				currentInt = "";
				++index;
			}								
			//gets operand value one digit at a time
			currentInt += equation.charAt(i);
		}
		//puts final operand into operand array
		nums[index] =  Integer.parseInt(currentInt);
		
		for (int i = 0; i < ops.length; ++i) //does multiplication operations first
		{
			if (ops[i] == 'x') //at array positions where the operator is 'x'
			{
				nums[i+1] *= nums[i]; //does multiplication on appropriate operands, 
				                      //then puts result back into operand array for adding in the future
				nums[i] = 0; //puts 0 in other operand position so it wont effect addition sum
			}
		}
		
		for (int i = 0; i < nums.length; ++i) //then does addition operations
		{
				ans += nums[i];	//adds together all values in operand array and stores it in int ans
								//this value is the correct answer to the equation
		}
		
		correctAns = ans; //sets the sum stored in int ans as being the correct answer to the equation
	}
	
	public void onClickHome(View v) //returns to home screen
	{
		Intent intent = new Intent(this, HomeScreenActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
	
	public void onCheck(View v) //when the 'Check' button is clicked
	{	//checks if user input correct answer
		boolean correct = check(); 
		//if answer was correct, text color of question becomes green
		if (correct) question.setTextColor(green);
		//if incorrect, text color becomes red
		else question.setTextColor(red);
	}
	
	private boolean check() //checks if user entered correct answer
	{
		int answer;
		//if user didn't input anything, input defaults to 0
		if (input.getText().toString().trim().length() == 0) answer = 0; 
		//otherwise, assign input to variable 'answer'
		else answer = Integer.parseInt(input.getText().toString());
		
		if (answer == correctAns) return true; //if input is correct, returns true
		else return false; //if not, returns false
	}
	
	//when user taps 'Play time Trial' button
	public void onPlay(View v)
	{
		inPlay = true; //the time trial game is in effect
		genNew = true; //game can only be played in generate new configuration
		//displays timer
		timer.setText("30s");
		timer.setVisibility(View.VISIBLE);
		timer.setTextColor(green);
		input.setText("");
		
		
		try {//prepares all sound effects that will be used in the game
			afd1 = getAssets().openFd(FirstAudioFile);
			afd2 = getAssets().openFd(SecondAudioFile);	
			afd3 = getAssets().openFd(ThirdAudioFile);
			afd4 = getAssets().openFd(HighScoreClapping);
			
			m_player1.setDataSource(afd1.getFileDescriptor(), afd1.getStartOffset(), afd1.getLength());
			m_player1.setLooping(true);
			m_player1.prepare();

			m_player2.setDataSource(afd2.getFileDescriptor(), afd2.getStartOffset(), afd2.getLength());
			m_player2.setLooping(false);
			m_player2.prepare();

			m_player3.setDataSource(afd3.getFileDescriptor(), afd3.getStartOffset(), afd3.getLength());
			m_player3.setLooping(false);
			m_player3.prepare();

			m_player4.setDataSource(afd4.getFileDescriptor(), afd4.getStartOffset(), afd4.getLength());
			m_player4.setLooping(false);
			m_player4.prepare();
		
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		//displays score (number of correct answers)
		ShowCorrect.setText("Correct: 0");
		ShowCorrect.setTextColor(black);
		ShowCorrect.setVisibility(View.VISIBLE);
		//shows and hides relevant and irrelevant information respectively
		time.setVisibility(View.VISIBLE);
		check.setVisibility(View.INVISIBLE);
		generate.setVisibility(View.INVISIBLE);
		ShowDifficulty.setVisibility(View.INVISIBLE);
		difficulty_view.setVisibility(View.INVISIBLE);
		seekBar.setVisibility(View.INVISIBLE);
		send.setVisibility(View.INVISIBLE);
		play.setVisibility(View.INVISIBLE);
		question.setVisibility(View.INVISIBLE);
		next.setText("Start"); //changes text of 'Next' button to 'Start'
	}
	
	//when user taps 'Save' button, meaning they wish to save the current equation 
	public void onSave(View v)
	{	
		String saveQuestion = question.getText().toString(); //the current equation to be saved
		
		try 
		{	//opens saved list file
			FileInputStream fis = openFileInput(saveFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
			
			String saved = reader.readLine(); //retrieves saved questions
			
			if (saved != null) 
					saveQuestion  =  saved + saveQuestion; //appends new question to list of other questions
			//closes file
			reader.close();
			isr.close();
			fis.close();
	
			//open saved list file (for writing)
	    	FileOutputStream fos = openFileOutput(saveFile, Context.MODE_PRIVATE);
	    	OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter writer = new BufferedWriter(osw);
	
			writer.append(saveQuestion + ";"); //write list of question into save file (add ; to separate)
			
			//closes file
			writer.close();
			osw.close();
			fos.close();	
			//alerts user that the question has been saved successfully
			Toast.makeText(getBaseContext(), "Question Saved Succesfully!", Toast.LENGTH_SHORT).show();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			//informs user that an error occurred while trying to save the question (in such a case)
			Toast.makeText(getBaseContext(), "There was an error saving the question...", Toast.LENGTH_LONG).show();
		}
			
	}
	
	//if player taps 'Edit' button, indicating they wish to edit the list of saved questions
	public void onEdit(View v)
	{
		String saved = "";
		//creates ArrayList for adapting to ListView
		ArrayList<CharSequence> list = new ArrayList<CharSequence>();
		String[] temp;
		Intent intent = new Intent(this, EditSaved.class); //intent to forward user to EditSaved activity

		try {
		//opens saved questions file
		FileInputStream fis = openFileInput(saveFile);	
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader reader = new BufferedReader(isr);
		
		saved = reader.readLine(); //retrieves saved question as single string
		
		if (saved != null)
		{
			temp  =  saved.split(";"); //creates array of individual saved equations
			
			for (int i = 0; i < temp.length; ++i) 
				list.add(temp[i]); //adds each array item to the ArrayList
		}
		//closes file
		reader.close();
		isr.close();
		fis.close();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (saved != null) //if there are items in the save file
		{
			intent.putExtra("saved", list); //includes ArrayList in the intent
			startActivity(intent); //starts EditSaved activity
		}
		//if the saved file is empty
		else Toast.makeText(getBaseContext(), "You have no saved questions...", Toast.LENGTH_SHORT).show();		
	}
	
	@Override //when the difficulty bar is moved
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		difficulty = progress + 1; //sets difficulty according to setting of bar
		ShowDifficulty.setText(difficulty.toString()); //shows new difficulty next to bar
		
		String str = generateNew(); //generates and outputs new equation with new difficulty setting
		calculate(str); //calculates answer to equation
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
	
	
	//for timer used in time trial game
	public class CounterClass extends CountDownTimer {

		public CounterClass(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}


		@Override //every time the timer changes (by a millisecond)
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			
			long time = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished); //milliseconds till reaching 0
			String seconds = String.valueOf(time); //seconds till reaching 0
		
			timer.setText(seconds + "s"); //outputs remaining time to timer view
			
			if (time == 5) //if there are less than 5 seconds left
			{
				m_player1.stop(); //stops ticking noise
				m_player2.start(); //starts more urgent sounding pounding noise
				
				timer.setTextColor(red); //changes timer text color to red
			}
		}

		@Override //when timer reaches 0
		public void onFinish() {
			// TODO Auto-generated method stub
			timer.setText("done..."); 
			
			m_player2.stop(); //stops pounding noise
			m_player3.start(); //plays a single ding sound to signify the ending of the clock
				
			next.setText("Continue"); //changes text of 'Next' button to 'Continue'
			
			input.setEnabled(false); //disables input (not that there's anything to do with it at this point)
			
			ShowCorrect.setTextColor(green); //changes the color of the number of correct answers to green
			
			if (NewHighScore()) //checks to see if user has beaten the high score.
			{//if so...
				ShowCorrect.setText("New High Score! Correct: " + Correct); //changes text to include 'New High Score!'
				m_player4.start(); //plays clapping sound
			}
			
			inPlay = false; //the time trial game is officially over
		}				
	}
	
	//checks if player has beaten high score (different scores help for each difficulty)
	private boolean NewHighScore()
	{
		String read = null;
		int HighScore = 0; //default value of 0
		String highScoreFile;
		
		//chooses appropriate text file to retrieve current high score from, based on current difficulty
		switch (difficulty) 
		{
		case 1:	highScoreFile = highScoreFile1; //level 1
					break;
		case 2:	highScoreFile = highScoreFile2; //level 2
					break;
		case 3:	highScoreFile = highScoreFile3; //level 3
					break;
		case 4:	highScoreFile = highScoreFile4; //level 4
					break;
		case 5:	highScoreFile = highScoreFile5; //level 5
					break;
		default:	highScoreFile = highScoreFile1; //defaults to level 1
						break;
		}
		
		try {
			//opens text file
			FileInputStream fis = openFileInput(highScoreFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
			
			read = reader.readLine(); //retrieves current high score
			
			if (read != null) 
			{
				HighScore = Integer.parseInt(read); //changes high score value to int for comparison
			}
			
			//closes text file
			reader.close();
			isr.close();
			fis.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		
		if (Correct > HighScore) //if players score is greater than the current high score
		{
			try {
				//opens text file (for writing)
				FileOutputStream fos = openFileOutput(highScoreFile, Context.MODE_PRIVATE);
		    	OutputStreamWriter osw = new OutputStreamWriter(fos);
				BufferedWriter writer = new BufferedWriter(osw);
				
				writer.append(Integer.toString(Correct)); //replaces old high score with new one
				
				//closes text file
				writer.close();
				osw.close();
				fos.close();	
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//in case of error, user will be notified
					Toast.makeText(getBaseContext(), "Error saving new high score...", Toast.LENGTH_SHORT).show();
				} 
		
			return true; //user has new high score
		}
		
		return false; //user did not exceed high score
	}
	
	private void endPlay() //when time trial ends, returns activity to original state
	{	
		//shows and hides relevant and irrelevant information respectively
		time.setVisibility(View.INVISIBLE);
		timer.setVisibility(View.INVISIBLE);
		ShowCorrect.setVisibility(View.INVISIBLE);
		check.setVisibility(View.VISIBLE);
		generate.setVisibility(View.VISIBLE);
		ShowDifficulty.setVisibility(View.VISIBLE);
		difficulty_view.setVisibility(View.VISIBLE);
		seekBar.setVisibility(View.VISIBLE);
		send.setVisibility(View.VISIBLE);
		play.setVisibility(View.VISIBLE);
		input.setEnabled(true);
		
		Correct = 0;
	}
}
