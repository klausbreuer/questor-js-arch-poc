package de.questor.poc.jsarch.simulator;

import android.util.Log;

/**
 * Logging class which can be used for Javascript debugging using Android's logging
 * infrastructure.
 * 
 * @author Robert Schuster <r.schuster@tarent.de>
 *
 */
public class Logger {
	
	private String tag;
	
	public Logger(String tag) {
		this.tag = tag;
	}
	
	public void v(String msg) {
		Log.v(tag, msg);
	}
	
	public void d(String msg) {
		Log.d(tag, msg);
	}

	public void i(String msg) {
		Log.i(tag, msg);
	}
	
	public void w(String msg) {
		Log.w(tag, msg);
	}
	
	public void e(String msg) {
		Log.e(tag, msg);
	}
	
}
