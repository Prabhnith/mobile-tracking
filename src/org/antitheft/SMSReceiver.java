package org.antitheft;

import android.content.*;
import android.os.Bundle;
import android.telephony.*;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver
{
	//Application parameters
	public static String magicWord;
	public static String number;
	public static int interval;
	private Intent localisationIntent;

	@Override
	public void onReceive(Context context, Intent intent)
	{
		//Get the stored preferences
		SharedPreferences settings = context.getSharedPreferences(AntiTheft.PREFS_NAME, 0);
		magicWord = settings.getString("magicWord", "");
		number = settings.getString("number", "");
		interval = settings.getInt("interval", 20);

		Log.e("SMSReceiver","Un SMS a ete recu");

		//get the SMS message passed in
		Bundle bundle = intent.getExtras();        
		SmsMessage[] msgs = null;

		if(bundle != null)
		{
			//retrieve the SMS message received
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];

			for (int i=0; i<msgs.length; i++)
			{
				msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

				if(msgs[i].getMessageBody().toUpperCase().equals(magicWord.toUpperCase()))
				{
					Log.e("SMSReceiver","Le SMS d'activation a ete recu");
					Log.e("SMSReceiver","Lancement du service de localisation");

					localisationIntent = new Intent(context,LocalisationService.class); 
					context.startService(localisationIntent);

					break;
				}
				else if(msgs[i].getMessageBody().toUpperCase().equals("STOP "+magicWord.toUpperCase()))
				{
					Log.e("SMSReceiver", "SMS stop recu");

					//Broadcast the intent to shutdown the service
					Intent stopLocalisationService = new Intent("STOP_LOCALISATION_SERVICE");
					context.sendBroadcast(stopLocalisationService);
					context.stopService(intent);
				}
			}
		}
	}
}