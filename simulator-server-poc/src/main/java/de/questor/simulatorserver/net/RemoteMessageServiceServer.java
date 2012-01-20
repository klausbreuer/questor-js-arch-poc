package de.questor.simulatorserver.net;

import java.util.HashMap;

import de.questor.simulatorserver.MessageService;
import de.questor.simulatorserver.SimulatorRuntime;

public class RemoteMessageServiceServer implements MessageService {

	SimpleNet snet;

	SimulatorRuntime simulatorRuntime;

	HashMap<String, Long> sessions = new HashMap<String, Long>();
	
	public RemoteMessageServiceServer(SimulatorRuntime simulatorRuntime,
			final Runnable listenRunnable, int port) {
		this.simulatorRuntime = simulatorRuntime;

		try {
			snet = new SimpleNet(port);
			new Thread() {
				public void run() {
					try {
						while (true) {
							long connectionId = snet.listen();
							System.err.println("listen successful");
							String sessionId = handshake(snet, connectionId);
							System.err.println("server-side handshake successful");
							
							if (listenRunnable != null)
								listenRunnable.run(RemoteMessageServiceServer.this);

							new ReceiveThread(sessionId, connectionId).start();
						}
					} catch (Exception e) {
						System.err.println("Listen failed: " + e);

						return;
					}

				}
			}.start();
		} catch (Exception e) {
			System.err.println("Failed: " + e);
		}
	}

	@Override
	public void sendToRenderer(String sessionId, String msg) {
		long id = sessions.get(sessionId);
		try {
			snet.send(id, msg);
		} catch (Exception e) {
			System.err.println("Failed send: " + e);
		}
	}

	@Override
	public void sendToSimulator(String sessionId, String msg) {
		simulatorRuntime.onMessage(sessionId, msg);
	}

	public static interface Runnable {
		void run(RemoteMessageServiceServer rmss);
	}
	
	private String handshake(SimpleNet snet, long connectionId) throws Exception {
		String sessionId = null;
		
		// First step: Expect proper identification by client
		String result[] = snet.receive(connectionId);
		if (result.length == 2
				&& Protocol.PROTOCOL_NAME.equals(result[0])
				&& Protocol.PROTOCOL_VERSION.equals(result[1])) {
			snet.send(connectionId, Protocol.ACK);
		} else {
			throw new Exception("Client did not behave according to protocol.");
		}

		// Second step: Handle client's request of how to deal with the session
		result = snet.receive(connectionId);
		if (result.length == 1 && Protocol.REQUEST_NEW_SESSION.equals(result[0])) {
            // Create a new sessionId and make a link to the connection id.
            sessionId = "questorSession-" + String.valueOf(connectionId);
            sessions.put(sessionId, connectionId);
		} else if (result.length == 2 && Protocol.REQUEST_REVIVE_SESSION.equals(result[0])) {
			String prevSessionId = result[1];
			Long oldConnectionId = sessions.get(prevSessionId);
			if (oldConnectionId != null) {
				// TODO: Some kind of proof must be requested from client before allowing it
				// to become the owner of that session.
				sessionId = prevSessionId;
				
				// Let SimpleNet discard the old connection
				snet.close(oldConnectionId);
				
				// Update our look-up table with the new data
				sessions.put(sessionId, connectionId);
			} else {
	            // Create a new sessionId and make a link to the connection id.
	            sessionId = "questorSession-" + String.valueOf(connectionId);
	            sessions.put(sessionId, connectionId);
			}
		} else {
			throw new Exception("Client did not behave according to protocol.");
		}
		
		// Send an ACK and the sessionId that is used (regardless of whether it is old or not)
		snet.send(connectionId, Protocol.ACK, sessionId);
	
		return sessionId;
	}

	class ReceiveThread extends Thread {
		long connectionId;
		String sessionId;
		
		ReceiveThread(String sessionId, long id) {
			this.sessionId = sessionId;
			connectionId = id;
		}

		public void run() {
			try {
				String[] strings = null;

				while ((strings = snet.receive(connectionId)) != null) {
					// NOTE: We could do a check whether this connection really belongs to the
					// given session etc.
					
					sendToSimulator(sessionId, strings[0]);
				}
				
				System.err.println("Receiving ended. Closing connection.");
				sendToSimulator(sessionId, null);
			} catch (Exception e) {
				snet.close(connectionId);
				System.err.println("Listen failed: " + e);

				return;
			} finally {
				sessions.remove(sessionId);
				snet.close(connectionId);
			}

		}
	}
	
}
