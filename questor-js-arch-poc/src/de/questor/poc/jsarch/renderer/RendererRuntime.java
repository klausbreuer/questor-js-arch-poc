package de.questor.poc.jsarch.renderer;

import de.questor.poc.jsarch.QuestorContext;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class RendererRuntime {

	private static RendererRuntime INSTANCE = null;

	private static final String TAG = "Renderer";
	private static Context mContext;
	private static QuestorContext mQuestorContext;

	public static RendererRuntime getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RendererRuntime();
		}
		return INSTANCE;
	}

	public void setContext(Context pContext) {
		mContext = pContext;
	}

	public void setQuestorContext(QuestorContext pQuestorContext) {
		mQuestorContext = pQuestorContext;
	}

	public void showToast(String toast) {
		Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	}
	
	
	public void showQuizStation(String pQuestion) {
		Log.i(TAG, "showQuizStation");
		Intent i = new Intent(mContext, QuizActivity.class);
		i.putExtra("question", pQuestion);
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
		if (mQuestorContext == null) {
			Log.i(TAG, "error: mQuestorContext is null!");
		}
		else {
			mQuestorContext.sendMessage(msg);
		}
	}

}
