package ethan.glick.supestotescalc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMS_QuestionReceiver extends BroadcastReceiver
{
	private String SMS_Prefix = "Supes-Totes-Calc Equation: ";
	private final String saveFile = "SavedQuestions"; //text file where saved equations are stored
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		// TODO Auto-generated method stub
		if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
		{
			Log.d("recieved", "received!");
			Bundle bundle = intent.getExtras();
			if (bundle != null)
			{
				try {
					Object[] pdus = (Object[]) bundle.get("pdus");
					String smsInfo = "";
					for(int i = 0;i<pdus.length;i++) //obtains the message text
					{
						SmsMessage smsMsg = SmsMessage.createFromPdu((byte[]) pdus[i]);
						smsInfo += smsMsg.getDisplayMessageBody();
					}
					
					Log.d("test", "  |"+smsInfo.substring(0, SMS_Prefix.length())+"||"+SMS_Prefix+"|");

					if (SMS_Prefix.equals(smsInfo.substring(0, SMS_Prefix.length())))
					{
						Log.d("saving", "It's good!!!!");
				        String equation = smsInfo.substring(SMS_Prefix.length(), smsInfo.length()); //the current equation to be saved
		
				        if (isGood(equation))
				        {
							Toast.makeText(context, "You have received a new equation!", Toast.LENGTH_SHORT).show();
							saveEquation(equation, context);
				        }
					}
				}
				catch(Exception e)
				{
					Log.d("Exception caught",e.getMessage());
				}

			}
		}
	}
	
	//checks that the received equation is compatible with the app (for case where person sends custom equation)
	private boolean isGood(String equation)
	{
		if (equation.length() < 17) //insuring the equation will not be too big for the screen
		{
			boolean hasChar = false; //to make sure the equation has at least one operation
			
			for (int i = 0; i < equation.length(); i++)
			{
				char c = equation.charAt(i);
				int x = Character.getNumericValue(c);
				boolean charLast = false; //the last position checked
				
				
				if (x < 0 || x > 9) //if value is not number 0-9
				{
					if (i != 0 && i != (equation.length()-1) && !charLast) //if its not the first or last position
					{												   //and the previous position was a number
						if (c == '+' || c == '-' || c == 'x')
						{
							charLast = hasChar = true;
							continue;
						}
					}
					return false;
				}
				charLast = false;
			}
			
			if (hasChar)
				return true;
		}
		return false;
	}
	
	private void saveEquation(String equation, Context context)
	{
	
		try 
		{	//opens saved list file
			FileInputStream fis = context.openFileInput(saveFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(isr);
			
			String saved = reader.readLine(); //retrieves saved questions
			
			if (saved != null) 
				equation  =  saved + equation; //appends new question to list of other questions
			//closes file
			reader.close();
			isr.close();
			fis.close();
		
			//open saved list file (for writing)
	    	FileOutputStream fos = context.openFileOutput(saveFile, Context.MODE_PRIVATE);
	    	OutputStreamWriter osw = new OutputStreamWriter(fos);
			BufferedWriter writer = new BufferedWriter(osw);
	
			writer.append(equation + ";"); //write list of question into save file (add ; to separate)
			
			//closes file
			writer.close();
			osw.close();
			fos.close();	
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			//informs user that an error occurred while trying to save the question (in such a case)
			Toast.makeText(context, "There was an error saving a received equation...", Toast.LENGTH_LONG).show();
		}
	}

}
