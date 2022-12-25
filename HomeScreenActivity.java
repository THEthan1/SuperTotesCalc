package ethan.glick.supestotescalc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

//the first and main screen/activity. All primary activities are reacher from here
public class HomeScreenActivity extends Activity implements OnClickListener, OnLongClickListener{

	final String imageFile = "savedImageLocation";
	RelativeLayout background;
	public static Handler exitHandler;
	Button PhotoButton;
	int yellowish = Color.rgb(255,221,0);
	private String AudioFile = "HomeScreen.mp3";
	private MediaPlayer m_player;
	private AssetFileDescriptor afd;
	public Music music;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_home_screen);
		
	     registerReceiver(new BatteryCheck(), new IntentFilter(
	                Intent.ACTION_BATTERY_CHANGED));
	     
		 m_player = new MediaPlayer(); 
		 
		 music = new Music();
		
		 PhotoButton = (Button) findViewById(R.id.btCamera);
		 PhotoButton.setOnLongClickListener(this);
		
		 exitHandler = new Handler() //for when exiting the app using the exit button
					  {
					      public void handleMessage(Message msg)
					      {
					     	    super.handleMessage(msg);
							    if(msg.what == 0) 
					     	    HomeScreenActivity.this.finish();		
					   	  }
					  };
		
		 background = (RelativeLayout) findViewById(R.id.HomeScreenLayout);
		 	 
		 new File(imageFile);
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
		
		if (id == R.id.instructions) //forwards to instructions page
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
		else if (id == R.id.help) //opens phone and auto dials pre-set phone number
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
		else if (id == R.id.exit) //exit the app closing all screens
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
	   
		 try { //setting background to image that was selected (if in-fact there was one)
	    		
				FileInputStream fis = openFileInput(imageFile);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader reader = new BufferedReader(isr);
				
				String imageUri = reader.readLine();
					
				if (imageUri != null)
				{
					Uri backgroundImage = Uri.parse(imageUri);					
					InputStream imageStream;
					imageStream = getContentResolver().openInputStream(backgroundImage);
					Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
					Drawable image = new BitmapDrawable(getResources(), bitmap);
					background.setBackground(image);
				}
				
				reader.close();
				isr.close();
				fis.close();	
		 } 
		 catch (Exception e) 
		 {
				e.printStackTrace();
		 }
	
		 try //resuming the background audio
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
		 
	
	@Override
	public void onClick(View v) //forwarding to activity based on user button press
	{
		Intent intent;
		
		switch (v.getId())
		{
		case R.id.btCalculator:
			intent = new Intent(this, CalculatorActivity.class);
				break;
		case R.id.btPrimes:
			intent = new Intent(this, PrimeActivity.class);
				break;
		case R.id.btQgame:
			intent = new Intent(this, QuestionsActivity.class);
				break;
		case R.id.btInstructions:
			intent = new Intent(this, InstructionsActivity.class);
				break;
		default:
			intent = new Intent(this, CalculatorActivity.class);
				break;
		}
		
		startActivity(intent);
	}
	
	public void getPhoto(View v) //brings user to gallery to select image for background of homescreen
	{
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Picture"),0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(requestCode == 0 && resultCode == RESULT_OK)
		{
			try 
			{
				Uri selectedImage = data.getData();
				
				grantUriPermission("ethan.glick.supestotescalc", selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);
				
				InputStream imageStream;
				imageStream = getContentResolver().openInputStream(selectedImage);
				Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
				Drawable image = new BitmapDrawable(getResources(), bitmap);
				background.setBackground(image);
				
				//saves location of background image
				try {
					
					FileOutputStream fos = openFileOutput(imageFile, Context.MODE_PRIVATE);
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					BufferedWriter writer = new BufferedWriter(osw);
				
					String imageUri = selectedImage.toString(); 
						
				    writer.append(imageUri);
				
					writer.close();
					osw.close();
					fos.close();
					
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}			
		    } 
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onLongClick(View v) //for deleting background image and returning to default background
	{// TODO Auto-generated method stub
		AlertDialog.Builder info = new AlertDialog.Builder(this);
        info.setTitle("Remove Background Image?");
        info.setMessage("Are you sure you wish to revert to the default background?");
        info.setCancelable(true);
        info.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int id)
            {	//erases current background image location            	
            	
            	try {
            		FileOutputStream fos = openFileOutput(imageFile, Context.MODE_PRIVATE);
            		OutputStreamWriter osw = new OutputStreamWriter(fos);
            		BufferedWriter writer = new BufferedWriter(osw);
			
            		writer.close();
					osw.close();
					fos.close();
					
					background.setBackgroundColor(yellowish);
				} 
            	catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
            }
        });
        info.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });
        
        info.show();
        
		return false;
	}
}
