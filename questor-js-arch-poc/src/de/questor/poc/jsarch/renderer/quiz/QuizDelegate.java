package de.questor.poc.jsarch.renderer.quiz;

import de.questor.poc.jsarch.renderer.html.HtmlDelegate;
import android.content.Context;
import android.content.Intent;

public class QuizDelegate {
	
	private Context context;
	
	private HtmlDelegate htmlDelegate;
	
	public QuizDelegate(Context ctx) {
		context = ctx;

		// ideally we would snatch this from the DelegateManager, too but 
		// for now that's OK.
		htmlDelegate = new HtmlDelegate(context);
	}
	
	public void showNative(String pQuestion) {
		Intent i = new Intent(context, QuizActivity.class);
		i.putExtra("question", pQuestion);
		context.startActivity(i);
		
	}
	
	public void showHtml(String html) {
		htmlDelegate.show(html);
	}
	
}
