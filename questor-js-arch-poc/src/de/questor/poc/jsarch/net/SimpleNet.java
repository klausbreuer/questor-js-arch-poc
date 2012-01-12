package de.questor.poc.jsarch.net;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class SimpleNet {
	
	ServerSocket serverSocket;
	
	String host;
	
	int port;
	
	HashMap<Long, Connection> connections = new HashMap<Long, Connection>();
	
	SimpleNet(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	SimpleNet(int port) {
		this.port = port;
	}
	
	long listen() throws Exception {
		if (serverSocket == null) {
			serverSocket = new ServerSocket(port);
		}
		
		Connection c = new Connection();
		c.socket = serverSocket.accept();
		c.sender = new PrintWriter(new BufferedOutputStream(c.socket.getOutputStream()));
		c.receiver = new BufferedReader(new InputStreamReader(c.socket.getInputStream()));
		
		connections.put(c.id, c);
		
		return c.id;
	}
	
	long connect() throws Exception {
		Connection c = new Connection();
		
		c.socket = new Socket(host, port);
		c.sender = new PrintWriter(new BufferedOutputStream(c.socket.getOutputStream()));
		c.receiver = new BufferedReader(new InputStreamReader(c.socket.getInputStream()));
		
		connections.put(c.id, c);
		
		return c.id;
	}
	
	void send(long id, String[] rawMessage) throws Exception {
		Connection c = connections.get(id);
		
		c.sender.println("START");
		for (String s : rawMessage)
			c.sender.println(s);
		c.sender.println("END");
		c.sender.flush();
	}
	
	String[] receive(long id) throws Exception {
		Connection c = connections.get(id);
		
		String r = null;
		
		while ("START".equals(r != c.receiver.readLine())) {
			System.err.println("discarding: " + r);
		}
		
		ArrayList<String> strings = new ArrayList<String>();
		
		while(!"END".equals(r = c.receiver.readLine())) {
			strings.add(r);
		}
		
		return strings.toArray(new String[strings.size()]);
	}
	
	void close(long id) {
		Connection c = connections.remove(id);

		try {
			if (c.socket != null)
				c.socket.close();
		} catch (Exception e) { }
	}
	
	void closeServer() {
		try {
			if (serverSocket != null)
				serverSocket.close();
			
		} catch (Exception e) { }
		finally {
			serverSocket = null;
		}
	}

	static class Connection {
		Socket socket;
		PrintWriter sender;
		BufferedReader receiver;
		
		long id;
		
		Connection() {
			id = System.nanoTime();
		}
		
	}
	
}
