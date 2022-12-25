package ethan.glick.supestotescalc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
 
public class BatteryCheck extends BroadcastReceiver {
 
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Get Battery %
       int level = intent.getIntExtra("level", 0);
        
       if(level < 5)
       {
    	   Toast.makeText(context, "Battery Low", Toast.LENGTH_LONG).show();
       }
	}
}
