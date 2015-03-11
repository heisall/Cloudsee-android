package com.jovision.views;

import java.io.IOException;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.AddThirdDevActivity;
import com.jovision.activities.AddThirdDeviceMenuFragment.OnDeviceClassSelectedListener;
import com.jovision.commons.CommonInterface;
import com.jovision.utils.ConfigUtil;

public class CustomDialog extends Dialog implements CommonInterface {
	private ImageView img_guide;
	private TextView loadingTv;
	private TextView tipsTv;
	private int img_res_id;
	private int dev_mark_id;
	private String[] third_guide_desc = null;
	private MediaPlayer myPlayer = null;
	private Context context;
	private boolean native_ui =  false;
	private WebView mWebView;
	private LinearLayout loadinglayout;
	private ImageView loadingBar;
	private boolean loadFailed = false;
	private OnDeviceClassSelectedListener mListener; 
	private String mUrl;
	public CustomDialog(Context context) {
		super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
		this.context = context;
		third_guide_desc = context.getResources().getStringArray(
				R.array.array_third_guide);
		// TODO Auto-generated constructor stub
	}

	//专用
	public CustomDialog(Context context, OnDeviceClassSelectedListener mainListener) {
		super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
		this.context = context;
		mListener = mainListener;
		third_guide_desc = context.getResources().getStringArray(
				R.array.array_third_guide);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(native_ui){
			this.setContentView(R.layout.guide_custom_dialog_layout);
		}
		else{
			this.setContentView(R.layout.webview_dialog_layout);		
		}
		setCanceledOnTouchOutside(false);
		setCancelable(false);
	}
    private WebChromeClient m_chromeClient = new WebChromeClient() {
        // 扩充缓存的容量
        @Override
        public void onReachedMaxAppCacheSize(long spaceNeeded,
                long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
            quotaUpdater.updateQuota(spaceNeeded * 2);
        }
    };
	@SuppressLint("NewApi")
	@Override
	public void onStart() {
		if(native_ui){
			img_guide = (ImageView) findViewById(R.id.guide_img);
			loadingTv = (TextView) findViewById(R.id.loadingText);
			tipsTv = (TextView) findViewById(R.id.guide_text_tips);
			img_guide.setImageResource(img_res_id);
			loadingTv.setText(third_guide_desc[dev_mark_id]);
			tipsTv.setText(third_guide_desc[dev_mark_id]);
			myPlayer = new MediaPlayer();			
		}
		else{
			mWebView = (WebView) findViewById(R.id.webview);

			loadingBar = (ImageView) findViewById(R.id.loadingbar);
			loadinglayout = (LinearLayout)findViewById(R.id.loadinglayout);
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			webSettings.setDomStorageEnabled(true);	
			webSettings.setMediaPlaybackRequiresUserGesture(false);
			mWebView.setWebChromeClient(m_chromeClient);
			
			mWebView.setWebViewClient(new WebViewClient() {
				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					Log.e("webv", "webView load failed");
					loadFailed = true;
					super.onReceivedError(view, errorCode, description, failingUrl);
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
					view.loadUrl(newUrl);					
					return true;
				}

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					loadinglayout.setVisibility(View.VISIBLE);
					Animation anim = AnimationUtils.loadAnimation(context,
							R.anim.rotate);
					loadingBar.setAnimation(anim);
					Log.v("Test", "webView start load");
					// mHandler.sendEmptyMessage(1);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					loadinglayout.setVisibility(View.GONE);
					Log.e("webv", "webView finish load");
					if (loadFailed) {
						Log.e("webv", "url:" + url + " load failed");
						dismiss();
					} 
					else {
						if (url.contains("device=")) {

							String param_array[] = url.split("\\?");
							HashMap<String, String> resMap;
							resMap = ConfigUtil.genMsgMapFromhpget(param_array[1]);

							String mDevType = resMap.get("device");
							if (mDevType != null && !mDevType.equals("")) {
								mListener.OnDeviceClassSelected(Integer
										.parseInt(mDevType), "");
							}
						}
					}

				}
			});	
			Log.e("webv","dialog load url:"+mUrl);
			mWebView.loadUrl(mUrl);
		}
	}

	@Override
	public void onStop() {
		if (null != myPlayer) {
			myPlayer.stop();
			myPlayer.release();
		}
	}

	public void Show(int img_id, int dev_mark) {
		img_res_id = img_id;
		dev_mark_id = dev_mark;
		this.show();
		playSound(dev_mark_id);
	}

	public void Show(int img_id, int dev_mark, String url) {
		if(native_ui){
			img_res_id = img_id;
			dev_mark_id = dev_mark;
			this.show();
			playSound(dev_mark_id);			
		}
		else{
			mUrl = url;
			this.show();
		}
	}

	@Override
	public void playSound(int soundType) {
		// TODO Auto-generated method stub
		try {
			// 打开指定音乐文件
			String file = "";
			if (ConfigUtil.getLanguage2(context) == Consts.LANGUAGE_ZH
					|| ConfigUtil.getLanguage2(context) == Consts.LANGUAGE_ZHTW) {
				switch (dev_mark_id) {
				case 1:// 门磁
					file = "menci.mp3";
					break;
				case 2:// 手环
					file = "shouhuan.mp3";
					break;
				case 3:// 遥控器
					file = "telecontrol.mp3";
					break;
				case 4:
				case 5:
				case 7:
					file = "alarm_guide_zh4.mp3";
					break;
				case 6:
					file = "alarm_guide_zh5.mp3";
					break;
				default:
					// file = "telecontrol.mp3";// 其他先用这个吧
					// break;
					return;
				}
				// } else if (ConfigUtil.getLanguage2(context) ==
				// Consts.LANGUAGE_ZHTW) {
				// switch (dev_mark_id) {
				// case 1:// 门磁
				// file = "menci_tw.mp3";
				// break;
				// case 2:// 手环
				// file = "shouhuan_tw.mp3";
				// break;
				// case 3:// 遥控器
				// file = "telecontrol_tw.mp3";
				// break;
				// case 4:
				// case 5:
				// case 7:
				// file = "alarm_guide_zh4.mp3";
				// break;
				// case 6:
				// file = "alarm_guide_zh5.mp3";
				// break;
				// default:
				// // file = "telecontrol_tw.mp3";
				// // break;
				// return;
				// }
			} else {
				switch (dev_mark_id) {
				case 1:// 门磁
					file = "menci_en.mp3";
					break;
				case 2:// 手环
					file = "shouhuan_en.mp3";
					break;
				case 3:// 遥控器
					file = "telecontrol_en.mp3";
					break;
				case 4:
				case 5:
				case 7:
					file = "alarm_guide_en4.mp3";
					break;
				case 6:
					file = "alarm_guide_en5.mp3";
					break;
				default:
					// file = "telecontrol_en.mp3";
					// break;
					return;
				}
			}
			int maxVolume = 100; // 最大音量值
			int curVolume = 20; // 当前音量值

			AudioManager audioMgr = null; // Audio管理器，用了控制音量
			audioMgr = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			// 获取最大音乐音量
			maxVolume = audioMgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			// 初始化音量大概为最大音量的1/2
			// curVolume = maxVolume / 2;
			// 每次调整的音量大概为最大音量的1/6
			audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume,
					AudioManager.FLAG_PLAY_SOUND);
			AssetManager assetMgr = context.getAssets();
			// 资源管理器
			AssetFileDescriptor afd = assetMgr.openFd(file);

			myPlayer.reset();

			// 使用searchView.myPlayer加载指定的声音文件。
			myPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			// 准备声音
			myPlayer.prepare();
			// 播放
			myPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
