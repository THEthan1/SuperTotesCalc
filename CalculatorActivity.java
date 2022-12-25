package ethan.glick.supestotescalc;

import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//a simple and straightforward calculator
//quadratic equation solver can be reached from here
public class CalculatorActivity extends Activity implements OnClickListener, OnLongClickListener{

	enum OPERATIONS {ADD, SUBTRACT, MULTIPLY, DIVIDE, PERCENT, PWR, ROOT, EQUALS}
	
	private OPERATIONS operation;
	private Button[] mat;
	private String number;
	private double ans;
	private long digitCount;
	private boolean positive;
	private int white;
	private int black;
	private boolean calculated;
	private boolean onHold;
	private String AudioFile = "Calculator.mp3";
	private MediaPlayer m_player;
	private AssetFileDescriptor afd;
	Button selected;
	TextView screen;
	Button sign;	
	Button decimal;
	Button clear;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculator);
		
		m_player = new MediaPlayer();
		
		screen = (TextView) findViewById(R.id.tvCalcView);
		sign = (Button) findViewById(R.id.btSign);
		decimal = (Button) findViewById(R.id.btDecimal);
		clear = (Button) findViewById(R.id.btClear);
		decimal.setClickable(true); 
		sign.setClickable(false);

		number = "";
		ans = 0;
		digitCount = 0; //number of digits in the current number
		positive = true; //if current number is positive or negative
		calculated = false; //if equals was called before current number
		white = Color.rgb(255, 255, 255);
		black = Color.rgb(0, 0, 0);
		operation = OPERATIONS.EQUALS;
		selected = (Button) findViewById(R.id.btPlus);
		onHold = false;
		
		View v = findViewById(R.id.btSqrt);
		v.setOnLongClickListener(this);
		v = findViewById(R.id.btPwr);
		v.setOnLongClickListener(this);
		
        try //preparing background audio
		{
			afd = getAssets().openFd(AudioFile);
			m_player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
			m_player.setLooping(true);
			m_player.prepare();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
        mat = new Button[11]; //sets up matrix of the calculator buttons
        mat[10] = (Button)findViewById(R.id.btPi);//setting Pi button as last button in array
    	String str;
    	int resID;
    	for (int i = 0; i < 10; i++)
    	{
    			str = "bt" + i;
    			resID = getResources().getIdentifier(str, "id", "ethan.glick.supestotescalc");
    		mat[i] = (Button)findViewById(resID);
    		mat[i].setOnClickListener(this);
    	}
    	mat[10].setOnClickListener(this);
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
		long id = item.getItemId();
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
			// start HomeScreenActivity
			Intent exitintent = new Intent(this,HomeScreenActivity.class);
			exitintent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(exitintent);
			HomeScreenActivity.exitHandler.sendEmptyMessage(0);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    
	    if (!Music.isMuted())
	    	m_player.pause();
	}
	
	@Override
    public void onResume() 
    {
        super.onResume();  // Always call the superclass method first
        
        Intent in = getIntent();
        
        if (in.getExtras() != null)
        {	//resume audio from same position as QuadSolverActivity
    		m_player.seekTo(in.getIntExtra("CurrentPosition", 0));
        }
        
        if (!Music.isMuted())
        	m_player.start();
        
        //if user opted to end an answer from the quadratic equation solver to the calculator
        //this sets up the calculator with the given value
        if (in.getCharSequenceExtra("xValue") != null)
        {
        	number =  (String) in.getCharSequenceExtra("xValue");
        	
        	double num = Double.parseDouble(number);
              
            if (num >= 0) positive = true;
            else positive = false;
            if (num % 1 == 0)
            {
         	   decimal.setClickable(true);
         	   number = ((Long)(long) num).toString();
           	   screen.setText(number);
            }
            else
            {
               decimal.setClickable(false);
           	   screen.setText(number); 
            }
              
            sign.setClickable(true);
              
            digitCount = number.length();
        }  
    }

	@Override
	public void onClick(View v) //when a digit button is clicked
	{		
		sign.setClickable(true);
		
		if (digitCount <= 12)
		{		
				if (number == "")
				{
					screen.setText("");
					//sets value as being positive (in case result of previous calculation was negative)
					positive = true;
				}
				 
		        switch(v.getId()) //adds the selected digit to a string of the other selected digits
		        {
		        case R.id.bt0:
		        		if(number == "") number = "0"; //set number value to 0 if screen is empty
		        		if(number != "0")
		        		{
		        			number += "0";
		        			++digitCount;
		        		}
		            	break;
		        case R.id.bt1:
		        		number += "1";
		        		++digitCount;
		        		break;
		        case R.id.bt2:
		        		number += "2";
		        		++digitCount;
		            	break;
		        case R.id.bt3:
		        		number += "3";
		        		++digitCount;
		        		break;
		        case R.id.bt4:
		        		number += "4";
		        		++digitCount;
		            	break;
		        case R.id.bt5:
		        		number += "5";
		        		++digitCount;
		            	break;
		        case R.id.bt6:
		        		number += "6";
		        		++digitCount;
		            	break;
		        case R.id.bt7:
		        		number += "7";
		        		++digitCount;
		            	break;
		        case R.id.bt8:
		        		number += "8";
		        		++digitCount;
		            	break;
		        case R.id.bt9:
		        		number += "9";
		        		++digitCount;
		            	break;
		        case R.id.btPi:
	        		number = "3.14159265358";
	        		decimal.setClickable(false);
	        		digitCount = 12;
	            	break;
		        default:
		        		number += "0";
		        		++digitCount;
		        		break;
		        }
		        
		        screen.setText(number);
		}
	}
	
	//when an operation button is selected
	public void OpSelect(View v)
	{
		if (!onHold)
		{
			if (screen.getText().toString() != "") //clears screen in preparation for next input
			{
	    		selected.setTextColor(white); //sets previously selected button to original state (color)
	    		
	    		if (calculated || (!calculated && number == "")) //if this is the first operand
	    		{
					number = screen.getText().toString();
					
					if (number == "MATH ERROR!") number = "0";
					
					selected = (Button) v;
					
					switch(selected.getId()) //sets the appropriate operation based on the users button selection
			        {
			        case R.id.btPlus:
			        		operation = OPERATIONS.ADD; //set selected operation as current operation
			        		break;
			        case R.id.btMinus:
			        		operation = OPERATIONS.SUBTRACT;
			        		break;
			        case R.id.btMultiply:
			        		operation = OPERATIONS.MULTIPLY;
			        		break;
			        case R.id.btDivide:
			        		operation = OPERATIONS.DIVIDE;
			        		break;
			        case R.id.btPercent:
			        		operation = OPERATIONS.PERCENT;
			        		break;
			        case R.id.btPwr:
		        		operation = OPERATIONS.PWR;
		        		break;
			        case R.id.btSqrt:
		        		operation = OPERATIONS.ROOT;
		        		break;
			        default:
			        		operation = OPERATIONS.ADD;
			        		break;
			        }
					
					selected.setTextColor(black); //change selected buttons text color to black
					ans = Double.parseDouble(number); //saves the number on screen for 
					number = "";					 //future calculation with second operand
					calculated = false;
	    		}
				
	    		else //if the second operand has been input but equals what not called previously
				{
					View temp = null;
					OnEquals(temp); //calls equals
					OpSelect(v); //sets new operation with the answer 
								//from the OnEquals call as the left operand
				}
				
				digitCount = 0;
				decimal.setClickable(true); 
			}
		}
		
		onHold = false;
	}
	
	public void OnEquals(View v)
	{
		boolean MathError = false; //only true if an improper mathematical 
								   //calculation was attempted
		//number = screen.getText().toString();
		
		if (number != "") //the screen is not empty
		{		
			double temp = Double.parseDouble(number);
			selected.setTextColor(white);
    		decimal.setClickable(true);
			
			if (operation != OPERATIONS.EQUALS) //checks that an operation 
			{									//has in-fact been selected
				switch(operation) //applies selected operation on the two operands			
		        {								
		        case ADD:
		        	if (number.contains("."))
		        		ans += Double.parseDouble(number);
		        	else ans += Long.parseLong(number);
		        		break;
		        case SUBTRACT:
		        	if (number.contains("."))
		        		ans -= Double.parseDouble(number);
		        	else ans -= Long.parseLong(number);		        		
		        	break;
		        case MULTIPLY:
		        	if (number.contains("."))
		        		ans *= Double.parseDouble(number);
		        	else ans *= Long.parseLong(number);		        		
		        	break;
		        case DIVIDE:
		        	if (Double.parseDouble(number) != 0.0)
					{
		        		if (number.contains("."))
			        		ans /= Double.parseDouble(number);
			        	else ans /= Long.parseLong(number);				
		        	}
		        	else //if the user attempted to divide by 0
		        	{
						MathError = true;
						ans = 0;
						number = "";
						Button btn = (Button) findViewById(R.id.btClear);
						btn.setText("AC");
					}	
		        		break;
		        case PERCENT:
		        		if (number.contains("."))
		        			ans = (ans * Double.parseDouble(number)) / 100;
			        	else ans = (ans * Long.parseLong(number)) / 100;
		        		break;
		        case PWR:
		        	double pwr;
		        	if (number.contains("."))
		        		pwr = Double.parseDouble(number);
		        	else pwr = Long.parseLong(number);
		        	
		        	ans = Math.pow(ans, pwr);
	        		break;
		        case ROOT:
		        	double root;
		        	if (number.contains("."))
		        		root = Double.parseDouble(number);
		        	else root = Long.parseLong(number);		        	
		        	if (ans == 0.0 || ans < 0 && (root % 2 == 0)) //if the user attempted to get 0th root or even root of negative number
					{
		        		MathError = true;
						ans = 0;
						number = "";
						Button btn = (Button) findViewById(R.id.btClear);
						btn.setText("AC");
					}
		        	else 
		        	{
		        		root = Math.abs(root);
		        		ans = Math.pow(root, 1/ans);
		        		if (!positive) ans = -ans;
					}	
	        		break;
		        default:
		        		ans += Double.parseDouble(number);
		        		break;
		        }
			}
			else ans = temp;
				
			if (ans % 1 == 0) //if ans is an integer
			{
				Long answerI;
				answerI = (long) ans;
				screen.setText(answerI.toString()); //show answer on screen
				
				if (MathError) screen.setText("MATH ERROR!"); //show error message on screen
			}												  //if a math error has been made
			else
			{
				Double answerD;
				answerD = ans; 
				screen.setText(answerD.toString()); //show answer on screen
			}
	
			//preparation for next input
			operation = OPERATIONS.EQUALS;
			if (ans < 0) positive = false;
			else positive = true;
			digitCount = 0;
			number = "";
			calculated = true;
		}
	}
	
	//adds a decimal point to the current number
	public void setDecimal(View v)
	{
		if (number == "") number = "0";
		number += ".";
		decimal.setClickable(false);
		screen.setText(number);
	}
	
	//changes the current number from positive to negative and vice-versa
	public void setSign(View v)
	{		
		if(number != "")
		{		
      		if(positive)
      		{
      			number = "-" + number; //adds a '-' to the beginning of the number
      			positive = false;   			
      		}
      		else //if negative
      		{//removes the '-' from the beginning of the number
      			number = number.substring(1, number.length()); 
      			positive = true; 
      		}
  
      		//if (!number.contains(".")) //checks in case of instance where number ends in '.' 
			//{						   //and would cause error if cast to double
				//number = longeger.toString((long) Double.parseDouble(number));
			//}
      		
      		screen.setText(number);
		}
		else //if equals has been called and the number on the 
		{	 //screen is actually the ans and not the current number	
      		ans *= -1;	
      		
      		if (ans % 1 == 0) 
			{
      			screen.setText(Long.toString((long)ans));
			}
      		else
      		{
      			screen.setText(Double.toString(ans));
      		}
		}
	}
	
	//deletes last digit in number
	public void backspace(View v)
	{
		if (number != "")
		{
			if (number.length() > 1)
					number = number.substring(0, number.length()-1);
			
			else number = "";
			
			--digitCount;
			
			screen.setText(number);
		}
	}
	
	public void OnClear(View v)
	{	
			ans = 0;
			number = "";
			screen.setText(number);
			operation = OPERATIONS.EQUALS;
			selected.setTextColor(white);
		
		digitCount = 0;
		positive = true;
		decimal.setClickable(true);
		sign.setClickable(false);
	}
	
	//the possible app exit locations
	public void exitOptions(View v)
	{
		Intent intent;
		m_player.pause();
		
		if (v.getId() == R.id.btQ_Function)  //forwards to quadratic equation solver activity 
		{
			intent = new Intent(this, QuadSolverActivity.class);	
			intent.putExtra("CurrentPosition", m_player.getCurrentPosition());
		}
		else //forwards to home screen activity
		{
			intent = new Intent(this, HomeScreenActivity.class);
			intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP|intent.FLAG_ACTIVITY_SINGLE_TOP);//closes the activity completely
			startActivity(intent);															//this is necessary for the exit 
			 																				//button to work properly
		}
		
		startActivity(intent);
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		
		if (screen.getText().toString() != "MATH ERROR!")
		{
			onHold = true;
			
			selected.setTextColor(white);
			
			if (v.getId() == R.id.btSqrt)
			{
				ans = 2;
				number = screen.getText().toString();
	    		operation = OPERATIONS.ROOT;
			}
			else
			{
				ans = Double.parseDouble(screen.getText().toString());
				number = "2";
	    		operation = OPERATIONS.PWR;
			}
			
			selected = (Button) v;
			OnEquals(v);
		}
		
		return false;
	}
}
