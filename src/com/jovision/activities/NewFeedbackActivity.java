package com.jovision.activities;

import android.graphics.Bitmap;
import android.test.JVACCOUNT;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.MyLog;

public class NewFeedbackActivity extends BaseActivity {

	WebView myWebView;
	public static boolean localFlag = false;// 本地登陆标志位

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub
		setContentView(R.layout.feedback_webview_layout);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout)findViewById(R.id.alarmnet);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		rightBtn= (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.INVISIBLE);
		myWebView = (WebView) findViewById(R.id.feedback_webview);

		currentMenu.setText(getResources().getString(R.string.str_idea_and_feedback));
		leftBtn.setOnClickListener(myOnClickListener);

		localFlag = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));

		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.setWebChromeClient(new WebChromeClient());
		myWebView.requestFocus(View.FOCUS_DOWN);
		// 加快加载速度
		myWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		myWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains("forum.php?mod=viewthread")) {
					showTextToast(R.string.str_feedback_ok);
					NewFeedbackActivity.this.finish();
				} else {
					view.loadUrl(url);
				}
				return false;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				if (url.contains("forum.php?mod=viewthread")) {
					showTextToast(R.string.str_feedback_ok);
					NewFeedbackActivity.this.finish();
				}
				super.onPageStarted(view, url, favicon);
			}
		});
		if (localFlag) {
			myWebView
					.loadUrl("http://182.92.242.230/member.php?mod=mobile&session=0&username=jv_guest");
		} else {
			byte[] session = new byte[32];
			JVACCOUNT.GetSession(session);
			if (session.length != 32) {
				String strSession = new String(session);
				MyLog.e("FeedBack", "获取session失败:" + strSession);
				showTextToast("session error");
				finish();
			} else {
				String strSession = new String(session);
				String userName = statusHashMap.get(Consts.KEY_USERNAME);
				StringBuffer sbParamBuffer = new StringBuffer(
						"http://182.92.242.230/member.php?mod=mobile&session=");
				sbParamBuffer.append(strSession).append("&username=")
						.append(userName);
				String strParam = sbParamBuffer.toString();
				MyLog.e("FeedBack", "url:" + strParam);
				myWebView.loadUrl(strParam);
			}
		}
	}

	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		this.finish();
	}
}
