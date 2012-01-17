package de.questor.simulatorserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

import de.questor.simulatorserver.net.RemoteMessageServiceServer;

public class Main {
	
	public static void main(String... args) throws Exception {
		main2(args);
	}

	public static void main2(String... args) throws Exception {
		
	    // initialization
		Context context = ContextFactory.getGlobal().enterContext();
		/*
		context.setOptimizationLevel(-1);
		context.setLanguageVersion(Context.VERSION_1_5);
		
		// Makes available 'print' and friends.
		Global global = new Global();
		global.init(context);
		*/
		Scriptable scope = context.initStandardObjects();

		SimulatorRuntime runtime = new SimulatorRuntime(context, scope);
		RemoteMessageServiceServer rmss = new RemoteMessageServiceServer(runtime, null, 10000);
		runtime.setMessageService(rmss);
		
	    // add bindings
		scope.put("logger", scope, Context.javaToJS(new Logger(), scope));
		scope.put("runtime", scope, Context.javaToJS(runtime, scope));
	    
		// Load scripts which make the non-Browser JavaScript environment
		// more compatible to one (JSON.parse(), setInterval(), ...)
		eval(context, scope, getReader("lib/rhino.js"));
		eval(context, scope, getReader("lib/json2.js"));
//		eval(context, scope, getReader("lib/env.rhino.1.2.js"));
		
		// Load the set of default functions.
		eval(context, scope, getReader("common.js"));
	    
	    // Load the simulator definition.
	    eval(context, scope, getReader("simulator.js"));
		eval(context, scope, getReader("attendeelist.js"));
	    
	    // Load the station code.
	    eval(context, scope, getReader("compassstation.js"));
	    eval(context, scope, getReader("endstation.js"));
	    eval(context, scope, getReader("htmlstation.js"));
	    eval(context, scope, getReader("quizstation.js"));

	    // Load the final environment code.
	    eval(context, scope, getReader("environment.js"));
	    
	    if (runtime.isInitialized()) {
	    	System.out.println("Runtime properly initialized.");
	    } else
	    {
	    	System.out.println("Failed to set up runtime.");
	    }

	    eval(context, scope, "simulator = new Simulator('Standalone Simulator Server PoC - V1.0');");

	    eval(context, scope, getReader("game1.js"));

	    eval(context, scope, "checkSimulator();");
	    
	    eval(context, scope, "logger.w('simulator object: ' + simulator);");
	}
	
	private static void eval(Context ctx, Scriptable scope, String s) {
		ctx.evaluateString(scope, s, "custom", 0, null);
	}

	private static void eval(Context ctx, Scriptable scope, Reader r) throws Exception {
		ctx.evaluateReader(scope, r, "custom", 0, null);
	}

	private static Reader getReader(String resource) throws Exception {
		InputStream is = Class.class
				.getResourceAsStream("/simulator/" + resource);
		
		return new BufferedReader(new InputStreamReader(is));
	}

}
