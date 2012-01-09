package de.questor.poc.jsarch.renderer.html;

import android.content.Context;
import android.content.Intent;

public class HtmlDelegate {
	
	private Context context;
	
	public HtmlDelegate(Context ctx) {
		context = ctx;
	}
	
	public void show(String pContent) {
		Intent i = new Intent(context, HtmlActivity.class);
		i.putExtra("content", pContent);
		context.startActivity(i);
	}
}
