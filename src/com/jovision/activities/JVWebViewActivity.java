package com.jovision.activities;

import java.io.File;
import java.util.HashMap;
import java.util.Stack;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
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
import com.jovision.commons.Url;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.JSONUtil;
import com.jovision.utils.MobileUtil;
import com.jovision.utils.UploadUtil;

public class JVWebViewActivity extends BaseActivity {

	private static final String TAG = "JVWebViewActivity";
	private LinearLayout loadinglayout;
	/** topBar **/
	private RelativeLayout topBar;

	private WebView webView;
	private String url = "";
	private int titleID = 0;
	private ImageView loadingBar;
	WebChromeClient wvcc = null;
	String sid = "";
	String lan = "";

	private LinearLayout loadFailedLayout;
	private ImageView reloadImgView;
	private boolean loadFailed = false;
	private boolean isfirst = false;

	Stack<String> titleStack = new Stack<String>();// 标题栈，后进先出

	// protected ValueCallback<Uri> mUploadMessage;
	// protected int FILECHOOSER_RESULTCODE = 1;
	// private Uri imageUri;

	protected static final int REQUEST_CODE_IMAGE_CAPTURE = 0;
	protected static final int REQUEST_CODE_IMAGE_SELECTE = 1;
	protected static final int REQUEST_CODE_IMAGE_CROP = 2;
	// /* 拍照的照片存储位置 */
	// private static final File PHOTO_DIR = new File(
	// Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	// 照相机拍照得到的图片
	private File mCurrentPhotoFile;
	// 缓存图片URI
	Uri imageTempUri = null;
	private String uploadUrl = "";// "http://bbs.cloudsee.net/misc.php?mod=swfupload&operation=upload&type=image&inajax=yes&infloat=yes&simple=2&uid=1";

	private Dialog initDialog;
	private RelativeLayout capture_Load;
	private RelativeLayout select_Load;
	private ImageView dialog_cancle_img;
	private TextView capturetext;
	private TextView selecttext;


	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.BBS_IMG_UPLOAD_SUCCESS: {
			dismissDialog();
			if (null != obj) {
				// showTextToast(obj.toString());
				webView.loadUrl("javascript:uppic(\"" + obj.toString() + "\")");
			} else {
				// showTextToast("null");
			}

			break;
		}
		case Consts.WHAT_DEMO_URL_SUCCESS: {
			dismissDialog();
			HashMap<String, String> paramMap = (HashMap<String, String>) obj;
			Intent intentAD = new Intent(JVWebViewActivity.this,
					JVWebView2Activity.class);
			intentAD.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intentAD.putExtra("URL", paramMap.get("new_url"));
			intentAD.putExtra("title", -2);
			intentAD.putExtra("rtmp", paramMap.get("rtmpurl"));
			intentAD.putExtra("cloudnum", paramMap.get("cloud_num"));
			intentAD.putExtra("channel", paramMap.get("channel"));

			JVWebViewActivity.this.startActivity(intentAD);
			break;
		}
		case Consts.WHAT_DEMO_URL_FAILED: {
			// TODO
			dismissDialog();
			showTextToast(R.string.str_video_load_failed);
			break;
		}
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		url = getIntent().getStringExtra("URL");
		// url =
		// "http://bbst.cloudsee.net/forum.php?mod=forumdisplay&fid=36&mobile=2";
		// url = "http://test.cloudsee.net/phone.action";
		// url = "http://app.ys7.com/";
		titleID = getIntent().getIntExtra("title", 0);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void initUi() {
		setContentView(R.layout.findpass_layout);

		// if (url.contains("rotate=x")) {
		// url = url.replace("rotate=x", "");
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//
		// 横屏
		// }

		if (Consts.LANGUAGE_ZH == ConfigUtil.getLanguage2(this)) {
			lan = "zh_cn";
		} else if (Consts.LANGUAGE_ZHTW == ConfigUtil.getLanguage2(this)) {
			lan = "zh_tw";
		} else {
			lan = "en_us";
		}

		if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
			String sessionResult = ConfigUtil.getSession();
			sid = sessionResult;
		} else {
			sid = "";
		}

		MyLog.v(TAG, "webview-URL=" + url);
		/** topBar **/
		topBar = (RelativeLayout) findViewById(R.id.topbarh);
		leftBtn = (Button) findViewById(R.id.btn_left);
		alarmnet = (RelativeLayout) findViewById(R.id.alarmnet);
		accountError = (TextView) findViewById(R.id.accounterror);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(R.string.loading);
		loadingBar = (ImageView) findViewById(R.id.loadingbars);
		loadinglayout = (LinearLayout) findViewById(R.id.loadinglayout);

		loadFailedLayout = (LinearLayout) findViewById(R.id.loadfailedlayout);
		loadFailedLayout.setVisibility(View.GONE);
		reloadImgView = (ImageView) findViewById(R.id.refreshimg);
		reloadImgView.setOnClickListener(myOnClickListener);

		if (-1 == titleID) {
			currentMenu.setText("");
		} else if (-2 == titleID) {

		} else {
			currentMenu.setText(titleID);
		}

		leftBtn.setOnClickListener(myOnClickListener);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		rightBtn.setOnClickListener(myOnClickListener);

		webView = (WebView) findViewById(R.id.findpasswebview);

		// url = "http://172.16.25.228:8080/";
		wvcc = new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				if (-2 == titleID) {
					currentMenu.setText(title);
					titleStack.push(title);
				}
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}

		};
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(this, "wst");

		// 设置setWebChromeClient对象
		webView.setWebChromeClient(wvcc);
		webView.requestFocus(View.FOCUS_DOWN);

		// setting.setPluginState(PluginState.ON);
		// 加快加载速度
		webView.getSettings().setRenderPriority(RenderPriority.HIGH);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				MyLog.v(TAG, "webView load failed");
				loadFailed = true;
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String newUrl) {
				MyLog.v("new_url", newUrl);
				// showTextToast(rtmp);//////////////等着去掉
				try {

					if (newUrl.contains("open")) {// 打开新的WebView模式
						Intent intentAD2 = new Intent(JVWebViewActivity.this,
								JVWebViewActivity.class);
						intentAD2.putExtra("URL", newUrl);
						intentAD2.putExtra("title", -2);
						JVWebViewActivity.this.startActivity(intentAD2);
					} else if (newUrl.contains("close")) {// 关闭当前webview
						JVWebViewActivity.this.finish();
					} else if (newUrl.contains("video")
							|| newUrl.contains("viewmode")) {// 是否含有视频

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

						createDialog("", false);
						new GetPlayUrlThread(paramMap, getPlayUtlRequest)
						.start();
					} else {
						// String plazzaUrl = statusHashMap
						// .get(Consts.MORE_DEMOURL);
						// if (newUrl.contains(plazzaUrl)) {
						// newUrl = newUrl + "?" + "plat=android&platv="
						// + Build.VERSION.SDK_INT + "&lang=" + lan
						// + "&d=" + System.currentTimeMillis()
						// + "&sid=" + sid;
						// }

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
				if (!isfirst) {
					loadinglayout.setVisibility(View.VISIBLE);
					loadingBar.setAnimation(AnimationUtils.loadAnimation(
							JVWebViewActivity.this, R.anim.rotate));
					isfirst = true;
				}
				MyLog.v(TAG, "webView start load");
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				// webView.loadUrl("javascript:videopayer.play()");
				webView.loadUrl("javascript:upload_url()");// 调用js 获取图片上传地址
				if (loadFailed) {
					loadFailedLayout.setVisibility(View.VISIBLE);
					webView.setVisibility(View.GONE);
					loadinglayout.setVisibility(View.GONE);
				} else {
					webView.loadUrl("javascript:function uploadPicFromMobile(str){fileupload.addFileList(str);}");
					// webView.loadUrl("javascript:(function() { var videos = document.getElementsByTagName('video'); for(var i=0;i<videos.length;i++){videos[i].play();}})()");
					loadinglayout.setVisibility(View.GONE);
					webView.setVisibility(View.VISIBLE);
					loadFailedLayout.setVisibility(View.GONE);
				}
				MyLog.v(TAG, "webView finish load");
			}
		});

		loadFailed = false;
		webView.loadUrl(url);
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
					handler.sendMessage(handler.obtainMessage(
							Consts.WHAT_DEMO_URL_SUCCESS, 0, 0, paramMap));
				} else {
					handler.sendMessage(handler.obtainMessage(
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
			case R.id.capture_upload:
				/** 从摄像头获取 */
				try {
					MobileUtil.createDirectory(new File(Consts.BBSIMG_PATH));
					imageTempUri = Uri
							.fromFile(new File(Consts.BBSIMG_PATH, System
									.currentTimeMillis()
									+ Consts.IMAGE_JPG_KIND));

					mCurrentPhotoFile = new File(Consts.BBSIMG_PATH,
							System.currentTimeMillis() + Consts.IMAGE_JPG_KIND);
					Intent it_camera = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					it_camera.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(mCurrentPhotoFile));
					startActivityForResult(it_camera,
							REQUEST_CODE_IMAGE_CAPTURE);
					initDialog.dismiss();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case R.id.select_upload:
				/** 从相册获取 */
				try {

					MobileUtil.createDirectory(new File(Consts.BBSIMG_PATH));
					imageTempUri = Uri
							.fromFile(new File(Consts.BBSIMG_PATH, System
									.currentTimeMillis()
									+ Consts.IMAGE_JPG_KIND));
					// 从相册取相片
					Intent it_photo = new Intent(Intent.ACTION_GET_CONTENT);
					it_photo.addCategory(Intent.CATEGORY_OPENABLE);
					// 设置数据类型
					it_photo.setType("image/*");
					// 设置返回方式
					// intent.putExtra("return-data", true);
					it_photo.putExtra(MediaStore.EXTRA_OUTPUT, imageTempUri);
					// 设置截图
					// it_photo.putExtra("crop", "true");
					// it_photo.putExtra("scale", true);
					// 跳转至系统功能
					startActivityForResult(it_photo, REQUEST_CODE_IMAGE_SELECTE);
					initDialog.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}


				break;
			case R.id.dialog_cancle_img:
				initDialog.dismiss();
				break;
			case R.id.btn_left: {
				backMethod();
				break;
			}
			case R.id.btn_right: {// 关闭当前网页
				backWebview();
				break;
			}
			case R.id.refreshimg: {
				loadFailedLayout.setVisibility(View.GONE);
				loadinglayout.setVisibility(View.VISIBLE);
				loadingBar.setAnimation(AnimationUtils.loadAnimation(
						JVWebViewActivity.this, R.anim.rotate));
				loadFailed = false;
				webView.reload();
				break;
			}
			}

		}
	};

	/**
	 * 返回事件
	 */
	private void backMethod() {
		MyLog.v("webView.canGoBack()", "" + webView.canGoBack());
		try {
			JVWebViewActivity.this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// webview返回
	public void backWebview() {
		MyLog.v("webView.canGoBack()", "" + webView.canGoBack());

		try {
			if (webView.canGoBack()) {
				if (null != titleStack && 0 != titleStack.size()) {
					titleStack.pop();
					String lastTitle = titleStack.peek();
					currentMenu.setText(lastTitle);
					webView.goBack(); // goBack()表示返回WebView的上一页面
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	@Override
	protected void onPause() {
		super.onPause();
		webView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.onResume();
	}

	/**
	 * upload_url js的返回值
	 * 
	 * @param upUrl
	 *            即js的返回值
	 */
	public void getUploadUrl(String upUrl) {
		uploadUrl = upUrl;
	}

	/**
	 * js window.wst.cutpic()
	 */
	//	public void cutpic() {
	//		new AlertDialog.Builder(JVWebViewActivity.this)
	//				.setTitle(getResources().getString(R.string.str_delete_tip))
	//				.setItems(
	//						new String[] {
	//								getResources().getString(
	//										R.string.capture_to_upload),
	//								getResources().getString(
	//										R.string.select_to_upload),
	//								getResources().getString(R.string.cancel) },
	//						new OnMyOnClickListener()).show();
	//
	//	}

	/**
	 * 
	 * 2015-03-31 修改上传照片dialog
	 * 
	 * */
	public void cutpic() {
		initDialog = new Dialog(JVWebViewActivity.this, R.style.mydialog);
		View view = LayoutInflater.from(JVWebViewActivity.this).inflate(
				R.layout.dialog_capture, null);
		initDialog.setContentView(view);

		capture_Load = (RelativeLayout)view.findViewById(R.id.capture_upload);
		select_Load = (RelativeLayout)view.findViewById(R.id.select_upload);
		dialog_cancle_img = (ImageView) view.findViewById(R.id.dialog_cancle_img);
		capturetext = (TextView)view.findViewById(R.id.capturetext);
		selecttext = (TextView)view.findViewById(R.id.selecttext);

		capture_Load.setOnClickListener(myOnClickListener);
		select_Load.setOnClickListener(myOnClickListener);
		dialog_cancle_img.setOnClickListener(myOnClickListener);
		initDialog.show();
	}

	//	/** 图片来源菜单响应类 */
	//	protected class OnMyOnClickListener implements
	//			DialogInterface.OnClickListener {
	//
	//		@Override
	//		public void onClick(DialogInterface dialog, int which) {
	//			if (which == 0) {
	//				
	//			} else if (which == 1) {
	//			
	//			} else if (which == 2) {
	//				/** 取消 */
	//				dialog.dismiss();
	//			}
	//		}
	//
	//	}

	/** 获取调用摄像头以及相册返回数据 */
	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			// ==========摄像头===========
			if (requestCode == REQUEST_CODE_IMAGE_CAPTURE
					&& resultCode == Activity.RESULT_OK) {
				if (mCurrentPhotoFile != null) {
					createDialog("", false);
					Thread uploadThread = new Thread() {

						@Override
						public void run() {
							String res = UploadUtil.uploadFile(
									mCurrentPhotoFile, uploadUrl);
							handler.sendMessage(handler.obtainMessage(
									Consts.BBS_IMG_UPLOAD_SUCCESS, 0, 0, res));
							super.run();
						}
					};
					uploadThread.start();
				}
				// ==========相册============
			} else if (requestCode == REQUEST_CODE_IMAGE_SELECTE
					&& resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();
				mCurrentPhotoFile = getFileFromUri(uri);// 根据uri获取文件
				if (mCurrentPhotoFile != null) {
					createDialog("", false);
					Thread uploadThread = new Thread() {
						@Override
						public void run() {
							String res = UploadUtil.uploadFile(
									mCurrentPhotoFile, uploadUrl);
							handler.sendMessage(handler.obtainMessage(
									Consts.BBS_IMG_UPLOAD_SUCCESS, 0, 0, res));
							super.run();
						}
					};
					uploadThread.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据uri获取文件
	 * 
	 * @param uri
	 * @return
	 */
	public File getFileFromUri(Uri uri) {
		File file = null;

		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor
				.getString(actual_image_column_index);
		file = new File(img_path);
		return file;
	}
}
