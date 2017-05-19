package org.antitheft;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

public class LocalisationService extends Service
{
	private LocationManager locationManager;
	private BroadcastReceiver receiver;

	@Override
	public IBinder onBind(Intent intent){return null;}

	@Override
	public void onCreate()
	{
		super.onCreate();

		Log.e("LocalisationService","Service de localisation lance avec un intervalle de "+SMSReceiver.interval);

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,  SMSReceiver.interval, 0, locationListener);

		//If the service receive a specific intent from the SMSReceiver, stop the service
		IntentFilter filter = new IntentFilter();
		filter.addAction("STOP_LOCALISATION_SERVICE");

		receiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				Log.e("LocalisationService","Intent d'arret recu par le service, arret en cours");
				stopSelf();
			}
		};
		registerReceiver(receiver, filter);
		Log.e("LocalisationService","apres l'enregistrement de l'intent d'arret");
	}

	@SuppressLint("NewApi") public void sendLocalisation(Location location)
	{
		Log.e("LocalisationService","SMS en cours d'envoi");
		String body = "http://maps.google.fr/maps?f=q&source=s_q&hl=fr&geocode=&q="+location.getLatitude()+","+location.getLongitude();
		SmsManager.getDefault().sendTextMessage(SMSReceiver.number, null, body, null, null);
		Log.e("LocalisationService", "SMS envoye, attente de "+SMSReceiver.interval+"ms");
	}
	
	//Define a listener that responds to location updates
	private LocationListener locationListener = new LocationListener()
	{
		//Called when a new location is found by the network location provider
		public void onLocationChanged(Location location)
		{
			Log.e("LocalisationService","Une nouvelle localisation a ete trouvee");
			//Send the current location by SMS
			sendLocalisation(location);
		}

		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			Log.e("LocalisationService","onStatusChanged");
		}

		public void onProviderEnabled(String provider)
		{
			Log.e("LocalisationService","onProviderEnabled: "+provider);
		}

		public void onProviderDisabled(String provider)
		{
			Log.e("LocalisationService","onProviderDisabled: "+provider);
		}
	};
	
	@Override
	public void onDestroy()
	{
		unregisterReceiver(receiver);
		locationManager.removeUpdates(locationListener);
		Log.e("LocalisationService","Arret du service de localisation");
		super.onDestroy();
	}
}
