package com.jovision.activities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.test.AutoLoad;
import android.view.WindowManager;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.User;
import com.jovision.commons.BaiOSS;
import com.jovision.commons.BaseOSS;
import com.jovision.commons.JVConst;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.commons.MyUtils;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.UserUtil;

public class JVWelcomeActivity extends BaseActivity {

	private final String TAG = "JVWelcomeActivity";
	private Handler initHandler;

	private static boolean HAS_LOADED = false;

	static {
		HAS_LOADED = AutoLoad.foo();
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		MySharedPreference.init(getApplication());
		ConfigUtil.getJNIVersion();

		getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
		statusHashMap.put(Consts.KEY_INIT_ACCOUNT_SDK, "false");
		statusHashMap.put(Consts.KEY_INIT_CLOUD_SDK, "false");
		statusHashMap.put(Consts.IMEI,
				ConfigUtil.getIMEI(JVWelcomeActivity.this));
		statusHashMap.put(Consts.NEUTRAL_VERSION, "false");// 默认非中性版本
		statusHashMap.put(Consts.LOCAL_LOGIN, "false");// 默认非本地登陆
		statusHashMap.put(Consts.SCREEN_WIDTH,
				String.valueOf(disMetrics.widthPixels));
		statusHashMap.put(Consts.SCREEN_HEIGHT,
				String.valueOf(disMetrics.heightPixels));
		initThread.start();

		initHandler = new Handler();
		initHandler.postDelayed(jumpThread, 4000);

		new Thread() {

			@Override
			public void run() {
				// [Neo] 0. precheck
				Date last = new Date(MySharedPreference.getLong(
						Consts.KEY_LAST_PUT_STAMP, 1l));

				Date current = MyUtils.getChinaTime();
				if (null == current) {
					current = new Date();
					MyLog.ub("# localtime: " + current);
				} else {
					MyLog.ub("# remotetime: " + current);
				}

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				if (format.format(last).equals(format.format(current))) {
					return;
				}

				// [Neo] 1. zip
				MyLog.w(Consts.TAG_LOGICAL, "auto upload condition matched! "
						+ last + " => " + current);

				String folder = MyLog.getFolder(MyLog.UB);
				if (null == folder) {
					return;
				}

				boolean result = false;
				String target = folder + File.separator + current.getTime()
						+ ".zip";

				MyLog.ub(":id-fingerprint:" + statusHashMap.get(Consts.IMEI)
						+ ":" + Build.FINGERPRINT);
				MyLog.enableUB(false);
				result = MyUtils.zip(target,
						new File[] { new File(MyLog.getPath(MyLog.UB)) });

				if (false == result) {
					MyLog.e(Consts.TAG_LOGICAL, "zip failed!");
					return;
				}

				// [Neo] 2. put & clean
				try {
					BaseOSS oss = BaiOSS.getInstance(
							Consts.BO_ID,
							Consts.BO_SECRET,
							BaseOSS.BUCKET_PREFIX
									+ new SimpleDateFormat(
											BaseOSS.BUCKET_SUBFIX_FORMATTER)
											.format(current));
					result = oss.put(target);
					oss.deinit();
				} catch (Exception e) {
					result = false;
				}

				new File(target).delete();

				if (result) {
					MyLog.clean(MyLog.UB);
				}
				MyLog.enableUB(true);

				// [Neo] 3. update config
				if (result) {
					MySharedPreference.putLong(Consts.KEY_LAST_PUT_STAMP,
							current.getTime());
				}
			}

		}.start();

		// SIMCardUtil siminfo = new SIMCardUtil(this);
		// System.out.println(siminfo.getProvidersName());
		// System.out.println(siminfo.getNativePhoneNumber());
		//
		// MyLog.v("tel",
		// siminfo.getNativePhoneNumber() + "--"
		// + siminfo.getProvidersName());
	}

	@Override
	protected void initUi() {
		MyLog.v(TAG, "initUi");
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome_layout);
	}

	@Override
	protected void saveSettings() {
	}

	@Override
	protected void freeMe() {
		// 关闭此定时器
		initHandler.removeCallbacks(initThread);
		dismissDialog();
	}

	/**
	 * 时间到跳转线程
	 */
	Thread jumpThread = new Thread() {
		@Override
		public void run() {
			Intent intent = new Intent();
			// 首次登陆,且非公共版本
			if (MySharedPreference.getBoolean(Consts.KEY_SHOW_GUID, true)
					&& "false".equalsIgnoreCase(statusHashMap
							.get(Consts.NEUTRAL_VERSION))) {

				intent.setClass(JVWelcomeActivity.this, JVGuideActivity.class);

				if (ConfigUtil.isLanZH()) {// 中文
					intent.putExtra("ArrayFlag", JVConst.LANGUAGE_ZH);
				} else {// 英文或其他
					intent.putExtra("ArrayFlag", JVConst.LANGUAGE_EN);
				}
			} else {
				User user = UserUtil.getLastUser();
				if (ConfigUtil.getNetWorkConnection() && null != user
						&& !"".equalsIgnoreCase(user.getUserName())
						&& !"".equalsIgnoreCase(user.getUserPwd())) {
					statusHashMap.put(Consts.KEY_USERNAME, user.getUserName());
					statusHashMap.put(Consts.KEY_PASSWORD, user.getUserPwd());
					createDialog(R.string.login_str_loging);

					intent.setClass(JVWelcomeActivity.this,
							JVLoginActivity.class);
					intent.putExtra("AutoLogin", true);
					intent.putExtra("UserName", user.getUserName());
					intent.putExtra("UserPass", user.getUserPwd());
				} else {
					intent.setClass(JVWelcomeActivity.this,
							JVLoginActivity.class);
				}

			}
			JVWelcomeActivity.this.startActivity(intent);
			JVWelcomeActivity.this.finish();
		}
	};
	/**
	 * 欢迎界面的一些初始化，设置设备小助手
	 */
	Thread initThread = new Thread() {
		@Override
		public void run() {
			MyLog.w(TAG, "initThread E");

			// [Neo] TODO set timer for timeout
			while (false == HAS_LOADED) {
				try {
					sleep(100);
				} catch (InterruptedException e) {
				}
			}

			// [Neo] you fool!
			ConfigUtil.initCloudSDK(getApplication());// 初始化CloudSDK
			// HelperUtil.setHelpToSavedDevice();// 给设备设置小助手

			if (ConfigUtil.getNetWorkConnection()) {
				boolean initASdkState = ConfigUtil
						.initAccountSDK(getApplication());// 初始化账号SDK

				ConfigUtil.getIMEI(JVWelcomeActivity.this);

				if (!initASdkState) {// 初始化Sdk失败
					initASdkState = ConfigUtil.initAccountSDK(getApplication());// 初始化账号SDK
				}
				if (!initASdkState) {// 初始化Sdk失败
					handler.sendMessage(handler.obtainMessage(
							JVConst.INIT_ACCOUNT_SDK_FAILED, 0, 0));
				}

			}

			// 开启广播，顺序！顺序！
			ConfigUtil.startBroadCast();

			MyLog.w(TAG, "initThread X");
		}
	};

	/**
	 * 消息处理handler
	 * 
	 * @author Administrator
	 * 
	 */
	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case JVConst.INIT_ACCOUNT_SDK_FAILED:// 初始化SDK失败
			showTextToast(R.string.str_initsdk_failed);
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
