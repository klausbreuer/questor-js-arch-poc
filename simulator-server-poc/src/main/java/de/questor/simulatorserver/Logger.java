package de.questor.simulatorserver;


/**
 * Logging class which can be used for Javascript debugging using Android's logging
 * infrastructure.
 * 
 * @author Robert Schuster <r.schuster@tarent.de>
 *
 */
public class Logger {
	
	private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(Logger.class.getName());
	
	public Logger() {
	}
	
	public void v(String msg) {
		log.finer(msg);
	}
	
	public void d(String msg) {
		log.finest(msg);
	}

	public void i(String msg) {
		log.fine(msg);
	}
	
	public void w(String msg) {
		log.warning(msg);
	}
	
	public void e(String msg) {
		log.severe(msg);
	}
	
}
