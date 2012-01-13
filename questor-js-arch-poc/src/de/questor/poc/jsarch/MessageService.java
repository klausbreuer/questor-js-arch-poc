package de.questor.poc.jsarch;

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
	 * @param sessionId
	 * @param msg
	 */
	void sendToRenderer(String sessionId, String msg);
	
	/**
	 * Sends a message to the simulator.
	 * 
	 * <p>The <code>contextKey</code> argument needs to valid in the
	 * simulator for the message to have a meaningful effect.</p>
	 * 
	 * @param sessionId
	 * @param msg
	 */
	void sendToSimulator(String sessionId, String msg);

}
