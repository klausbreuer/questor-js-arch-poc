package de.questor.poc.jsarch;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class RendererRuntime {
	
	private static final String TAG = "Renderer";
	Context mContext;
	
	public RendererRuntime(Context pContext) {
		mContext = pContext;
	}

	public void showToast(String toast) {
		Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	}

	public void showQuizStation(String pQuestion) {
		Log.i(TAG, "showQuizStation");
		Intent i = new Intent(mContext, QuizActivity.class);
		i.putExtra("question", pQuestion);
		//((Activity)mContext).startActivityForResult(i, SHOW_STORY_NODE_REQUEST);
		mContext.startActivity(i);
	}
	
	public void showHtmlStation(String pContent) {
		Log.i(TAG, "showHTMLStation");

		Intent i = new Intent(mContext, HtmlActivity.class);
		i.putExtra("content", pContent);
		mContext.startActivity(i);
	}
	
	/**
	 * A messaging function that is called from Javascript which allows sending
	 * a reply to the simulator.
	 * 
	 * @param msg
	 */
	public void sendReply(String msg) {
		Log.i(TAG, "reply: " + msg);
		//questorContext.sendMessage(msg);
	}
	

}
