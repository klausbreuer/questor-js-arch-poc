package de.questor.poc.jsarch.net;

import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.QuestorContext;
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
	public void sendToRenderer(Object contextKey, String msg) {
		rendererRuntime.onMessage(new AnswerContext(contextKey), msg);
	}

	@Override
	public void sendToSimulator(Object contextKey, String msg) {
		try {
			snet.send(connectionId, new String[] { (String) contextKey, msg });
		} catch (Exception e) {
			System.err.println("Failed: " + e);
		}
	}

	/**
	 * By sending messages to the {@link RendererRuntime} using this class the
	 * called service can reply without knowing the actual <code>contextKey</code>
	 * instance.
	 * 
	 * <p>In a real networked implementation this mechanism is also supposed to be
	 * implemented on the client-side of the network.</p>
	 * 
	 * @author Robert Schuster <r.schuster@tarent.de>
	 *
	 */
	private class AnswerContext implements QuestorContext {
		
		private Object contextKey;
		
		AnswerContext(Object contextKey) {
			this.contextKey = contextKey;
		}

		@Override
		public void sendMessage(String msg) {
			RemoteMessageServiceClient.this.sendToSimulator(contextKey, msg);
		}
		
	}
	
	public static interface Runnable {
		void run(RemoteMessageServiceClient rmsc);
	}

}
