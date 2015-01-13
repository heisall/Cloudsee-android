package com.jovision.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.commons.MyLog;

public class JVWebView2Activity extends BaseActivity {

	/** topBar **/
	private RelativeLayout topBar;

	private RelativeLayout webviewLayout;

	private WebView webView;
	private String url = "";
	private int titleID = 0;
	private ProgressBar progressbar;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

	}

	@Override
	protected void initSettings() {
		url = getIntent().getStringExtra("URL");
		// url = "http://app.ys7.com/";
		titleID = getIntent().getIntExtra("title", 0);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initUi() {
		setContentView(R.layout.webview2_layout);
		webviewLayout = (RelativeLayout) findViewById(R.id.webviewlayout);
		/** topBar **/
		topBar = (RelativeLayout) findViewById(R.id.topbarh);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout)findViewById(R.id.alarmnet);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		if (-1 == titleID) {
			currentMenu.setText("");
		} else if (-2 == titleID) {

		} else {
			currentMenu.setText(titleID);
		}

		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);

		webView = (WebView) findViewById(R.id.findpasswebview);
		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebViewClient(new MyWebviewCient());
		// 设置setWebChromeClient对象
		webView.setWebChromeClient(new MyChromeClient());
		webView.requestFocus(View.FOCUS_DOWN);

		// setting.setPluginState(PluginState.ON);
		// 加快加载速度
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
				MyLog.v("new_url", newUrl);
				if (newUrl.contains("viewmode")) {
					Intent intentAD = new Intent(JVWebView2Activity.this,
							JVWebView2Activity.class);
					intentAD.putExtra("URL", newUrl);
					intentAD.putExtra("title", -2);
					JVWebView2Activity.this.startActivity(intentAD);
				} else {
					view.loadUrl(newUrl);
				}

				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				progressbar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				webView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
				progressbar.setVisibility(View.GONE);
				// webView.loadUrl("javascript:videopayer.play()");
			}

		});

		webView.loadUrl(url);
	}

	OnClickListener myOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				backMethod();
				break;
			}
		}
	};

	@Override
	public void onBackPressed() {
		backMethod();
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {
	}

	/**
	 * 返回事件
	 */
	private void backMethod() {
		if (webView.canGoBack()) {
			webView.goBack(); // goBack()表示返回WebView的上一页面
		} else {
			webView.onPause();
			JVWebView2Activity.this.finish();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		webView.onPause();
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// webView.onPause(); // 暂停网页中正在播放的视频
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.reload();
		webView.onResume();
	}

	private View myView = null;
	private WebChromeClient.CustomViewCallback myCallBack = null;

	public class MyWebviewCient extends WebViewClient {
		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view,
				String url) {
			WebResourceResponse response = null;
			response = super.shouldInterceptRequest(view, url);
			return response;
		}
	}

	public class MyChromeClient extends WebChromeClient {

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			if (-2 == titleID) {
				currentMenu.setText(title);
			}
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			if (myView != null) {
				callback.onCustomViewHidden();
				return;
			}
			webviewLayout.removeView(webView);
			webviewLayout.addView(view);
			myView = view;
			myCallBack = callback;
		}

		@Override
		public void onHideCustomView() {
			if (myView == null) {
				return;
			}
			webviewLayout.removeView(myView);
			myView = null;
			webviewLayout.addView(webView);
			myCallBack.onCustomViewHidden();
		}

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			// TODO Auto-generated method stub
			Log.d("ZR",
					consoleMessage.message() + " at "
							+ consoleMessage.sourceId() + ":"
							+ consoleMessage.lineNumber());
			return super.onConsoleMessage(consoleMessage);
		}
	}

}
