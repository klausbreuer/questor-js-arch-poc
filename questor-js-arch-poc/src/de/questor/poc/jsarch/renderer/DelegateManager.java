package de.questor.poc.jsarch.renderer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DelegateManager {
	
	private HashMap<String, Object> delegates = new HashMap<String, Object>();
	
	void registerDelegate(String name, Object object) {
		delegates.put(name, object);
	}
	
	Iterator<Map.Entry<String, Object>> iterator() {
		return delegates.entrySet().iterator();
	}

}
