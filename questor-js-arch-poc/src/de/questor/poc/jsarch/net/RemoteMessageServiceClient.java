package de.questor.poc.jsarch.net;

import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.renderer.RendererRuntime;

public class RemoteMessageServiceClient implements MessageService {
	
	private SimpleNet snet;
	
	private RendererRuntime rendererRuntime;
	
	private long connectionId;
	
	private String currentSessionId;
	
	public RemoteMessageServiceClient(RendererRuntime rendererRuntime, final Runnable connectRunnable, String host, int port) {
		this.rendererRuntime = rendererRuntime;
		
		try {
			snet = new SimpleNet(host, port);
			new Thread() {
				public void run() {
					try {
						connectionId = snet.connect();
						System.err.println("connected");
						
						// Negotiate a session id
						currentSessionId = handshake(snet, connectionId, currentSessionId);
						System.err.println("client-side handshake OK");
						
						// If reached this point, run the code that informs the caller about a successful
						// connection establishment.
						if (connectRunnable != null)
							connectRunnable.run(RemoteMessageServiceClient.this);
						
						String[] strings = null;
						
						while ((strings = snet.receive(connectionId)) != null) {
							sendToRenderer(currentSessionId, strings[0]);
						}
						
					} catch (Exception e) {
						snet.close(connectionId);
						System.err.println("Listen failed: " + e);
						
						return;
					}
					
					
				}
			}.start();
		} catch (Exception e) {
			System.err.println("Failed: " + e);
		}
	}
	
	/**
	 * Does a handshake between server and client part which includes a session revival attempt.
	 * 
	 * TODO: For the session revival to work properly the Renderer and Simulator need to support
	 * this more deeply.
	 */
	private String handshake(SimpleNet snet, long connectionId, String previousSessionId) throws Exception {
		String sessionId = null;
		
		// First step: Identify ourself, expect an ACK
		snet.send(connectionId, Protocol.PROTOCOL_NAME, Protocol.PROTOCOL_VERSION);
		String[] result = snet.receive(connectionId);
		if (!(result.length == 1 && Protocol.ACK.equals(result[0]))) {
			throw new Exception("Server did not react according to the protocol.");
		}
		
		// Second step: Request session creation or revival.
		if (previousSessionId == null) {
			snet.send(connectionId, Protocol.REQUEST_NEW_SESSION);
		} else {
			snet.send(connectionId, Protocol.REQUEST_REVIVE_SESSION, previousSessionId);
		}
		
		// The server either hands out a new session or the same. For us the behavior is the same.
		result = snet.receive(connectionId);
		if (result.length == 2 && Protocol.ACK.equals(result[0])) {
			sessionId = result[1];
		} else {
			throw new Exception("Server did not react according to the protocol.");
		}
		
		// Purely informational
		if (sessionId.equals(previousSessionId)) {
			System.err.println("Server let us revive a previous session.");
		}
		
		return sessionId;
	}
	
	@Override
	public void sendToRenderer(String sessionId, String msg) {
		rendererRuntime.onMessage(sessionId, msg);
	}

	@Override
	public void sendToSimulator(String sessionId, String msg) {
		if (msg == null) {
			System.err.println("Message is null. Terminating connection.");
			snet.close(connectionId);
		} else {
			try {
				snet.send(connectionId, msg);
			} catch (Exception e) {
				System.err.println("Failed: " + e);
			}
		}
	}
	
	public static interface Runnable {
		void run(RemoteMessageServiceClient rmsc);
	}

}
