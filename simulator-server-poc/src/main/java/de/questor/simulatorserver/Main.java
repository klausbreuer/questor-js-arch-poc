package de.questor.simulatorserver;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

public class Main {
	
	public static void main(String... args) throws Exception {
		main2(args);
	}

	public static void main2(String... args) throws Exception {

	    // initialization
		Context context = ContextFactory.getGlobal().enterContext();
		Scriptable scope = context.initStandardObjects();

		SimulatorRuntime runtime = new SimulatorRuntime(context, scope);

	    // add bindings
		scope.put("logger", scope, Context.javaToJS(new Logger(), scope));
		scope.put("runtime", scope, Context.javaToJS(runtime, scope));
	    
		eval(context, scope, getReader("rhino.js"));
		eval(context, scope, getReader("json2.js"));
		
	    eval(context, scope, getReader("common.js"));
	    eval(context, scope, getReader("simulator.js"));
	    
	    // Station code
	    eval(context, scope, getReader("compassstation.js"));
	    eval(context, scope, getReader("endstation.js"));
	    eval(context, scope, getReader("htmlstation.js"));
	    eval(context, scope, getReader("quizstation.js"));

	    eval(context, scope, getReader("environment.js"));
	    
	    if (runtime.isInitialized()) {
	    	System.out.println("Runtime properly initialized.");
	    } else
	    {
	    	System.out.println("Failed to set up runtime.");
	    }

	    eval(context, scope, "simulator = new Simulator();");

	    eval(context, scope, "checkSimulator();");
	    
	    eval(context, scope, "logger.i('hello world');");
	    
	    context.exit();
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
