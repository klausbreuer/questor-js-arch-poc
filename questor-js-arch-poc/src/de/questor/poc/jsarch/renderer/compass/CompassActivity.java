package de.questor.poc.jsarch.renderer.compass;

import java.io.BufferedReader;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;

import de.questor.poc.jsarch.R;
import de.questor.poc.jsarch.renderer.Choice;
import de.questor.poc.jsarch.renderer.LocationService;
import de.questor.poc.jsarch.renderer.OnChoiceListener;

/**
 * Simple Activity wrapper that hosts a {@link CompassView}
 * 
 */
@SuppressWarnings("serial")
public class CompassActivity extends Activity implements OnChoiceListener {

	static final String UPDATE_PLAYER_POSITION_INTENT = "de.questor.poc.jsarch.UpdatePlayerPosition";

	static final String UPDATE_POI_POSITION_INTENT = "de.questor.poc.jsarch.UpdatePoiPosition";

	private static final int MENU_STANDARD = Menu.FIRST + 1;
	private static final int MENU_METRIC = Menu.FIRST + 2;
	private static final String COMPASS = "compass";
	private static final String PREF_METRIC = "metric";

	private String compassMode = "poiTeamConquest";

	private SensorManager mSensorManager;
	// private Sensor mAccelerometer;
	// private CompassView mCompass;
	private CompassMultiView mCompass;
	private LocationManager mLocationManager;
	private SharedPreferences mPrefs;

	private String poiPos;

	private String imei;

	private Thread controllerThread;
	private boolean controllerTreadCanRun = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.compass);
		// mCompass = (CompassView) findViewById(R.id.compass);
		mCompass = (CompassMultiView) findViewById(R.id.compass);
		mCompass.addOnChoiceListener(this);

		// If the compass is on, the device should not fall to sleep:
		mCompass.setKeepScreenOn(true);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		// mAccelerometer =
		// mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Metric or standard units?
		mPrefs = getSharedPreferences(COMPASS, MODE_PRIVATE);
		boolean useMetric = mPrefs.getBoolean(PREF_METRIC, false);
		// Klaus: we always want metric as default:
		// mCompass.setUseMetric(useMetric);
		mCompass.setUseMetric(true);
		mCompass.setDistanceView((TextView) findViewById(R.id.distance));

		/*
		 * // Read the station object from our intent
		 * 
		 * mStation = (CompassStation) i.getSerializableExtra("station");
		 * mCompass.initForStation(mStation);
		 * 
		 * // if a orientation is given in the story.xml we set it:
		 * checkOrientation(mStation);
		 * 
		 * // we set the background: ((LinearLayout)
		 * findViewById(R.id.compassLayout
		 * )).setBackgroundDrawable(Utility.findDrawableByTag( getResources(),
		 * mStation, IMG_NAME_BACKGROUND));
		 */

		if (compassMode.equals("serverTargets") || compassMode.equals("poiTeamConquest")) {
			// shows the positions of the other players
			TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tManager.getDeviceId();
			// Log.i("klaus", imei);

			controllerTreadCanRun = true;
			controllerThread = new Thread(new ControllerThread());
			controllerThread.start();
		}

	}

	private final BroadcastReceiver poiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent i) {
			GeoPoint myPoint;
			
			String id = i.getStringExtra("id");
			int lonE6 = i.getIntExtra("lonE6", -1);
			int latE6 = i.getIntExtra("latE6", -1);
			int color = i.getIntExtra("color", -1);

			myPoint = new GeoPoint(lonE6, latE6);
			mCompass.updateTarget(1, id, myPoint, color);
		}
	};

	private final BroadcastReceiver playerReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent i) {
			GeoPoint myPoint;
			
			String id = i.getStringExtra("id");
			int lonE6 = i.getIntExtra("lonE6", -1);
			int latE6 = i.getIntExtra("latE6", -1);
			int color = i.getIntExtra("color", -1);

			myPoint = new GeoPoint(lonE6, latE6);
			mCompass.updateTarget(2, id, myPoint, color);
		}
	};

	public class ControllerThread extends Thread {

		GeoPoint currentLocationPoint = null;
		BufferedReader in = null;
		GeoPoint myPoint;

		@Override
		public void run() {
			while (controllerTreadCanRun) {
				// Log.i("klaus", "Thread Loop!");
				if (isInterrupted()) {
					// Log.i("klaus", "Thread interrupted!");
					break;
				}

				currentLocationPoint = mCompass.getCurrentLocationPoint();

				if (currentLocationPoint != null) {

					// 1. we send the own position to the server:
					// Note: Done through JavaScript already ... 

					// 2. location of other players arrives via Android messaging

					mCompass.calcDistanceAndBearingOfTargets();

				}

				try {
					Thread.sleep(3000);
					// Log.i("klaus", "3 sekunden schlaf beendet");
					mCompass.setErrorMessage("");
				} catch (InterruptedException e) {
					// Log.i("klaus", "InterruptedException!");
					interrupt();
				}

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		registerReceiver(poiReceiver, new IntentFilter(UPDATE_POI_POSITION_INTENT));
		registerReceiver(playerReceiver, new IntentFilter(UPDATE_PLAYER_POSITION_INTENT));

		mSensorManager.registerListener(mCompass, SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_GAME);
		// mSensorManager.registerListener(mCompass, mAccelerometer,
		// SensorManager.SENSOR_DELAY_GAME);

		// Start animating the compass screen
		mCompass.startSweep();

		// Register for location updates
		// mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// LOCATION_UPDATE_INTERVAL_MILLIS, 1,mCompass);
		// mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		// LOCATION_UPDATE_INTERVAL_MILLIS, 1,mCompass);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mCompass);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mCompass);

		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LocationService.getInstance());

		if (compassMode.equals("serverTargets") || compassMode.equals("poiTeamConquest")) {
			if (controllerThread.getState() == Thread.State.TERMINATED) {
				controllerTreadCanRun = true;
				controllerThread = new Thread(new ControllerThread());
				controllerThread.start();
			}
		}

	}

	@Override
	protected void onPause() {
		controllerTreadCanRun = false;

		unregisterReceiver(poiReceiver);
		unregisterReceiver(playerReceiver);

		mSensorManager.unregisterListener(mCompass);
		mLocationManager.removeUpdates(mCompass);
		mLocationManager.removeUpdates(LocationService.getInstance());

		// Stop animating the compass screen
		mCompass.stopSweep();

		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		/*
		 * menu.add(0, MENU_STANDARD, 0,
		 * R.string.menu_standard).setIcon(R.drawable.ic_menu_standard)
		 * .setAlphabeticShortcut('A'); menu.add(0, MENU_METRIC, 0,
		 * R.string.menu_metric
		 * ).setIcon(R.drawable.ic_menu_metric).setAlphabeticShortcut('C');
		 */
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_STANDARD: {
			setUseMetric(false);
			return true;
		}
		case MENU_METRIC: {
			setUseMetric(true);
			return true;
		}
		}

		return super.onOptionsItemSelected(item);
	}

	private void setUseMetric(boolean useMetric) {
		SharedPreferences.Editor e = mPrefs.edit();
		e.putBoolean(PREF_METRIC, useMetric);
		e.commit();
		mCompass.setUseMetric(useMetric);
	}

	public void onChoice(Choice choice) {
		Intent i = new Intent();
		i.putExtra("choice", choice);
		setResult(RESULT_OK, i);
		finish();
	}

}