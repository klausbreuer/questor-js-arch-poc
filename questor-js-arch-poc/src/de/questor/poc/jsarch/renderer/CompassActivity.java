package de.questor.poc.jsarch.renderer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
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

/**
 * Simple Activity wrapper that hosts a {@link CompassView}
 * 
 */
@SuppressWarnings("serial")
public class CompassActivity extends Activity implements OnChoiceListener {

	private static final String IMG_NAME_BACKGROUND = "background";

	private static final int TIMEOUTCONNECTION = 3000;
	private static final int TIMEOUTSOCKET = 3000;

	private static final int LOCATION_UPDATE_INTERVAL_MILLIS = 1000;
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

	private String myUpdatePositionUrl;
	private String myRemovePositionUrl;
	private String myGetTargetsUrl;

	private HttpParams httpParameters = new BasicHttpParams();

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

		myUpdatePositionUrl = getResources().getString(R.string.StoryServerUrl) + "?mode=updateLocation";
		myRemovePositionUrl = getResources().getString(R.string.StoryServerUrl) + "?mode=removeLocation";
		myGetTargetsUrl = getResources().getString(R.string.StoryServerUrl) + "?mode=getLocations";

		if (compassMode.equals("serverTargets") || compassMode.equals("poiTeamConquest")) {
			// shows the positions of the other players
			TelephonyManager tManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
			imei = tManager.getDeviceId();
			// Log.i("klaus", imei);

			HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUTCONNECTION);
			HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUTSOCKET);

			controllerTreadCanRun = true;
			controllerThread = new Thread(new ControllerThread());
			controllerThread.start();
		}

	}

	private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent i) {
			
			GeoPoint myPoint;
			
			poiPos = (String) i.getSerializableExtra("poiPos");
			Log.i("klaus", "CompassActivity: " + poiPos);

			String[] values = poiPos.split(",");
			myPoint = new GeoPoint(Integer.parseInt(values[1]), Integer.parseInt(values[2]));
			mCompass.updateTarget(1, values[0], myPoint, (int) Long.parseLong(values[3], 16));

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

					try {
						HttpGet request = new HttpGet(myUpdatePositionUrl + "&id=" + imei + "&lat="
								+ ((Integer) currentLocationPoint.getLatitudeE6()).toString() + "&lon="
								+ ((Integer) currentLocationPoint.getLongitudeE6()).toString());
						HttpClient client = new DefaultHttpClient(httpParameters);
						client.execute(request);
						// Log.i("klaus", "sending position...");
					} catch (Exception e) {
						mCompass.setErrorMessage("http timeout!");
						Log.i("klaus", "error sending position: " + e.toString());
					}

					// 2. get the positions of all the other players from the
					// server and show them
					// in the compass:
					try {
						HttpGet request = new HttpGet(myGetTargetsUrl);
						HttpClient client = new DefaultHttpClient(httpParameters);
						HttpResponse response = client.execute(request);
						in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
						String line = "";
						line = in.readLine();
						// Log.i("klaus", line);
						// we hide all the other player-targets in the compass
						// by "deactivting" them:
						mCompass.deactivateAllTargets(2);
						// we insert all targets we got from the sever
						// (id,lat,lon,col~id,lat,lon,col...)
						String[] targets = line.split("~");
						for (int i = 0; i < targets.length; i++) {
							String[] values = targets[i].split(",");
							if (!values[0].equals(imei)) {
								myPoint = new GeoPoint(Integer.parseInt(values[1]), Integer.parseInt(values[2]));
								mCompass.updateTarget(2, values[0], myPoint, (int) Long.parseLong(values[3], 16));
							}
						}
					} catch (Exception e) {
						// if we have a problem with the connection, we
						// deactivate all other players.
						// Otherwise we would have them hanging around somewhere
						// on the screen, where they may not be any more...
						mCompass.deactivateAllTargets(2);
						mCompass.setErrorMessage("http timeout!");
						Log.i("klaus", "error getting positions: " + e.toString());
					}

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

		registerReceiver(myReceiver, new IntentFilter("de.questor.poc.jsarch.poiPos"));

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

		unregisterReceiver(myReceiver);

		mSensorManager.unregisterListener(mCompass);
		mLocationManager.removeUpdates(mCompass);

		// Stop animating the compass screen
		mCompass.stopSweep();

		if (compassMode.equals("serverTargets") || compassMode.equals("poiTeamConquest")) {

			// If we are in poiTeamConquest mode, we first send our last
			// position to the server:
			GeoPoint currentLocationPoint = null;
			currentLocationPoint = mCompass.getCurrentLocationPoint();
			if (currentLocationPoint != null) {
				try {
					HttpGet request = new HttpGet(myUpdatePositionUrl + "&id=" + imei + "&lat="
							+ ((Integer) currentLocationPoint.getLatitudeE6()).toString() + "&lon="
							+ ((Integer) currentLocationPoint.getLongitudeE6()).toString());
					HttpClient client = new DefaultHttpClient(httpParameters);
					client.execute(request);
					// Log.i("klaus", "sending position for the last time...");
				} catch (Exception e) {
					Log.i("klaus", "error sending position: " + e.toString());
				}
			}

			// After that, we send a remove-position-request to the server.
			// The position will be delete from the server some seconds later..
			try {
				HttpGet request = new HttpGet(myRemovePositionUrl + "&id=" + imei);
				HttpClient client = new DefaultHttpClient(httpParameters);
				client.execute(request);
				// Log.i("klaus", "sending delete request...");
			} catch (Exception e) {
				Log.i("klaus", "error removing position: " + e.toString());
			}
		}

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