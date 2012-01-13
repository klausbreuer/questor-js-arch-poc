package de.questor.poc.jsarch.net;

import java.util.HashMap;

import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.simulator.SimulatorRuntime;

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
							long id = snet.listen();

							System.err.println("listen successful");
							if (listenRunnable != null)
								listenRunnable
										.run(RemoteMessageServiceServer.this);

							new ReceiveThread(id).start();
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
			snet.send(id, new String[] { sessionId, msg });
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

	class ReceiveThread extends Thread {
		long connectionId;
		
		ReceiveThread(long id) {
			connectionId = id;
		}

		public void run() {
			try {
				String sessionId = null;
				String[] strings = null;

				// First message contains the information about our session.
				if ((strings = snet.receive(connectionId)) != null) {
					// Makes a link between a sessionName and the connection.
					sessionId = "questorSession-" + String.valueOf(connectionId);
					sessions.put(sessionId, connectionId);

					sendToSimulator(sessionId, strings[1]);
				}

				while ((strings = snet.receive(connectionId)) != null) {
					// NOTE: We could do a check whether this connection really belongs to the
					// given session etc.
					
					sendToSimulator(strings[0], strings[1]);
				}
				
				System.err.println("Receiving ended. Closing connection.");
				sessions.remove(sessionId);
				sendToSimulator(sessionId, null);
				snet.close(connectionId);

			} catch (Exception e) {
				System.err.println("Listen failed: " + e);

				return;
			}

		}
	}
	
}
