package ethan.glick.supestotescalc;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
		
public class EditSaved extends Activity implements OnItemClickListener, OnClickListener{

	private ArrayList<CharSequence> SavedList;
	private ListView List;
	private final String textFile = "SavedQuestions";
	private String AudioFile = "Edit.mp3";
	private MediaPlayer m_player;
	private AssetFileDescriptor afd;
	private String sendEquation;
	private String SMS_Prefix = "Supes-Totes-Calc Equation: ";
	ArrayAdapter<CharSequence> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_edit_saved);
				
			sendEquation = null;
			
			Intent intent = getIntent(); //receives the saved list from the questions activity
		    SavedList = intent.getCharSequenceArrayListExtra("saved");
			
	        List = (ListView) findViewById(R.id.tvSaved);
	        
	        //declares and sets adapter for ListView
	        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_list_item_1, SavedList);
	        List.setAdapter(adapter);
	      
	        List.setOnItemClickListener(this);
	      
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
		    	m_player.reset();
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

		@Override //when an item is clicked
		public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
		{
			AlertDialog.Builder info = new AlertDialog.Builder(this); //shows alert box to remove item from list
	        info.setTitle("Select Action");
	        info.setMessage("What would you like to do with the equation '" + SavedList.get(position).toString() + "'?");
	        info.setCancelable(true);
	        info.setPositiveButton("Send", new DialogInterface.OnClickListener() 
	        {
	            public void onClick(DialogInterface dialog, int id)
	            {	            	
	            	sendEquation = SavedList.get(position).toString();
	            	optionSEND(position);
	            }
	        });
	        info.setNeutralButton("Delete", new DialogInterface.OnClickListener() 
	        {
	            public void onClick(DialogInterface dialog, int id)
	            {	            	
	            	optionDELETE(position);
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
		}
		
		private void optionDELETE(final int position)
		{
			AlertDialog.Builder info = new AlertDialog.Builder(this); //shows alert box to remove item from list
	        info.setTitle("Delete Equation");
	        info.setMessage("Are you sure you want to delete the equation '" + SavedList.get(position).toString() + "' from the list?");
	        info.setCancelable(true);
	        info.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
	        {
	            public void onClick(DialogInterface dialog, int id)
	            {	            	
	            	remove(position); //calls function to remove item from list
	            	
	            	List.setAdapter(adapter);
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
		}
		
		private void optionSEND(final int position)
		{
			AlertDialog.Builder info = new AlertDialog.Builder(this); //shows alert box to remove item from list
	        info.setTitle("Send Question");
	        info.setMessage("Select the contact you wish to send the equation '" + SavedList.get(position).toString() + "' to:\n\n" +
	        				"Please Note: The receiving user must currently have the Supes-Totes-Calc " +
	        				"app running on their device in order to receive the question.");
	        info.setCancelable(true);
	        info.setPositiveButton("Contacts", new DialogInterface.OnClickListener() 
	        {
	            public void onClick(DialogInterface dialog, int id)
	            {	            	
	            	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
	            	startActivityForResult(intent, 1); 
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
		}
		
		@Override
		public void onActivityResult(int reqCode, int resultCode, Intent data) {
		  super.onActivityResult(reqCode, resultCode, data);
		  
		  switch (reqCode) {
		    case (1) :
		      if (resultCode == Activity.RESULT_OK) {
		        Uri contactData = data.getData();
		        
		        if (contactData != null)
		        {
		        	Cursor c =  getContentResolver().query(contactData, null, null, null, null);
		        	if (c != null && c.moveToFirst()) {

		                String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

		        		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, 
	                             					ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
		        		
	                    phones.moveToFirst();
	                      
	                    String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	                       		
		        		sendEquation(number);
		        	}
		        }
		      }
		      break;
		  }
		}
		
		private void sendEquation(String number)
		{				
			SmsManager mng = SmsManager.getDefault();
			mng.sendTextMessage(number, null, SMS_Prefix + sendEquation, null, null);
			
			Toast.makeText(getBaseContext(), "Equation Sent!", Toast.LENGTH_LONG).show();
		}
		
	    private void remove(int index)
	    {	
			SavedList.remove(index); //removes item from list
		}

	@Override
	public void onClick(View v) //exiting the activity
		{		
		try { //saving new list to the save file
			
			FileOutputStream fos;
			fos = openFileOutput(textFile, Context.MODE_PRIVATE);
			
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter writer = new BufferedWriter(osw);

			String NewSave = new String("");
			
			//adds list items with separation of ';' to a single string
			for (int i = 0; i < SavedList.size(); ++i)
				NewSave = NewSave + SavedList.get(i).toString() + ";";
		
				writer.append(NewSave);
		
			writer.close();
			osw.close();
			fos.close();
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Intent intent;
			if (v.getId() == R.id.btReturn_Edit) //return to Questions Activity
				intent = new Intent(this, QuestionsActivity.class);		
			else
			{
				intent = new Intent(this, HomeScreenActivity.class); //return to home screen
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);//closes the activity completely
				startActivity(intent);															//this is necessary for the exit 
				 																				//button to work properly
			}
				
			startActivity(intent);	
		}
}
