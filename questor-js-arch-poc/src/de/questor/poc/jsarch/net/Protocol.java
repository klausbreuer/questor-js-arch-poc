package de.questor.poc.jsarch.net;

public interface Protocol {
	public static String PROTOCOL_NAME = "QUESTOR";
	public static String PROTOCOL_VERSION = "1.0";
	public static String ACK = "ACK";

	public static String REQUEST_NEW_SESSION = "NEW_SESSION";
	public static String REQUEST_REVIVE_SESSION = "REVIVE_SESSION";
}
