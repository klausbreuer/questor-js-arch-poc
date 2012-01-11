package de.questor.simulatorserver.net;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SimpleNet {
	
	ServerSocket serverSocket;
	
	Socket socket;
	
	PrintWriter sender;
	
	BufferedReader receiver;
	
	String host;
	
	int port;
	
	SimpleNet(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	SimpleNet(int port) {
		this.port = port;
	}
	
	void listen() throws Exception {
		serverSocket = new ServerSocket(port);
		socket = serverSocket.accept();
		sender = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
		receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	void connect() throws Exception {
		socket = new Socket(host, port);
		sender = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()));
		receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	void send(String[] rawMessage) throws Exception {
		sender.println("START");
		for (String s : rawMessage)
			sender.println(s);
		sender.println("END");
		sender.flush();
	}
	
	String[] receive() throws Exception {
		String r = null;
		
		while ("START".equals(r != receiver.readLine())) {
			System.err.println("discarding: " + r);
		}
		
		ArrayList<String> strings = new ArrayList<String>();
		
		while(!"END".equals(r = receiver.readLine())) {
			strings.add(r);
		}
		
		return strings.toArray(new String[strings.size()]);
	}
	
	void close() {
		try {
			if (serverSocket != null)
				serverSocket.close();
		} catch (Exception e) { }

		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) { }
	}

}
