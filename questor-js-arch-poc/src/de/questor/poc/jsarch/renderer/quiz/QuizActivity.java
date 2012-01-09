package de.questor.poc.jsarch.renderer.quiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import de.questor.poc.jsarch.R;
import de.questor.poc.jsarch.renderer.RendererRuntime;

public class QuizActivity extends Activity {

	private static final String IMG_NAME_BACKGROUND = "background";
	public static final String AUD_BACKGROUND = "audiobg";

	private String mQuestion;
	private WebView mWebView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Get the question from our intent:
		Intent i = getIntent();
		mQuestion = (String) i.getSerializableExtra("question");

		setContentView(R.layout.quiz);

		// set the background:
		Drawable bg = getResources().getDrawable(R.drawable.background);
		findViewById(R.id.quizLayout).setBackgroundDrawable(bg);
		
		mWebView = (WebView) findViewById(R.id.webViewQuizQuestion);
		mWebView.setBackgroundColor(0x00000000);
		mWebView.loadData("<div style='padding:10px'> " + mQuestion + "</div>", "text/html", "UTF-8");

		final EditText mEdittext = (EditText) findViewById(R.id.editQuizAnswer);

		final Button mButton = (Button) findViewById(R.id.buttonSubmitQuizAnswer);

		mButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String inputUpcase = mEdittext.getText().toString();
				RendererRuntime.getInstance().sendReply(inputUpcase);
				finish();
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
	   return;
	}	
	

}
