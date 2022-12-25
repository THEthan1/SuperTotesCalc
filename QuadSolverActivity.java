package ethan.glick.supestotescalc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class QuadSolverActivity extends Activity implements OnClickListener{

	private final String instanceFile = "quadSolverInstance";
	private String AudioFile = "QuadSolver.mp3";
	private MediaPlayer m_player;
	private AssetFileDescriptor afd;
		
	EditText A;
	EditText B;
	EditText C;
	TextView X1;
	TextView X2;
	TextView Error;
	TextView ErrorMsg;
	Button sendX1;
	Button sendX2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quad_solver);
		
		new File(instanceFile);
		
		 m_player = new MediaPlayer();
		 m_player.setLooping(true);
		
		A = (EditText) findViewById(R.id.etA_value); //input value A
		B = (EditText) findViewById(R.id.etB_value); //input value B
		C = (EditText) findViewById(R.id.etC_value); //input value C
		X1 = (TextView) findViewById(R.id.tvX1ans); //answer 1
		X2 = (TextView) findViewById(R.id.tvX2ans); //answer 2
		Error = (TextView) findViewById(R.id.tvError); 
		ErrorMsg = (TextView) findViewById(R.id.tvErrorMsg); //error message
		sendX1 = (Button) findViewById(R.id.btSendX1);
		sendX2 = (Button) findViewById(R.id.btSendX2);
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
	public void onDestroy()
	{
		super.onDestroy();
		
		//erases stored instance of activity
		try {
			
			FileOutputStream fos = openFileOutput(instanceFile, Context.MODE_PRIVATE);
	    	OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter writer = new BufferedWriter(osw);
			
			writer.close();
			osw.close();
			fos.close();	
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
	}

	@Override
	public void onPause() {
	    super.onPause();
	    
	    if (!Music.isMuted())
	    	m_player.pause();
	    
		try { //saves the current state of the activity so that the user 
			  //can return to view or make use of alternate answer values
			
		FileOutputStream fos = openFileOutput(instanceFile, Context.MODE_PRIVATE);
    	OutputStreamWriter osw = new OutputStreamWriter(fos);
		BufferedWriter writer = new BufferedWriter(osw);
		
		String[] save = new String[5];
		//saves values
		save[0] = A.getText().toString();
		save[1] = B.getText().toString();
		save[2] = C.getText().toString();
		save[3] = X1.getText().toString();
		save[4] = X2.getText().toString();
		
		for (int i = 0; i < 5; ++i)
					writer.append(save[i] + ";"); //saves each value separated by ';'
		
		writer.close();
		osw.close();
		fos.close();	
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
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
				 
        Intent in = getIntent();
        
        if (!Music.isMuted())
        {
			m_player.seekTo(in.getIntExtra("CurrentPosition", 0)); //plays from same position as as Calculator left off at
			m_player.start();
        }
        
	    String strLine = null;
	    String saved[] = null;
	    
	    try { //returns activity to previous state
    		
			FileInputStream fis = openFileInput(instanceFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
		
			strLine  = reader.readLine(); //reads saved information for previous state
			
			if (strLine != null)
			{
			    saved = strLine.split(";"); //separates information by ';'
			    
			    A.setText(saved[0]);
			    B.setText(saved[1]);
			    C.setText(saved[2]);
			    X1.setText(saved[3]);
			    X2.setText(saved[4]);
			}
			
			reader.close();
			isr.close();
			fis.close();
			
    	} catch (Exception e) {
			e.printStackTrace();
    	}	 
	    
		if(X1.getText().toString() != "") //only shows forward buttons if relevant values exist
		{
			sendX1.setVisibility(View.VISIBLE);
			sendX2.setVisibility(View.VISIBLE);
		}
	}
	
	
	@Override
	public void onClick(View v)
	{
		Intent intent;
		if (v.getId() == R.id.btReturnQuad) //return to calculator		
		{
			intent = new Intent(this, CalculatorActivity.class);
			intent.putExtra("CurrentPosition", m_player.getCurrentPosition());
		}
		else //return to home screen
		{
			intent = new Intent(this, HomeScreenActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);//closes the activity completely
			startActivity(intent);															//this is necessary for the exit 
		}																					//button to work properly		
		startActivity(intent);
	}
	
	public void onClickClear(View v) //empties all fields and hides error message view (if showing)
	{
		A.setText("");
		B.setText("");
		C.setText("");
		X1.setText("");
		X2.setText("");
		
		Error.setVisibility(View.INVISIBLE);
		ErrorMsg.setVisibility(View.INVISIBLE);
		sendX1.setVisibility(View.INVISIBLE);
		sendX2.setVisibility(View.INVISIBLE);
	}
	
	public void onClickCalculate(View v)//calculates result and outputs results or error message
	{
		//hides error message view, if showing
		ErrorMsg.setVisibility(View.INVISIBLE);	
		Error.setVisibility(View.INVISIBLE);
		
		float a, b, c; //the A,B,C values of a quadratic equation
		String temp = "0";
		String error = ""; //an error message to be shown if a math error is found
		
		if (A.getText().toString().trim().length() > 0) temp = A.getText().toString(); //if there is an input in field A
		else
		{	
			error = "Insert value for 'A'."; //error message if A value is missing
		}
			a = Float.parseFloat(temp); //assigns A input value
		
		if (B.getText().toString().trim().length() > 0) temp = B.getText().toString(); //if there is an input in field B
		else
		{
			error = "Insert value for 'B'."; //error message if B value is missing
		}
			b = Float.parseFloat(temp); //assigns B input value
		
		if (C.getText().toString().trim().length() > 0) temp = C.getText().toString(); //if there is an input in field C
		else
		{
			error = "Insert value for 'C'."; //error message if C value is missing
		}
			c = Float.parseFloat(temp); //assigns C input value
		
		if (error == "") error = errorCheck(a,b,c); //checks for input errors, such as improper number ratios
		
		if (error == null) //if there were no errors
		{
			Float x1, x2, sqrt, BB;
			
			BB = b*b; //b^2
			sqrt = (float) Math.sqrt(BB - 4*a*c); //the square root of the discriminator
			
			x1 = (-b + sqrt) / (2*a); //answer 1
			x2 = (-b - sqrt) / (2*a); //answer 2
			
			X1.setText(x1.toString());
			X2.setText(x2.toString());
			
			sendX1.setVisibility(View.VISIBLE);
			sendX2.setVisibility(View.VISIBLE);
		}	
		else 
		{ //show relevant error message
			Error.setVisibility(View.VISIBLE);
			ErrorMsg.setText(error);
			ErrorMsg.setVisibility(View.VISIBLE);
			
			X1.setText("");
			X2.setText("");
			
			sendX1.setVisibility(View.INVISIBLE);
			sendX2.setVisibility(View.INVISIBLE);
		}
	}
	
	public void onSend(View v) //send desired X value to calculator activity
	{
		Intent sendX = new Intent(this, CalculatorActivity.class);
		
		if (v.getId() == R.id.btSendX1)
			sendX.putExtra("xValue", X1.getText().toString()); //value of answer 1
		
		else
			sendX.putExtra("xValue", X2.getText().toString()); //value of answer 2
		
		sendX.putExtra("CurrentPosition", m_player.getCurrentPosition());
		
		startActivity(sendX);

	}
	
	private String errorCheck(double a, double b, double c)
	{
		if (a == 0) return "'A' value cannot equal 0!"; //if A value is 0
		else if ((b*b) < (4*a*c)) return "Number ratio is impossible!"; //if discriminator is negative
		else return null;
	}
}
