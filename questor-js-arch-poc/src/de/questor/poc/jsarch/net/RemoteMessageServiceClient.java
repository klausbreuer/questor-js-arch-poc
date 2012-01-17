package de.questor.poc.jsarch.net;

import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.renderer.RendererRuntime;

public class RemoteMessageServiceClient implements MessageService {
	
	SimpleNet snet;
	
	RendererRuntime rendererRuntime;
	
	long connectionId;
	
	public RemoteMessageServiceClient(RendererRuntime rendererRuntime, final Runnable connectRunnable, String host, int port) {
		this.rendererRuntime = rendererRuntime;
		
		try {
			snet = new SimpleNet(host, port);
			new Thread() {
				public void run() {
					try {
						connectionId = snet.connect();
						
						System.err.println("connected");
						if (connectRunnable != null)
							connectRunnable.run(RemoteMessageServiceClient.this);
						
						String[] strings = null;
						
						while ((strings = snet.receive(connectionId)) != null) {
							sendToRenderer(strings[0], strings[1]);
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
		rendererRuntime.onMessage(sessionId, msg);
	}

	@Override
	public void sendToSimulator(String sessionId, String msg) {
		try {
			snet.send(connectionId, new String[] { sessionId, msg });
		} catch (Exception e) {
			System.err.println("Failed: " + e);
		}
	}
	
	public static interface Runnable {
		void run(RemoteMessageServiceClient rmsc);
	}

}
