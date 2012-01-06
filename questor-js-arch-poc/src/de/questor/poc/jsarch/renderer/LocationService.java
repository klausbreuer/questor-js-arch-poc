package de.questor.poc.jsarch.renderer;

import java.util.HashSet;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import de.questor.poc.jsarch.Interpreter;

public class LocationService implements LocationListener {
	
	private static LocationService INSTANCE;
	
	/**
	 * Names of the javascript object that will receive the
	 * coordinates through its 'onLocationChanged'() method.
	 */
	HashSet<String> targets = new HashSet<String>();
	
	Interpreter interpreter;
	
	public LocationService(Interpreter interpreter) {
		INSTANCE = this;
		this.interpreter = interpreter;
	}
	
	public static LocationService getInstance() {
		if (INSTANCE == null) {
			throw new IllegalStateException("Accessing the class too early.");
		}
		
		return INSTANCE;
	}
	
	public void addTarget(String target) {
		targets.add(target);
	}
	
	public void removeTarget(String target) {
		targets.remove(target);
	}

	@Override
	public void onLocationChanged(Location location) {
		int lon = (int) (location.getLongitude() * 1E6); 
		int lat = (int) (location.getLatitude() * 1E6);
		
		for (String t : targets)
		{
			interpreter.eval(String.format("%s.onLocationChanged(%s, %s);", t, lon, lat));
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
