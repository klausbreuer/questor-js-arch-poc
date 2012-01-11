package de.questor.simulatorserver;

/**
 * The {@link MessageService} manages the transfer of messages between the 
 * renderer and the simulator.
 * 
 * <p>Depending on the actual implementation this involves network transfer or
 * not.</p>
 * 
 * @author Robert Schuster <r.schuster@tarent.de>
 *
 */
public interface MessageService {
	
	/**
	 * Sends a message to the renderer.
	 * 
	 * <p>The <code>contextKey</code> argument allows the renderer
	 * to send replies to the origin of this message.</p>
	 * 
	 * @param contextKey
	 * @param msg
	 */
	void sendToRenderer(Object contextKey, String msg);
	
	/**
	 * Sends a message to the simulator.
	 * 
	 * <p>The <code>contextKey</code> argument needs to valid in the
	 * simulator for the message to have a meaningful effect.</p>
	 * 
	 * @param contextKey
	 * @param msg
	 */
	void sendToSimulator(Object contextKey, String msg);
	
}
