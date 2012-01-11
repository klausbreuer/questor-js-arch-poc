package de.questor.poc.jsarch.net;

import de.questor.poc.jsarch.MessageService;
import de.questor.poc.jsarch.simulator.SimulatorRuntime;

public class RemoteMessageServiceServer implements MessageService {
	
	SimpleNet snet;
	
	SimulatorRuntime simulatorRuntime;
	
	public RemoteMessageServiceServer(SimulatorRuntime simulatorRuntime, int port) {
		this.simulatorRuntime = simulatorRuntime;
		
		try {
			snet = new SimpleNet(port);
			new Thread() {
				public void run() {
					try {
						snet.listen();

						System.err.println("listen successful");

						String[] strings = null;
						
						while ((strings = snet.receive()) != null) {
							sendToSimulator(strings[0], strings[1]);
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
		try {
			snet.send(new String[] { (String) contextKey, msg });
		} catch (Exception e) {
			System.err.println("Failed: " + e);
		}
	}

	@Override
	public void sendToSimulator(Object contextKey, String msg) {
		simulatorRuntime.onMessage(contextKey, msg);
	}

}
