package com.jovision.activities;

import java.lang.ref.WeakReference;

import android.R;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jovision.Consts;
import com.jovision.commons.BaseApp;
import com.jovision.commons.JVConfigManager;
import com.jovision.commons.JVConst;
import com.jovision.commons.JVUpdate;
import com.jovision.commons.UpdateThread;
import com.jovision.commons.Url;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.ConfigUtil;

public class JVMoreFeatureActivity extends BaseActivity {

	private int GPS_SWITCH = 90001;

	private UpdateThread updateThread;// 检查新版本线程
	private ProgressDialog dialog = null;

	private Button back;
	private Button add;
	private Button loginOut;
	private TextView currentMenu;

	private TextView screenImageText = null;
	private RelativeLayout sharelayout = null;
	private RelativeLayout alarmlayout1 = null;// 报警信息
	private TextView alarmInfo = null;
	private RelativeLayout alarmlayout2 = null;// 报警开关
	// private RelativeLayout watchtypelayout = null;
	private RelativeLayout positionlayout = null;
	private RelativeLayout accountlayout = null;
	private RelativeLayout scenepiclayout = null;
	private RelativeLayout helplayout = null;
	private RelativeLayout browseModeLayout = null;// 观看模式
	private RelativeLayout shakeLayout = null;// 摇一摇加设备
	private RelativeLayout checkupdatelayout = null;
	private RelativeLayout aboutlayout = null;
	private RelativeLayout feedbacklayout = null;

	private ToggleButton alarmBtn = null;
	private TextView alarmText = null;
	private ToggleButton positionBtn = null;
	private ToggleButton scenepicBtn = null;
	private ToggleButton helpBtn = null;
	private ToggleButton browseModeBtn = null;
	private ToggleButton watchTypeBtn = null;
	private ToggleButton shakeDeviceBtn = null;
	private TextView browseModeText = null;
	private TextView currentAccount = null;
	private TextView watchTypeText = null;

	// private Weibo mWeibo;
	// private static String CONSUMER_KEY = "";// 替换为开发者的appkey，例如"1646212860";
	// private static final String REDIRECT_URL = "http://www.sina.com";
	// public static Oauth2AccessToken accessToken;
	// public static final String TAG = "sinasdk";

	SharedPreferences sharedPreferences = null;
	Editor editor = null;// 获取编辑器
	boolean direDis = false;// true:直接注销账号不断开视频
	JVConfigManager dbManager = null;

	// MyRequestListener myListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.morefeatures_layout);
		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		// myListener = new MyRequestListener();

		sharedPreferences = BaseApp.getSP(getApplicationContext());
		editor = BaseApp.getEditor(getApplicationContext());
		;// 获取编辑器

		Intent intent = JVMoreFeatureActivity.this.getIntent();
		if (null != intent) {
			direDis = intent.getBooleanExtra("DirectDisConn", false);
		}

		dbManager = BaseApp.getDbManager(this);//

		// CONSUMER_KEY = getResources().getString(R.string.str_sina_key);
		initViews();

		dialog = new ProgressDialog(JVMoreFeatureActivity.this);
		dialog.setMessage(getResources().getString(R.string.str_checkupdating));

	}

	public void initViews() {
		BaseApp.moreHandler = new MoreHandler(JVMoreFeatureActivity.this);
		back = (Button) findViewById(R.id.back);
		add = (Button) findViewById(R.id.btn_right);
		loginOut = (Button) findViewById(R.id.loginout);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentAccount = (TextView) findViewById(R.id.currentaccount);
		currentAccount.setText(statusHashMap.get(Consts.KEY_USERNAME));
		currentMenu.setText(R.string.str_more);
		sharelayout = (RelativeLayout) findViewById(R.id.sharelayout);
		alarmlayout1 = (RelativeLayout) findViewById(R.id.alarmlayout1);
		alarmlayout2 = (RelativeLayout) findViewById(R.id.alarmlayout2);
		// watchtypelayout = (RelativeLayout)
		// findViewById(R.id.watchtypelayout);
		positionlayout = (RelativeLayout) findViewById(R.id.positionlayout);
		scenepiclayout = (RelativeLayout) findViewById(R.id.scenepiclayout);
		accountlayout = (RelativeLayout) findViewById(R.id.accountlayout);
		helplayout = (RelativeLayout) findViewById(R.id.helpadvicelayout);
		aboutlayout = (RelativeLayout) findViewById(R.id.aboutlayout);
		checkupdatelayout = (RelativeLayout) findViewById(R.id.checkupdatelayout);
		feedbacklayout = (RelativeLayout) findViewById(R.id.feedbacklayout);
		alarmBtn = (ToggleButton) findViewById(R.id.alarmbtn);
		watchTypeBtn = (ToggleButton) findViewById(R.id.watchtypebtn);
		positionBtn = (ToggleButton) findViewById(R.id.positionbtn);
		positionBtn.setChecked(isGPSEnable());
		positionBtn.setOnCheckedChangeListener(onCheckedChangeListener);
		scenepicBtn = (ToggleButton) findViewById(R.id.scenepicbtn);
		screenImageText = (TextView) findViewById(R.id.screenimagetext);
		alarmInfo = (TextView) findViewById(R.id.alarminfocount);
		alarmText = (TextView) findViewById(R.id.alarminfo);
		watchTypeText = (TextView) findViewById(R.id.watchtypeinfo);
		helpBtn = (ToggleButton) findViewById(R.id.helpbtn);

		shakeDeviceBtn = (ToggleButton) findViewById(R.id.shakedevicebtn);
		if (null != sharedPreferences) {
			shakeDeviceBtn.setChecked(sharedPreferences.getBoolean(
					"ShakeDevice", false));
		}
		shakeDeviceBtn.setOnCheckedChangeListener(onCheckedChangeListener);

		browseModeLayout = (RelativeLayout) findViewById(R.id.videomodelayout);// 观看模式
		shakeLayout = (RelativeLayout) findViewById(R.id.shakelayout);// 摇一摇加设备

		browseModeBtn = (ToggleButton) findViewById(R.id.videomodebtn);
		browseModeText = (TextView) findViewById(R.id.videomodeinfo);

		// 暂时屏蔽地理位置，场景图，报警
		alarmBtn.setChecked(false);
		scenepicBtn.setChecked(false);
		positionBtn.setChecked(false);

		if (Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {// 本地登录场景图选项不可设置
			scenepicBtn.setClickable(false);
			screenImageText.setTextColor(getResources().getColor(
					R.color.screenimagetextcolor));
			currentAccount.setTextColor(getResources().getColor(
					R.color.screenimagetextcolor));
			alarmBtn.setChecked(false);
			alarmBtn.setClickable(false);
			alarmText.setTextColor(getResources().getColor(
					R.color.screenimagetextcolor));
			watchTypeBtn.setChecked(false);
			watchTypeBtn.setClickable(false);
			watchTypeText.setTextColor(getResources().getColor(
					R.color.screenimagetextcolor));
		} else {
			// screenImageText.setTextColor(getResources().getColor(R.color.morefunctiontextcolor));
			scenepicBtn.setChecked(BaseApp.LOADIMAGE);
			scenepicBtn.setOnCheckedChangeListener(onCheckedChangeListener);

			if (null != sharedPreferences
					&& sharedPreferences.getBoolean("PushMessage", false)) {
				alarmBtn.setChecked(true);
			} else {
				alarmBtn.setChecked(false);
			}
			alarmBtn.setOnCheckedChangeListener(onCheckedChangeListener);
			if (null != sharedPreferences
					&& sharedPreferences.getBoolean("watchType", true)) {
				watchTypeBtn.setChecked(true);
			} else {
				watchTypeBtn.setChecked(false);
			}
			watchTypeBtn.setOnCheckedChangeListener(onCheckedChangeListener);
		}

		if (null != sharedPreferences) {
			// 只要有一个为true就是选中状态
			boolean flag1 = sharedPreferences.getBoolean("ShowMainHelp1", true);
			boolean flag2 = sharedPreferences.getBoolean("ShowAddHelp", true);
			boolean flag3 = sharedPreferences.getBoolean("ShowLeftHelp", true);
			boolean flag4 = sharedPreferences.getBoolean("ShowPlayHelp", true);
			boolean flag5 = sharedPreferences
					.getBoolean("ShowManageHelp", true);
			boolean flag6 = sharedPreferences.getBoolean("ShowMainHelp2", true);
			if (BaseApp.showQRHelp) {
				if (flag1 && flag2 && flag3 && flag4 && flag5 && flag6) {
					helpBtn.setChecked(true);
				} else {
					helpBtn.setChecked(false);
				}
			} else {
				if (flag1 && flag3 && flag4 && flag5 && flag6) {
					helpBtn.setChecked(true);
				} else {
					helpBtn.setChecked(false);
				}
			}

		}

		helpBtn.setOnCheckedChangeListener(onCheckedChangeListener);
		browseModeBtn.setOnCheckedChangeListener(onCheckedChangeListener);

		add.setOnClickListener(onClickListener);
		back.setOnClickListener(onClickListener);
		loginOut.setOnClickListener(onClickListener);
		sharelayout.setOnClickListener(onClickListener);

		if (null != sharedPreferences
				&& sharedPreferences.getBoolean("MoreVideoMode", false)) {
			browseModeBtn.setChecked(true);// 多设备模式
			browseModeText.setText(getResources().getString(
					R.string.str_video_mode)
					+ "("
					+ getResources().getString(R.string.str_video_more_mode)
					+ ")");
		} else {
			browseModeBtn.setChecked(false);// 单设备模式
			browseModeText.setText(getResources().getString(
					R.string.str_video_mode)
					+ "("
					+ getResources().getString(R.string.str_video_single_mode)
					+ ")");
		}
		browseModeBtn.setOnCheckedChangeListener(onCheckedChangeListener);
		if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
			accountlayout.setOnClickListener(onClickListener);
		} else {
			accountlayout.setClickable(false);
			accountlayout.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.more_feature_bg_1_gray));
		}
		// helplayout.setOnClickListener(onClickListener);
		aboutlayout.setOnClickListener(onClickListener);
		checkupdatelayout.setOnClickListener(onClickListener);
		feedbacklayout.setOnClickListener(onClickListener);
		alarmlayout1.setOnClickListener(onClickListener);
		positionlayout.setOnClickListener(onClickListener);
		scenepiclayout.setOnClickListener(onClickListener);
		helplayout.setOnClickListener(onClickListener);
		alarmlayout2.setOnClickListener(onClickListener);
		// watchtypelayout.setOnClickListener(onClickListener);
		browseModeLayout.setOnClickListener(onClickListener);
		shakeLayout.setOnClickListener(onClickListener);
		int count = 0;
		BaseApp.getPushList();
		count = BaseApp.pushList.size();
		alarmInfo.setText(getResources().getString(R.string.str_alarm_info)
				+ "(" + String.valueOf(count) + ")");

		sharelayout.setVisibility(View.GONE);
		// accountlayout
		// .setBackgroundResource(R.drawable.more_feature_bg_1_selector);

		// 公共版本的隐藏反馈
		if (BaseApp.PUBLIC_VERSION) {
			feedbacklayout.setVisibility(View.GONE);
		}
	}

	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			switch (buttonView.getId()) {

			case R.id.videomodebtn:
				editor.putBoolean("MoreVideoMode", isChecked);
				editor.commit();// 提交修改
				if (isChecked) {
					browseModeText.setText(getResources().getString(
							R.string.str_video_mode)
							+ "("
							+ getResources().getString(
									R.string.str_video_more_mode) + ")");
				} else {
					browseModeText.setText(getResources().getString(
							R.string.str_video_mode)
							+ "("
							+ getResources().getString(
									R.string.str_video_single_mode) + ")");
				}
				break;
			case R.id.scenepicbtn:
				editor.putBoolean("ScenePictures", isChecked);
				editor.commit();// 提交修改
				BaseApp.LOADIMAGE = isChecked;
				break;
			case R.id.helpbtn:
				editor.putBoolean("ShowMainHelp1", isChecked);
				editor.commit();// 提交修改
				editor.putBoolean("ShowAddHelp", isChecked);
				editor.commit();// 提交修改
				editor.putBoolean("ShowLeftHelp", isChecked);
				editor.commit();// 提交修改
				editor.putBoolean("ShowPlayHelp", isChecked);
				editor.commit();// 提交修改
				editor.putBoolean("ShowManageHelp", isChecked);
				editor.commit();// 提交修改
				editor.putBoolean("ShowMainHelp2", isChecked);
				editor.commit();// 提交修改
				break;
			case R.id.watchtypebtn:
				if (dialog == null) {
					dialog = new ProgressDialog(JVMoreFeatureActivity.this);
				}
				dialog.setMessage(getResources().getString(
						R.string.str_deleting));
				dialog.setCancelable(false);
				if (!dialog.isShowing()) {
					dialog.show();
				}
				if (null != sharedPreferences) {
					boolean flag = sharedPreferences.getBoolean("watchType",
							true);
					// 一样没做任何选择
					if (flag && isChecked) {

					} else {
						watchTypeBtn.setChecked(isChecked);
						editor.putBoolean("watchType", isChecked);
						editor.commit();// 提交修改
						if (BaseApp.isConnected(JVMoreFeatureActivity.this)) {
							CheckStateThread csThread = new CheckStateThread(
									JVMoreFeatureActivity.this);
							csThread.start();
						}
					}
				}
				break;
			case R.id.shakedevicebtn:
				editor.putBoolean("ShakeDevice", isChecked);
				editor.commit();// 提交修改
				break;
			}

		}

	};

	// 是否已经打开GPS
	public boolean isGPSEnable() {
		/*
		 * 用Setting.System来读取也可以，只是这是更旧的用法 String str =
		 * Settings.System.getString(getContentResolver(),
		 * Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		 */
		String str = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		Log.v("GPS", str);
		if (str != null) {
			return (str.contains("gps") || str.contains("network"));
		} else {
			return false;
		}
	}

	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back:
				JVMoreFeatureActivity.this.finish();
				break;
			case R.id.btn_right:
				// Intent shareInt=new Intent(Intent.ACTION_SEND);
				// shareInt.setType("text/plain");
				// shareInt.putExtra(Intent.EXTRA_SUBJECT, "选择分享方式");
				// shareInt.putExtra(Intent.EXTRA_TEXT, "OK");
				// shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(shareInt);
				break;
			case R.id.loginout:
				if (dialog == null) {
					dialog = new ProgressDialog(JVMoreFeatureActivity.this);
				}
				dialog.setMessage(getResources().getString(
						R.string.str_logining_out));
				if (!dialog.isShowing()) {
					dialog.show();
				}
				OutThread outThread = new OutThread(JVMoreFeatureActivity.this);
				outThread.start();

				break;
			// case R.id.sharelayout:
			//
			// // Intent shareInt=new Intent(Intent.ACTION_SEND);
			// // shareInt.setType("text/plain");
			// // shareInt.putExtra(Intent.EXTRA_SUBJECT, "选择分享方式");
			// // shareInt.putExtra(Intent.EXTRA_TEXT, "中维世纪");
			// // shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// //
			// // startActivity(shareInt);
			//
			//
			//
			//
			// if (null == accessToken) {
			// mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
			// // 3.0及以上支持sso，否则用oauth2.0
			// if (android.os.Build.VERSION.SDK_INT >= 11) {
			// /**
			// * 下面两行代码，仅当sdk支持sso时有效，
			// */
			//
			// mSsoHandler = new SsoHandler(
			// JVMoreFeatureActivity.this, mWeibo);
			// mSsoHandler.authorize(new AuthDialogListener());
			// } else {
			// mWeibo.authorize(JVMoreFeatureActivity.this,
			// new AuthDialogListener());
			// }
			// } else {
			// Intent shareIntent = new Intent();
			// shareIntent.setClass(JVMoreFeatureActivity.this,
			// JVShareActivity.class);
			// JVMoreFeatureActivity.this.startActivity(shareIntent);
			// }
			// break;
			case R.id.alarmbtn:
				// Toast.makeText(JVMoreFeatureActivity.this, "alarmbtn",
				// Toast.LENGTH_SHORT).show();
				break;
			case R.id.positionbtn:
				// Toast.makeText(JVMoreFeatureActivity.this, "positionbtn",
				// Toast.LENGTH_SHORT).show();
				break;
			case R.id.accountlayout:
				Intent intentEdit = new Intent(JVMoreFeatureActivity.this,
						JVEditPassActivity.class);
				JVMoreFeatureActivity.this.startActivity(intentEdit);
				break;
			case R.id.alarmlayout1:
				Intent alarmIntent = new Intent(JVMoreFeatureActivity.this,
						JVNoticeActivity.class);
				JVMoreFeatureActivity.this.startActivity(alarmIntent);
				break;

			case R.id.aboutlayout:
				// Toast.makeText(JVMoreFeatureActivity.this, "aboutlayout",
				// Toast.LENGTH_SHORT).show();
				break;
			case R.id.checkupdatelayout:
				// 检查新版本
				if (dialog == null) {
					dialog = new ProgressDialog(JVMoreFeatureActivity.this);
				}
				dialog.setMessage(getResources().getString(
						R.string.str_checkupdating));
				if (!dialog.isShowing()) {
					dialog.show();
				}
				updateThread = new UpdateThread(JVMoreFeatureActivity.this,
						JVConst.JVMORE_ACTIVITY_FLAG);
				updateThread.start();
				break;
			case R.id.feedbacklayout:// 反馈
				// // 给someone@domain.com发邮件
				// Uri uri = Uri.parse("mailto:someone@domain.com");
				// Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				// startActivity(intent);
				// Intent emailIntent = new Intent(Intent.ACTION_SEND);
				// String subject = "["
				// + getResources().getString(R.string.app_name) + "]"
				// + getResources().getString(R.string.str_feedback);
				// String body = BaseApp.mobileInfo(JVMoreFeatureActivity.this);
				//
				// String[] extra = new String[] {
				// getResources().getString(R.string.str_feedback_email),
				// getResources().getString(R.string.str_feedback_email2),
				// getResources().getString(R.string.str_feedback_email3) };
				// emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
				// emailIntent.putExtra(Intent.EXTRA_TEXT, body);
				// emailIntent.putExtra(Intent.EXTRA_EMAIL, extra);
				// try {
				// emailIntent.setType("message/rfc822");
				// JVMoreFeatureActivity.this.startActivity(emailIntent);
				// } catch (Exception e) {
				// BaseApp.showTextToast(JVMoreFeatureActivity.this,
				// R.string.str_feedback_error);
				// e.printStackTrace();
				// }

				// // 给多人发邮件
				// Intent intent=new Intent(Intent.ACTION_SEND);
				// String[] tos = {"1@abc.com", "2@abc.com"}; // 收件人
				// String[] ccs = {"3@abc.com", "4@abc.com"}; // 抄送
				// String[] bccs = {"5@abc.com", "6@abc.com"}; // 密送
				// intent.putExtra(Intent.EXTRA_EMAIL, tos);
				// intent.putExtra(Intent.EXTRA_CC, ccs);
				// intent.putExtra(Intent.EXTRA_BCC, bccs);
				// intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
				// intent.putExtra(Intent.EXTRA_TEXT, "Hello");
				// intent.setType("message/rfc822");
				// startActivity(intent);

				if (!ConfigUtil.getNetWorkConnection()) {
					BaseApp.alertNetDialog(JVMoreFeatureActivity.this);
				} else {
					Intent feedbackIntent = new Intent();
					feedbackIntent.setClass(JVMoreFeatureActivity.this,
							JVFeedbackActivity.class);
					JVMoreFeatureActivity.this.startActivity(feedbackIntent);
				}
				break;
			case R.id.positionlayout:
				if (positionBtn.isChecked()) {
					positionBtn.setChecked(false);
				} else {
					positionBtn.setChecked(true);
				}
				break;
			case R.id.scenepiclayout:
				if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
					if (scenepicBtn.isChecked()) {
						scenepicBtn.setChecked(false);
					} else {
						scenepicBtn.setChecked(true);
					}
				}
				break;
			case R.id.helpadvicelayout:
				if (helpBtn.isChecked()) {
					helpBtn.setChecked(false);
				} else {
					helpBtn.setChecked(true);
				}
				break;
			case R.id.alarmlayout2:
				if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
					if (alarmBtn.isChecked()) {
						alarmBtn.setChecked(false);
					} else {
						alarmBtn.setChecked(true);
					}
				}
				break;
			case R.id.watchtypelayout:
				if (!Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN))) {
					if (watchTypeBtn.isChecked()) {
						watchTypeBtn.setChecked(false);
					} else {
						watchTypeBtn.setChecked(true);
					}
				}
				break;
			case R.id.videomodelayout:// 观看模式

				if (browseModeBtn.isChecked()) {
					browseModeBtn.setChecked(false);
				} else {
					browseModeBtn.setChecked(true);
				}
				break;
			case R.id.shakelayout:// 摇一摇加设备
				if (shakeDeviceBtn.isChecked()) {
					shakeDeviceBtn.setChecked(false);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							JVMoreFeatureActivity.this);
					builder.setMessage(R.string.str_shake_device_tips);
					builder.setCancelable(true);
					builder.setPositiveButton(R.string.str_open_shake_device,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									shakeDeviceBtn.setChecked(true);
									dialog.dismiss();
								}
							});
					builder.setNegativeButton(
							R.string.str_shake_device_learn_more,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");

									String urlStr = "";

									if (ConfigUtil.isLanZH()) {// 中文
										urlStr = Url.LEARN_MORE;
									} else {// 英文
										urlStr = Url.LEARN_MORE_EN;
									}
									Uri url = Uri.parse(urlStr);
									intent.setData(url);
									startActivity(intent);
								}
							});
					builder.create().show();
				}
				break;

			}
		}

	};

	// 注销线程
	private static class OutThread extends Thread {

		private final WeakReference<JVMoreFeatureActivity> mActivity;

		public OutThread(JVMoreFeatureActivity activity) {
			mActivity = new WeakReference<JVMoreFeatureActivity>(activity);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			JVMoreFeatureActivity activity = mActivity.get();
			if (null != activity && !activity.isFinishing()) {
				try {
					BaseApp.saveDeviceToLocal(activity.sharedPreferences,
							activity.editor, Boolean
									.getBoolean(activity.statusHashMap
											.get(Consts.LOCAL_LOGIN)));
					// Log.e("注销1", "进注销");
					if (!activity.direDis) {
						// if (null != JVPlayActivity.getInstance()) {
						// Log.e("注销1 -1", "进注销");
						// if (null != BaseApp.channelMap
						// && 0 != BaseApp.channelMap.size()) {
						// JVSChannel jvschannel = BaseApp.channelMap
						// .get(BaseApp.windowIndex);
						// // 推掉线前断开该断开的（音频监听，对讲），保存该保存的
						// // Log.e("注销1-2", "进注销");
						// if (null != jvschannel && jvschannel.isConnected()) {
						// // Log.e("注销1-3", "进注销");
						// if (0 == 0) {// 正常播放
						// // 正在录像暂停录像
						// BaseApp.stopRecord(jvschannel);
						// // Log.e("注销1-3", "进注销");
						// // 音频监听
						// if (BaseApp.AUDIO_FLAG) {
						//
						// if (jvschannel.is05StartCode) {
						// JVS1.JAD_D2(0);
						// }
						// }
						// // Log.e("注销1-4", "进注销");
						// // 若正在语音对讲关闭对讲
						// if (BaseApp.VOICE_CALL_FLAG) {
						// BaseApp.VOICE_CALL_FLAG = false;
						// // Log.e("注销1-5", "进注销");
						// // JVSUDT.JVC_SendData(
						// // BaseApp.windowIndex,
						// // (byte) JVNetConst.JVN_CMD_CHATSTOP,
						// // new byte[0], 0);
						// // Log.e("注销1-6", "进注销");
						//
						// // // Log.e("注销1-7", "进注销");
						// // if (null != JVSUDT.arBean) {//
						// // 暂停采集声音起
						// // JVSUDT.arBean.stopRecording();
						// // }
						// // Log.e("注销1-8", "进注销");
						// }
						// // Log.e("注销1-9", "进注销");
						// } else {// 远程回放
						// // 正在录像暂停录像
						// // Log.e("注销1-10", "进注销");
						//
						// BaseApp.stopRecord(jvschannel);
						// // Log.e("注销1-11", "进注销");
						// // JVSUDT.PLAY_FLAG = 0;
						// //
						// JVPlayActivity.getInstance().currentMenu.setText(R.string.str_video_play);//正常播放
						// }
						// }
						// // Log.e("注销2", "停止录像");
						//
						// if (0 == 0) {// 正常播放
						// try {
						// for (int key : BaseApp.channelMap.keySet()) {
						// // for (Map.Entry entry :
						// //
						// JVPlayActivity.getInstance().channelMap.entrySet())
						// // { //把别的通道都暂停掉
						// // int key = (Integer) entry.getKey();
						// JVSChannel channel = null;
						//
						// if (null != BaseApp.channelMap
						// && BaseApp.channelMap.size() > 0) {
						// channel = (JVSChannel) BaseApp.channelMap
						// .get(key);
						// }
						//
						// if (channel.isConnected()) {
						// // Log.e("注销2-1", "停止录像");
						// // Log.e("注销2-1", "停止录像");
						// // JVSUDT.JVC_SendData(
						// // key + 1,
						// // (byte)
						// // JVNetConst.JVN_CMD_VIDEOPAUSE,
						// // new byte[0], 0);
						// // Log.e("注销2-2", "停止录像");
						// channel.stopPrimaryChannel(key);
						// // Log.e("注销2-3", "停止录像");
						// } else if (channel.isConnecting) {
						// // Log.e("注销2-4", "停止录像");
						// channel.stopPrimaryChannel(key);
						// // Log.e("注销2-5", "停止录像");
						// }
						//
						// }
						// } catch (Exception e) {
						// // TODO: handle exception
						// e.printStackTrace();
						// }
						//
						// } else {
						// for (int key : BaseApp.channelMap.keySet()) { //
						// 把别的通道都暂停掉
						// JVSChannel channel = null;
						//
						// if (null != BaseApp.channelMap
						// && BaseApp.channelMap.size() > 0) {
						// channel = (JVSChannel) BaseApp.channelMap
						// .get(key);
						// }
						// if (key != BaseApp.windowIndex) {
						// // Log.e("注销2-6", "停止录像");
						// if (channel.isConnected()) {
						// // Log.e("注销2-7", "停止录像");
						// // JVSUDT.JVC_SendData(
						// // key + 1,
						// // (byte)
						// // JVNetConst.JVN_CMD_VIDEOPAUSE,
						// // new byte[0], 0);
						// // Log.e("注销2-8", "停止录像");
						// channel.stopPrimaryChannel(key);
						// // Log.e("注销2-9", "停止录像");
						// } else if (channel.isConnecting) {
						// // Log.e("注销2-10", "停止录像");
						// channel.stopPrimaryChannel(key);
						// // Log.e("注销2-1", "停止录像");
						// }
						// } else {
						// // Log.e("注销2-11", "停止录像");
						// if (null != BaseApp.videoList) {
						// BaseApp.videoList.clear();
						// }
						// channel.isWaitIFrame = false;
						// channel.setPktWaitIFrame(false);
						// channel.setDecoderFirstTime(true);
						// channel.playBackheight = 0;
						// channel.playBackWidth = 0;
						// // Log.e("注销2-12", "停止录像");
						// // JVSUDT.JVC_SendData(
						// // key + 1,
						// // (byte) JVNetConst.JVN_CMD_PLAYSTOP,
						// // new byte[0], 0);
						// // Log.e("注销2-13", "停止录像");
						// while (true) {
						//
						// if (!channel.userDecoderFlag) {
						//
						// if (!channel.isStandardDeocder) {
						//
						// JVS1.JVD04_DecodeClose(key);
						// JVS1.JVD04_DecodeOpen(
						// channel.bmWidth,
						// channel.bmHeight,
						// key);
						// channel.isOpenDecoder = true;
						// } else {
						//
						// JVS5.JVD05_DecodeClose(key);
						// JVS5.JVD05_DecodeOpen(key,
						// channel.bmWidth,
						// channel.bmHeight);
						// Log.e("tags",
						// "run channge play");
						// channel.isOpenDecoder = true;
						// }
						// break;
						// }
						//
						// }
						//
						// Thread.sleep(1 * 1000);
						// // Log.e("注销2-14", "停止录像");
						// // JVSUDT.JVC_SendData(
						// // key + 1,
						// // (byte) JVNetConst.JVN_CMD_VIDEO,
						// // new byte[0], 0);
						//
						// Thread.sleep(1 * 1000);
						// if (channel.isConnected()) {
						// // Log.e("注销2-15", "停止录像");
						// channel.stopPrimaryChannel(key);
						// // Log.e("注销2-16", "停止录像");
						// }
						// // Log.e("注销2-17", "停止录像");
						// // JVSUDT.PLAY_FLAG = 0;
						// }
						//
						// }
						// }
						// // Log.e("注销3", "卡在断开");
						// }
					}
					// }

					// 将数据接免登陆的状态置为0

					BaseApp.resetUser();

					// //将用户名密码写入缓存
					// editor.putString("UserName", "");
					// editor.putString("PassWord", "");
					// // editor.putBoolean("PassMD5", false);
					// editor.commit();
					// Log.e("注销4-1", "卡在销毁保持在线");
					// 销毁保持在线服务
					if (JVConst.KEEP_ONLINE_FLAG) {// 已经开启服务
						Intent serviceIntent = new Intent(activity
								.getResources().getString(
										R.string.str_offline_class_name));
						activity.stopService(serviceIntent);
						JVConst.KEEP_ONLINE_FLAG = false;
					}
					Log.e("注销4", "卡在销毁保持在线");

					// 本地登录，将数据库中所有ip和断开都改成默认的
					// if(BaseApp.LOCAL_LOGIN_FLAG){
					// BaseApp.modifyAllIPPORT();
					// }
					Log.e("注销5", "卡在修改数据库");
					// if(null != AsyncImageLoader.imageCache){
					// AsyncImageLoader.imageCache.clear();
					// }

					if (null != BaseApp.deviceList
							&& 0 != BaseApp.deviceList.size()) {
						BaseApp.deviceList.clear();
					}

					// // 销毁广播搜索服务
					// JVSUDT.JVC_StopLANSerchServer();

					// try {
					// this.sleep(500);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

					Log.e("注销6", "卡在销毁activity");
					// 允许获取推送消息
					// if (null != activity.sharedPreferences
					// && activity.sharedPreferences.getBoolean(
					// "PushMessage", false)
					// && !BaseApp.LOCAL_LOGIN_FLAG) {
					// boolean closeFlag = JVClient.JVStopKeepOnline();
					//
					// Log.v("注销7关推送", closeFlag + "");
					// BaseApp.pushList.clear();
					// // JVClient.JVRelease();
					// // Log.v("注销8资源释放", closeFlag+"");
					// if (closeFlag) {
					// String lang = String.valueOf(BaseApp.getLan());
					// LoginUtil.loginSuccessToConfirm(
					// BaseApp.getIMEI(activity), lang,
					// LoginUtil.userName, "false");
					// }
					//
					// }
					AccountUtil.signOut(activity);
					Message msg = BaseApp.moreHandler.obtainMessage();
					msg.what = JVConst.LOG_OUT_SUCCESS;
					BaseApp.moreHandler.sendMessage(msg);// 注销完
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
			}
		}

	}

	// 检查软件更新handler
	public static class MoreHandler extends Handler {
		private final WeakReference<JVMoreFeatureActivity> mActivity;

		public MoreHandler(JVMoreFeatureActivity activity) {
			mActivity = new WeakReference<JVMoreFeatureActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			JVMoreFeatureActivity activity = mActivity.get();
			if (null != activity && !activity.isFinishing()) {
				switch (msg.what) {

				case JVConst.LOG_OUT_SUCCESS:// 注销完成
					// if(null != BaseApp.mNotificationManager){
					// BaseApp.mNotificationManager.cancel(0);
					// }
					// BaseApp.release();
					if (null != activity.dialog && activity.dialog.isShowing()) {
						try {
							activity.dialog.dismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					activity.dialog = null;
					try {
						Intent intent = new Intent();
						intent.setClass(activity, JVLoginActivity.class);
						activity.startActivity(intent);
						activity.finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case JVConst.CHECK_UPDATE_SUCCESS:
					if (null != activity.dialog && activity.dialog.isShowing()) {
						activity.dialog.dismiss();
					}
					activity.dialog = null;
					Bundle data = msg.getData();
					JVUpdate jvUpdate = new JVUpdate(activity);
					jvUpdate.checkUpdateInfo(data.getString("updateContent"));
					break;
				case JVConst.CHECK_NO_UPDATE:
					if (null != activity.dialog && activity.dialog.isShowing()) {
						activity.dialog.dismiss();
					}
					activity.dialog = null;
					activity.showTextToast(R.string.str_already_newest);
					// Toast.makeText(JVMoreFeatureActivity.this,
					// R.string.str_already_newest, Toast.LENGTH_LONG).show();
					break;
				case JVConst.CHECK_UPDATE_FAILED:
					if (null != activity.dialog && activity.dialog.isShowing()) {
						activity.dialog.dismiss();
					}
					activity.dialog = null;
					activity.showTextToast(R.string.str_check_update_failed);
					// Toast.makeText(JVMoreFeatureActivity.this,
					// R.string.str_check_update_failed,
					// Toast.LENGTH_LONG).show();
					break;
				case JVConst.REFRESH_PUSH_COUNT:

					BaseApp.WEBCC_INTERFACE = false;
					if (null != activity.dialog && activity.dialog.isShowing()) {
						activity.dialog.dismiss();
					}
					activity.dialog = null;

					int count = 0;
					BaseApp.getPushList();
					count = BaseApp.pushList.size();
					activity.alarmInfo.setText(activity.getResources()
							.getString(R.string.str_alarm_info)
							+ "("
							+ String.valueOf(count) + ")");
					activity.alarmBtn.setChecked(activity.sharedPreferences
							.getBoolean("PushMessage", false));

					if (null != activity.sharedPreferences
							&& activity.sharedPreferences.getBoolean(
									"PushMessage", false)) {
						activity.alarmBtn.setChecked(true);
					} else {
						activity.alarmBtn.setChecked(false);
					}

					break;
				case JVConst.DEVICE_SEARCH_BROADCAST:// 广播设备列表

					// 添加设备广播搜索通道数量
					BaseApp.initBroadCast();
					// JVSUDT.BROADCAST_DEVICELIST_FLAG = true;// 广播设备列表
					BaseApp.sendBroadCast();

					break;
				case JVConst.UPDATE_DEVICE_STATE_SUCCESS:
					if (!Boolean.valueOf(activity.statusHashMap
							.get(Consts.LOCAL_LOGIN))) {
						if (activity.watchTypeBtn.isChecked()) {
							BaseApp.deviceList = BaseApp
									.orderByOnlineState(BaseApp.deviceList);
						} else {
							BaseApp.deviceList = BaseApp
									.allDeviceOnline(BaseApp.deviceList);
						}
					}
					if (activity.dialog.isShowing()) {
						activity.dialog.dismiss();
					}
					if (!activity.watchTypeBtn.isChecked()) {

						activity.showTextToast(R.string.str_setting_offline);
					}
					break;
				default:
					break;
				}
			}

		}

	};

	// // 授权登录
	// class AuthDialogListener implements WeiboAuthListener {
	//
	// @Override
	// public void onComplete(Bundle values) {
	// String token = values.getString("access_token");
	// String expires_in = values.getString("expires_in");
	// JVMoreFeatureActivity.accessToken = new Oauth2AccessToken(token,
	// expires_in);
	// if (JVMoreFeatureActivity.accessToken.isSessionValid()) {
	// String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
	// .format(new java.util.Date(
	// JVMoreFeatureActivity.accessToken
	// .getExpiresTime()));
	// // Log.e("", "认证成功: \r\n access_token: " + token + "\r\n"
	// // + "expires_in: " + expires_in + "\r\n有效期：" + date);
	// /**
	// * 认证成功: access_token: 2.00l5EAtB0UO6ar095e2002f26DDMeB
	// * expires_in: 157674034 有效期：2018/06/13 14:10:13
	// *
	// */
	//
	// try {
	// Class sso = Class
	// .forName("com.weibo.sdk.android.api.WeiboAPI");//
	// 如果支持weiboapi的话，显示api功能演示入口按钮
	// // apiBtn.setVisibility(View.VISIBLE);
	// } catch (ClassNotFoundException e) {
	// // e.printStackTrace();
	// Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI not found");
	//
	// }
	// AccessTokenKeeper.keepAccessToken(JVMoreFeatureActivity.this,
	// accessToken);
	//
	// // Toast.makeText(JVMoreFeatureActivity.this, "认证成功",
	// // Toast.LENGTH_SHORT).show();
	// showTextToast(getResources().getString(R.string.str_sina_success));
	// Intent shareIntent = new Intent();
	// shareIntent.setClass(JVMoreFeatureActivity.this, JVShareActivity.class);
	// JVMoreFeatureActivity.this.startActivity(shareIntent);
	// }
	// }
	//
	// @Override
	// public void onError(WeiboDialogError e) {
	// // showTextToast("Auth error : " + e.getMessage());
	// // Toast.makeText(getApplicationContext(),
	// // "Auth error : " + e.getMessage(), Toast.LENGTH_LONG).show();
	// }
	//
	// @Override
	// public void onCancel() {
	// // showTextToast("Auth cancel");
	// // Toast.makeText(getApplicationContext(), "Auth cancel",
	// // Toast.LENGTH_LONG).show();
	// }
	//
	// @Override
	// public void onWeiboException(WeiboException e) {
	// // showTextToast("Auth exception : " + e.getMessage());
	// // Toast.makeText(getApplicationContext(),
	// // "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
	// // .show();
	// }
	//
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == GPS_SWITCH) {
			positionBtn.setChecked(isGPSEnable());
		} else {
			// /**
			// * 下面两个注释掉的代码，仅当sdk支持sso时有效，
			// */
			// if (mSsoHandler != null) {
			// mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
			// }
		}

	}

	//
	// class MyRequestListener implements RequestListener{
	//
	// @Override
	// public void onComplete(String arg0) {
	// // TODO Auto-generated method stub
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// Toast.makeText(JVMoreFeatureActivity.this, "微博发布成功",
	// Toast.LENGTH_LONG).show();
	// }
	// });
	// }
	//
	// @Override
	// public void onError(WeiboException arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onIOException(final IOException arg0) {
	// // TODO Auto-generated method stub
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// Toast.makeText(
	// JVMoreFeatureActivity.this,
	// String.format("发布失败" + ":%s",
	// arg0.getMessage()), Toast.LENGTH_LONG).show();
	// }
	// });
	// }
	//
	// }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		int count = 0;
		BaseApp.getPushList();
		count = BaseApp.pushList.size();
		alarmInfo.setText(getResources().getString(R.string.str_alarm_info)
				+ "(" + String.valueOf(count) + ")");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();
	}

	class CheckStateThread extends Thread {
		private Context context;

		public CheckStateThread(JVMoreFeatureActivity jvMoreFeatureActivity) {
			context = jvMoreFeatureActivity;
		}

		@Override
		public void run() {
			super.run();
			Message msg = BaseApp.moreHandler.obtainMessage();
			if (null != BaseApp.deviceList && 0 != BaseApp.deviceList.size()) {// 获取设备成功,去广播设备列表
				if (!ConfigUtil.is3G(context, false)) {
					BaseApp.broadDeviceList();
					msg.what = JVConst.DEVICE_SEARCH_BROADCAST;// 发广播搜索局域网设备（非3g搜索局域网）
					BaseApp.broadcastState = JVConst.CHECK_STATE_BROADCAST;
					BaseApp.moreHandler.sendMessage(msg);
				} else {
					// boolean result = LoginUtil.getDeviceInfo(0);
					msg.what = JVConst.UPDATE_DEVICE_STATE_SUCCESS;
					BaseApp.moreHandler.sendMessage(msg);
				}
			} else {
				msg.what = JVConst.UPDATE_DEVICE_STATE_SUCCESS;
				BaseApp.moreHandler.sendMessage(msg);
			}
		}
	}

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

	}

	@Override
	protected void saveSettings() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

}
