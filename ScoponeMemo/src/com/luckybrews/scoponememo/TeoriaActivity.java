package com.luckybrews.scoponememo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml.Encoding;
import android.webkit.WebView;
import com.luckybrews.scoponememo.R;

public class TeoriaActivity extends Activity {

	private static final String TAG = "ActivityCredits";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teoria);

		Log.d(TAG, "Displaying THEORY");

		// WebView
		WebView webview = (WebView) findViewById(R.id.webteoria);

		String teoria = loadFileTeoria();
		// credits = tweakHtml(credits);
		//credits = "<html><head><link href=\"stile.css\" rel=\"stylesheet\" type=\"text/css\" /></head><body>" + credits + "</body></html>";
		webview.loadDataWithBaseURL("file:///android_asset/", teoria, "text/html", Encoding.UTF_8.toString(), null);
	}
	
	
	public String loadFileTeoria() {

		AssetManager am = this.getAssets();
		DataInputStream in = null;
		String ret = "";

		try {
			in = new DataInputStream(new BufferedInputStream(am.open("teoria.html")));

			while (in.available() != 0) {
				ret += in.readLine() + "\n";			
			}

		} catch (IOException e) {
			Log.e(TAG, "Error opening teoria.html");
			e.printStackTrace();
		}
		
		return ret;
	}
	
}