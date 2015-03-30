package com.jovision.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
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
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	// 照相机拍照得到的图片
	private File mCurrentPhotoFile;
	// 缓存图片URI
	Uri imageTempUri = Uri.fromFile(new File(PHOTO_DIR, "1426573739396.jpg"));
	private Bitmap cameraBitmap = null;

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case 9898: {
			if (null != obj) {
				showTextToast(obj.toString());
			} else {
				showTextToast("null");
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

		// url = "http://172.16.25.228:8080/dev/test/upload.html";
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
			//
			// // For Android 3.0-
			// @SuppressWarnings("unused")
			// public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			// mUploadMessage = uploadMsg;
			// openFileChooser(uploadMsg, "");
			// }
			//
			// // For Android 3.0+
			// public void openFileChooser(ValueCallback<Uri> uploadMsg,
			// String acceptType) {
			// mUploadMessage = uploadMsg;
			// selectImage();
			// }
			//
			// // For Android 4.1
			// @SuppressWarnings("unused")
			// public void openFileChooser(ValueCallback<Uri> uploadMsg,
			// String acceptType, String capture) {
			// mUploadMessage = uploadMsg;
			// openFileChooser(uploadMsg, "");
			// }

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
		// if (null != mUploadMessage) {
		// mUploadMessage.onReceiveValue(null);
		// mUploadMessage = null;
		// }
		try {
			JVWebViewActivity.this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// webview返回
	public void backWebview() {
		MyLog.v("webView.canGoBack()", "" + webView.canGoBack());

		// if (null != mUploadMessage) {
		// mUploadMessage.onReceiveValue(null);
		// mUploadMessage = null;
		// }

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

	// @Override
	// // 设置回退
	// // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
	// webView.goBack(); // goBack()表示返回WebView的上一页面
	// return true;
	// } else {
	// JVWebViewActivity.this.finish();
	// }
	// return false;
	// }

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
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// webView.onPause(); // 暂停网页中正在播放的视频
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		webView.onResume();
		// if (null != mUploadMessage) {
		// mUploadMessage.onReceiveValue(null);
		// mUploadMessage = null;
		// }
	}

	// protected final void selectImage() {
	// AlertDialog.Builder builder = new Builder(JVWebViewActivity.this);
	// // builder.setTitle("插入照片");
	// builder.setItems(
	// new String[] {
	// getResources().getString(R.string.capture_to_upload),
	// getResources().getString(R.string.select_to_upload) },
	// new DialogInterface.OnClickListener() {
	// @SuppressLint("SdCardPath")
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// Intent intent = null;
	// switch (which) {
	// case REQ_CAMERA:
	// intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	// // 必须确保文件夹路径存在，否则拍照后无法完成回调
	// File vFile = new File(Consts.BBSIMG_PATH
	// + (System.currentTimeMillis() + ".jpg"));
	// if (!vFile.exists()) {
	// File folderFile = new File(Consts.BBSIMG_PATH);
	// MobileUtil.createDirectory(folderFile);
	// } else {
	// if (vFile.exists()) {
	// vFile.delete();
	// }
	// }
	// imageUri = Uri.fromFile(vFile);
	// intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
	// JVWebViewActivity.this.startActivityForResult(
	// intent, REQ_CAMERA);
	// break;
	// case REQ_CHOOSER:
	// intent = new Intent(Intent.ACTION_PICK, null);
	// intent.setDataAndType(
	// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	// "image/*");
	// JVWebViewActivity.this.startActivityForResult(
	// Intent.createChooser(
	// intent,
	// getResources().getString(
	// R.string.select_to_upload)),
	// REQ_CHOOSER);
	// break;
	// }
	// }
	// });
	// builder.setNegativeButton(R.string.cancel,
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// if (null != mUploadMessage) {
	// mUploadMessage.onReceiveValue(null);
	// mUploadMessage = null;
	// }
	// }
	// });
	//
	// AlertDialog dialog = builder.create();
	// dialog.setCancelable(false);
	// dialog.show();
	// }
	//
	// public static final int REQ_CAMERA = 0;
	// public static final int REQ_CHOOSER = 1;
	//
	// @Override
	// protected void onActivityResult(int requestCode, int resultCode,
	// Intent intent) {
	// switch (requestCode) {
	// case REQ_CHOOSER:
	// if (null == mUploadMessage)
	// return;
	// Uri result = intent == null || resultCode != RESULT_OK ? null
	// : intent.getData();
	//
	// String realPath = MobileUtil.getRealPath(JVWebViewActivity.this,
	// result);
	// // showTextToast(realPath);
	// if (null != realPath && !"".equalsIgnoreCase(realPath)) {
	// File file = new File(realPath);
	// mUploadMessage.onReceiveValue(Uri.fromFile(file));
	// } else {
	// mUploadMessage.onReceiveValue(null);
	// }
	//
	// mUploadMessage = null;
	// break;
	// case REQ_CAMERA:
	// if (resultCode == Activity.RESULT_OK) {
	// mUploadMessage.onReceiveValue(imageUri);
	// // showTextToast(imageUri.toString());
	// mUploadMessage = null;
	// }
	// break;
	// default:
	// webView.requestFocus();
	// mUploadMessage = null;
	// // mWebView.setFocusable(true);
	// break;
	// }
	// }

	public void startFunction(final String str) {
		// Toast.makeText(this, str + "页面js调用的java方法",
		// Toast.LENGTH_SHORT).show();
		// runOnUiThread(new Runnable() {
		//
		// @TargetApi(Build.VERSION_CODES.FROYO)
		// @Override
		// public void run() {
		// Map<String, Object> map = new HashMap<String, Object>();
		// byte[] m = image2byte(new File(PHOTO_DIR, "1426573739396.jpg")); //
		// 将图片转换成二进制数组
		// // base64编码转换成String
		// String pic = Base64.encodeToString(m, Base64.DEFAULT);
		// map.put("filename", "1426573739396.jpg");
		// map.put("filedata", pic);
		// map.put("filetype", 1);
		// map.put("fileextension", "jpg");
		// String s = UploadUtil.submitPostData(map, "GBK",
		// "http://bbst.cloudsee.net/misc.php?mod=swfupload&operation=upload&type=image&inajax=yes&infloat=yes&simple=2&uid=1");
		// // msgView.setText(msgView.getText() + s);
		// MyLog.v("startFunction", s);
		// }
		// });

		Thread uploadThread = new Thread() {
			@TargetApi(Build.VERSION_CODES.FROYO)
			@Override
			public void run() {
				Map<String, Object> map = new HashMap<String, Object>();
				byte[] m = image2byte(new File(PHOTO_DIR, "1426573739396.jpg")); // 将图片转换成二进制数组
				// base64编码转换成String
				String pic = Base64.encodeToString(m, Base64.DEFAULT);
				map.put("filename", "1426573739396.jpg");
				map.put("filedata", pic);
				map.put("filetype", 1);
				map.put("fileextension", "jpg");
				String s = UploadUtil
						.submitPostData(
								map,
								"GBK",
								"http://172.16.25.228:8080/misc.php?mod=swfupload&operation=upload&type=image&inajax=yes&infloat=yes&simple=2&uid=1&XDEBUG_SESSION_START=PHPSTORM");
				// msgView.setText(msgView.getText() + s);
				MyLog.v("startFunction", s);
			}
		};

		uploadThread.start();
	}

	public void cutpic() {
		new AlertDialog.Builder(JVWebViewActivity.this)
				.setTitle(getResources().getString(R.string.str_delete_tip))
				.setItems(
						new String[] {
								getResources().getString(
										R.string.capture_to_upload),
								getResources().getString(
										R.string.select_to_upload),
								getResources().getString(R.string.cancel) },
						new OnMyOnClickListener()).show();

	}

	/** 图片来源菜单响应类 */
	protected class OnMyOnClickListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			/** 从摄像头获取 */
			if (which == 0) {
				try {
					// 从摄像头拍照取头像
					PHOTO_DIR.mkdirs(); // 创建照片的存储目录
					mCurrentPhotoFile = new File(PHOTO_DIR,
							"temp_camera_headimg.jpg");
					Intent it_camera = new Intent(
							MediaStore.ACTION_IMAGE_CAPTURE);
					it_camera.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(mCurrentPhotoFile));
					startActivityForResult(it_camera,
							REQUEST_CODE_IMAGE_CAPTURE);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			} else if (which == 1) {
				/** 从相册获取 */
				try {

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
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (which == 2) {
				/** 取消 */
				dialog.dismiss();
			}
		}

	}

	/** 获取调用摄像头以及相册返回数据 */
	@SuppressLint("NewApi")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			// ==========摄像头===========
			if (requestCode == REQUEST_CODE_IMAGE_CAPTURE
					&& resultCode == Activity.RESULT_OK) {
				if (mCurrentPhotoFile != null) {
					// 方法1：读取缓存图片
					// startPhotoZoom(Uri.fromFile(mCurrentPhotoFile));

					Thread uploadThread = new Thread() {

						@Override
						public void run() {
							startFunction("");
							String res = UploadUtil.post();

							handler.sendMessage(handler.obtainMessage(9898, 0,
									0, res));
							super.run();
						}
					};
					uploadThread.start();
				}
				// ==========相册============
			} else if (requestCode == REQUEST_CODE_IMAGE_SELECTE
					&& resultCode == Activity.RESULT_OK) {
				cameraBitmap = this.decodeUriAsBitmap(imageTempUri);// 反编码
				if (cameraBitmap != null) {
					// //////chuantupian
					// }

					Thread uploadThread = new Thread() {

						@Override
						public void run() {
							startFunction("");
							String res = UploadUtil.post();
							handler.sendMessage(handler.obtainMessage(9898, 0,
									0, res));
							super.run();
						}
					};
					uploadThread.start();
				}
			} else {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 缩放拍摄图片 */
	public void startPhotoZoom1(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageTempUri);
		// intent.putExtra("return-data", true);
		startActivityForResult(intent, 1);
	}

	/** 通过URI获取BITMAP图 */
	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	/** 将图片转换成二进制数组 */
	public byte[] image2byte(File file) {
		byte[] data = null;
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int numBytesRead = 0;
			while ((numBytesRead = input.read(buf)) != -1) {
				output.write(buf, 0, numBytesRead);
			}
			data = output.toByteArray();
			output.close();
			input.close();
		} catch (FileNotFoundException ex1) {
			ex1.printStackTrace();
		} catch (IOException ex1) {
			ex1.printStackTrace();
		}
		return data;
	}
}
