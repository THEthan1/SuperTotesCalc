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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class PrimeActivity extends Activity implements OnClickListener{
	
	public enum FUNCTIONS {OUTPUT, FIND, CHECK}; //the 3 different prime functions
	private FUNCTIONS FUNC;
	private String AudioFile = "Primes.mp3";
	private MediaPlayer m_player;
	private AssetFileDescriptor afd;
	EditText PrimeIn; //the number input
	TextView PrimeOut; //the appropriate output

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_prime);
		
		PrimeIn = (EditText) findViewById(R.id.etPrimeInput);
		PrimeOut = (TextView) findViewById(R.id.tvPrimeOut);
		PrimeOut.setMovementMethod(new ScrollingMovementMethod()); //output view is scrollable
		
		FUNC = FUNCTIONS.OUTPUT;

		m_player = new MediaPlayer();
	    m_player.setLooping(true);
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
	    	m_player.reset(); //stops background audio and sets it back to the beginning
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    
		 try //starts background audio
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

	@Override //when user clicks 'Prime!' button
	public void onClick(View v) 
	{	//the highest permitted inputs for the different prime functions
		int MAXoutput = 10000;
		int MAXfind = 100000;
		int MAXcheck = 1000000000;
		Integer input;
		
		PrimeOut.setText(null);
		PrimeOut.scrollTo(0, 0); //reverts output view to start position
		
		if (PrimeIn.getText().toString().trim().length() == 0) input = 0; //if no input, input is 0
		
		else input = Integer.parseInt(PrimeIn.getText().toString());
		
		if (input < 1) //number must be greater than 0
		{
			PrimeIn.setHint("Must be > 0");
			PrimeIn.setText(null);
		}
		
		else //makes sure input is appropriate size for appropriate function
		{
			switch(FUNC)
			{
			case OUTPUT: //output function
				if (input > MAXoutput) //if number is too large
				{
					PrimeIn.setHint("Can't be > " + MAXoutput);
					PrimeIn.setText(null);
				}
				else PrimeFunction(input, FUNC); //otherwise, calculates and outputs results
				break;
			case FIND: //find function
				if (input > MAXfind) //if number is too large
				{
					PrimeIn.setHint("Can't be > " + MAXfind);
					PrimeIn.setText(null);
				}
				else PrimeFunction(input, FUNC); //otherwise, calculates and outputs results
				break;
			case CHECK: //check function
				if (input > MAXcheck) //if number is too large
				{
					PrimeIn.setHint("Can't be > " + MAXcheck);
					PrimeIn.setText(null);
				}
				else PrimeFunction(input, FUNC); //otherwise, calculates and outputs results
				break;
			default:
				PrimeIn.setHint("ERROR! NO REQUEST FOUND..."); //if somehow user did not select a function
				break;
			}
		}
	}
	
	//when the user selects a function type
	public void RadioClick(View view)
	{
		boolean checked = ((RadioButton) view).isChecked(); //makes sure button is actually checked
		//clears input and output boxes
		PrimeIn.setText(null);
		PrimeOut.setText(null);
		
        switch(view.getId()) //sets function to selected funtion
        {
        case R.id.radOutput: //output function - outputs the inputed number of prime numbers
            if (checked)
            	FUNC = FUNCTIONS.OUTPUT;
            	PrimeIn.setHint("How many primes?");
            break;
        case R.id.radFind: //find function - finds the Nth prime, where N is the input number
            if (checked)
            	FUNC = FUNCTIONS.FIND;
            	PrimeIn.setHint("Which number prime?");
            break;
        case R.id.radCheck: //check function - checks if the input number is a prime number
            if (checked)
            	FUNC = FUNCTIONS.CHECK;
            	PrimeIn.setHint("Which number?");
            break;
        default: //defaults to output function
        		FUNC = FUNCTIONS.OUTPUT;
        		PrimeIn.setHint("How many primes?");
        	break;    	
        }
        
	}
	
	//the prime number calculator for the various functions
	public void PrimeFunction(int check, FUNCTIONS func)
	{
		int num = check; //the size of the prime array to generate
		
		if (func == FUNCTIONS.FIND || func == FUNCTIONS.OUTPUT)
		{
			Integer[] prime = new Integer[num]; //creates prime array
			prime[0] = 2; //sets first prime to 2
			String primes = prime[0].toString(); //the string version of the prime array (will grow gradually)
			int index = 1; //the current index
			
			//generates prime array
				for (int i = 3; num > index; i+=2) //starts at 3, and increments by 2 to only use odd numbers
				{								   //since all primes are odd aside from 2
					if (checkPrime(i, prime)) //checks if current i value is prime
					{//if so..
						prime[index] = i; //adds value to prime index
						
						if (func == FUNCTIONS.OUTPUT) //for creating prime list for output
							primes += "  " + prime[index];
						
						index++; //moves to next index
					}
				}
			//if the function is output
			if (func == FUNCTIONS.OUTPUT) PrimeOut.setText(primes); //outputs the prime list
			//if the function is find
			else if (func == FUNCTIONS.FIND)
			{
				String suffix; //the suffix for the index number of the prime (i.e. 3rd, 4th, etc.)
				int ones = num % 10; //the ones digit of the index number
				
				switch (ones) //sets proper suffix
				{
				case 1: suffix = "st"; //numbers that end in 1
							break;
				case 2: suffix = "nd"; //numbers that end in 2
							break;
				case 3: suffix = "rd"; //numbers that end in 3
							break;
				default: suffix = "th"; //numbers that end in any other number
							break;
				}
				//outputs the last prime in the prime list (the number input), and its index (with the appropriate suffix)
				PrimeOut.setText(prime[num-1].toString() + " is the " + num + suffix + " prime number.");
			}
		}
		else //func == FUNCTIONS.CHECK
		{	
			boolean isPrime = true;
			
			if (check % 2 != 0) //checks if num is divisible by any odd number 
			{
				for (int i = 3; i * i < check; i += 2)
				{
					if (check % i == 0) isPrime = false;
				}
			}
			else isPrime = false; //if number is divisible by 2
			
			//if the number is prime
			if(isPrime) PrimeOut.setText(check + " is PRIME!");
			//if not...
			else PrimeOut.setText(check + " is not prime...");
		}
	}
	//checks if a given number is prime by checking if it's divisible by any of the other number in the prime list
	public boolean checkPrime(int check, Integer[] prime)
	{	//loops through every prime in the array, until the value is greater than the square root of the checking number
		for (int i = 0; (prime[i] * prime[i]) <= check; i++)
		{	//checks if the check number is divisible by the prime in the index
			if (check % prime[i] == 0) return false; //if so, the number is not prime
		}	
		 return true; //otherwise, it is prime
	}
	//when user clicks home button
	public void onClickHome(View v)
	{	//brings user to home screen
		Intent intent = new Intent(this, HomeScreenActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
}
