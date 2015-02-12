package com.jovision.activities;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.Url;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.JSONUtil;
import com.jovision.views.AlarmDialog;

public class JVVideoFragment extends BaseFragment {

	private static final String TAG = "JVWebViewActivity";
	private LinearLayout loadinglayout;
	/** topBar **/
	private RelativeLayout topBar;

	public static WebView webView;
	private String urls = "";
	private int titleID = 0;
	private ImageView loadingBar;
	private View rootView;
	private boolean isshow = false;

	private RelativeLayout loadFailedLayout;
	private ImageView reloadImgView;
	private boolean loadFailed = false;
	private boolean isConnected = false;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.WHAT_DEMO_URL_SUCCESS: {
			mActivity.dismissDialog();
			HashMap<String, String> paramMap = (HashMap<String, String>) obj;
			Intent intentAD = new Intent(mActivity, JVWebView2Activity.class);
			intentAD.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intentAD.putExtra("URL", paramMap.get("new_url"));
			intentAD.putExtra("title", -2);
			intentAD.putExtra("rtmp", paramMap.get("rtmpurl"));
			intentAD.putExtra("cloudnum", paramMap.get("cloud_num"));
			intentAD.putExtra("channel", paramMap.get("channel"));

			mActivity.startActivity(intentAD);
			break;
		}
		case Consts.WHAT_DEMO_URL_FAILED: {
			// TODO
			mActivity.dismissDialog();
			mActivity.showTextToast(R.string.str_video_load_failed);
			break;
		}
		case Consts.WHAT_PUSH_MESSAGE:
			// 弹出对话框
			if (null != mActivity) {
				mActivity.onNotify(Consts.NEW_PUSH_MSG_TAG_PRIVATE, 0, 0, null);//
				new AlarmDialog(mActivity).Show(obj);
				// onResume();
			} else {
				MyLog.e("Alarm",
						"onHandler mActivity is null ,so dont show the alarm dialog");
			}
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.demovideo_layout, container,
					false);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (null != ((BaseActivity) mActivity).statusHashMap.get("DEMOURL")) {
			
			String sid = "";
			String lan = "";
			if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(mActivity)) {
				lan = "zh_cn";
			} else if (Consts.LANGUAGE_ZHTW == ConfigUtil
					.getLanguage2(mActivity)) {
				lan = "zh_tw";
			} else {
				lan = "en_us";
			}
			urls = ((BaseActivity) mActivity).statusHashMap.get("DEMOURL");
//			urls = "http://test.cloudsee.net/phone.action";
			
			
			if (!Boolean.valueOf(mActivity.statusHashMap
					.get(Consts.LOCAL_LOGIN))) {
				String sessionResult = ConfigUtil.getSession();
				sid = sessionResult;
			} else {
				sid = "";
			}
			urls = urls + "?" + "plat=android&platv=" + Build.VERSION.SDK_INT
					+ "&lang=" + lan + "&d=" + System.currentTimeMillis()
					+ "&sid=" + sid;
		} else {
			isshow = true;
		}
		// url = "http://app.ys7.com/";
		if (urls.contains("rotate=x")) {
			urls = urls.replace("rotate=x", "");
			mActivity
			.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
		}
		MyLog.e("yanshidian", urls);
		/** topBar **/
		topBar = (RelativeLayout) rootView.findViewById(R.id.topbarh);
		leftBtn = (Button) rootView.findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) rootView.findViewById(R.id.alarmnet);
		accountError = (TextView) rootView.findViewById(R.id.accounterror);
		currentMenu = (TextView) rootView.findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.demo);
		loadingBar = (ImageView) rootView.findViewById(R.id.loadingbars);
		loadinglayout = (LinearLayout) rootView
				.findViewById(R.id.loadinglayout);

		loadFailedLayout = (RelativeLayout) rootView
				.findViewById(R.id.loadfailedlayout);
		loadFailedLayout.setVisibility(View.GONE);
		reloadImgView = (ImageView) rootView.findViewById(R.id.refreshimg);
		reloadImgView.setOnClickListener(myOnClickListener);
		currentMenu.setText(R.string.demo);
		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn = (Button) rootView.findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		webView = (WebView) rootView.findViewById(R.id.findpasswebview);

		// WebChromeClient wvcc = new WebChromeClient() {
		// @Override
		// public void onReceivedTitle(WebView view, String title) {
		// super.onReceivedTitle(view, title);
		// if (-2 == titleID) {
		//
		// }
		// }
		// };
		webView.getSettings().setJavaScriptEnabled(true);

		// 设置setWebChromeClient对象
		// webView.setWebChromeClient(wvcc);
		webView.requestFocus(View.FOCUS_DOWN);

		// setting.setPluginState(PluginState.ON);
		// 加快加载速度
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				MyLog.v(TAG, "webView load failed");
				loadFailed = true;
				loadFailedLayout.setVisibility(View.VISIBLE);
				webView.setVisibility(View.GONE);
				loadinglayout.setVisibility(View.GONE);
				mActivity.statusHashMap.put(Consts.HAS_LOAD_DEMO, "false");
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
				MyLog.v("new_url", newUrl);
				// showTextToast(rtmp);//////////////等着去掉
				try {
					if (newUrl.contains("viewmode")) {

						String param_array[] = newUrl.split("\\?");
						HashMap<String, String> resMap;
						resMap = ConfigUtil.genMsgMapFromhpget(param_array[1]);

						String rtmp_url = resMap.get("streamsvr");
						String rtmp_port = resMap.get("rtmport");
						String hls_port = resMap.get("hlsport");
						String cloud_num = resMap.get("cloudnum");
						String channel = resMap.get("channel");

						HashMap<String, String> paramMap = new HashMap<String, String>();
						paramMap.put("new_url", newUrl);
						paramMap.put("rtmp_url", rtmp_url);
						paramMap.put("rtmp_port", rtmp_port);
						paramMap.put("hls_port", hls_port);
						paramMap.put("cloud_num", cloud_num);
						paramMap.put("channel", channel);

						String getPlayUtlRequest = Url.JOVISION_PUBLIC_API
								+ "server=" + rtmp_url + "&dguid=" + cloud_num
								+ "&channel=" + channel + "&hlsport="
								+ hls_port + "&rtmpport=" + rtmp_port;

						mActivity.createDialog("", false);
						new GetPlayUrlThread(paramMap, getPlayUtlRequest)
						.start();
					} else {
						view.loadUrl(newUrl);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				if (!MySharedPreference.getBoolean("webfirst")) {
					MySharedPreference.putBoolean("webfirst", true);
					loadinglayout.setVisibility(View.VISIBLE);
					loadingBar.setAnimation(AnimationUtils.loadAnimation(mActivity,
							R.anim.rotate));
				}
				MyLog.v(TAG, "webView start load");
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				// webView.loadUrl("javascript:videopayer.play()");

				if (loadFailed) {
					mActivity.statusHashMap.put(Consts.HAS_LOAD_DEMO, "false");
					loadFailedLayout.setVisibility(View.VISIBLE);
					webView.setVisibility(View.GONE);
					loadinglayout.setVisibility(View.GONE);
				} else {
					if (isConnected) {
						mActivity.statusHashMap.put(Consts.HAS_LOAD_DEMO,
								"true");
						webView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
						loadinglayout.setVisibility(View.GONE);
						webView.setVisibility(View.VISIBLE);
						loadFailedLayout.setVisibility(View.GONE);
					} else {
						mActivity.statusHashMap.put(Consts.HAS_LOAD_DEMO,
								"false");
						loadFailedLayout.setVisibility(View.VISIBLE);
						webView.setVisibility(View.GONE);
						loadinglayout.setVisibility(View.GONE);
					}
				}
				if (isshow) {
					mActivity.statusHashMap.put(Consts.HAS_LOAD_DEMO, "false");
					loadFailedLayout.setVisibility(View.VISIBLE);
					webView.setVisibility(View.GONE);
					loadinglayout.setVisibility(View.GONE);
				}
				MyLog.v(TAG, "webView finish load");
			}
		});

		boolean hasLoad = false;
		if (null != mActivity.statusHashMap.get(Consts.HAS_LOAD_DEMO)) {
			hasLoad = Boolean.parseBoolean(mActivity.statusHashMap
					.get(Consts.HAS_LOAD_DEMO));
		}
		if (hasLoad && ConfigUtil.isConnected(mActivity)) {
			webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
		} else {
			loadinglayout.setVisibility(View.VISIBLE);
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			loadFailed = false;
			webView.loadUrl(urls);
		}
	}

	class GetPlayUrlThread extends Thread {
		String requestUrl;
		HashMap<String, String> paramMap;

		public GetPlayUrlThread(HashMap<String, String> map, String url) {
			requestUrl = url;
			paramMap = map;
		}

		@Override
		public void run() {
			super.run();
			MyLog.v("post_request", requestUrl);
			int rt = 0;
			String rtmpUrl = "";
			// {"rt": 0, "rtmpurl":
			// "rtmp://119.188.172.3:1935/live/a579223323_1", "hlsurl":
			// "http://119.188.172.3:8080/live/a579223323_1.m3u8", "mid": 33512}
			String result = JSONUtil.getRequest(requestUrl);
			try {
				if (null != result && !"".equalsIgnoreCase(result)) {
					JSONObject obj = new JSONObject(result);
					if (null != obj) {
						rt = obj.getInt("rt");
						rtmpUrl = obj.getString("rtmpurl");
					}
				}

				if (0 == rt) {
					paramMap.put("rtmpurl", rtmpUrl);
					fragHandler.sendMessage(fragHandler.obtainMessage(
							Consts.WHAT_DEMO_URL_SUCCESS, 0, 0, paramMap));
				} else {
					fragHandler.sendMessage(fragHandler.obtainMessage(
							Consts.WHAT_DEMO_URL_FAILED, 0, 0, null));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			MyLog.v("post_result", result);
		}
	}

	OnClickListener myOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left: {
				backMethod();
				break;
			}
			case R.id.refreshimg: {
				if (ConfigUtil.isConnected(mActivity)) {
					loadFailedLayout.setVisibility(View.GONE);
					mActivity.statusHashMap.put(Consts.HAS_LOAD_DEMO, "false");
					loadinglayout.setVisibility(View.VISIBLE);
					loadingBar.setAnimation(AnimationUtils.loadAnimation(
							mActivity, R.anim.rotate));
					loadFailed = false;
					if ("".equalsIgnoreCase(urls)) {
						MyLog.v(TAG, "urls is null");
					}
					webView.loadUrl(urls);
				} else {
					mActivity.alertNetDialog();
				}
				break;
			}
			}

		}
	};

	/**
	 * 返回事件
	 */
	private void backMethod() {
		if (webView.canGoBack()) {
			webView.goBack(); // goBack()表示返回WebView的上一页面
		} else {
			mActivity.openExitDialog();
		}
	}

	// @Override
	// // 设置回退
	// // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	// webView.goBack(); // goBack()表示返回WebView的上一页面
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	@Override
	public void onPause() {
		super.onPause();
		webView.onPause();
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// webView.onPause(); // 暂停网页中正在播放的视频
		// }
	}

	@Override
	public void onResume() {
		super.onResume();
		webView.onResume();
		if (!ConfigUtil.isConnected(mActivity)) {
			isConnected = false;
			loadFailedLayout.setVisibility(View.VISIBLE);
			mActivity.statusHashMap.put(Consts.HAS_LOAD_DEMO, "false");
			webView.setVisibility(View.GONE);
			loadinglayout.setVisibility(View.GONE);
		} else {
			isConnected = true;
		}
		// fragHandler.sendMessage(fragHandler
		// .obtainMessage(2000, 0, 0, null));
	}

}
