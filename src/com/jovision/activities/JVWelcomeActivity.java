package com.jovision.activities;

import android.content.Intent;
import android.os.Handler;
import android.test.AutoLoad;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.bean.User;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.DefaultExceptionHandler;
import com.jovision.utils.ImportOldData;
import com.jovision.utils.UserUtil;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;
import com.tencent.stat.common.StatConstants;
import com.umeng.analytics.MobclickAgent;

public class JVWelcomeActivity extends BaseActivity {

	private final String TAG = "JVWelcomeActivity";
	private Handler initHandler;

	private RelativeLayout cloudseeLayout;
	private RelativeLayout neturalLayout;

	// private static boolean HAS_LOADED = false;

	static {
		AutoLoad.foo();
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(
				this));
		ConfigUtil.getJNIVersion();
		ImportOldData importOld = new ImportOldData(JVWelcomeActivity.this);
		if (!MySharedPreference.getBoolean("HasImport")) {
			Consts.DEVICE_LIST = Consts.LOCAL_DEVICE_LIST;

			importOld.queryAllUserList();
			importOld.getDevList();
			deleteDatabase(Consts.JVCONFIG_DATABASE);
			MySharedPreference.putBoolean("HasImport", true);
		}

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
		MySharedPreference.putBoolean(Consts.AD_UPDATE, false);
		if (null == statusHashMap.get(Consts.KEY_LAST_LOGIN_TIME)
				|| "".equalsIgnoreCase(statusHashMap
						.get(Consts.KEY_LAST_LOGIN_TIME))) {
			statusHashMap.put(Consts.KEY_LAST_LOGIN_TIME,
					ConfigUtil.getCurrentDate());
		}

		initThread.start();

		initHandler = new Handler();
		initHandler.postDelayed(jumpThread, 4000);

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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome_layout);

		StatConfig.setDebugEnable(true);

		if (statusHashMap.get(Consts.NEUTRAL_VERSION).equals("false")) {
			String appkey = "A8IA5GMIL13M";
			try {
				StatService.startStatService(JVWelcomeActivity.this, appkey,
						StatConstants.VERSION);
			} catch (MtaSDkException e) {
				e.printStackTrace();
			}

			StatService.trackCustomEvent(JVWelcomeActivity.this, "onCreat",
					"welcome");
		}
		cloudseeLayout = (RelativeLayout) findViewById(R.id.cloudseewelcome);
		neturalLayout = (RelativeLayout) findViewById(R.id.neturalwelcome);

		if (statusHashMap.get(Consts.NEUTRAL_VERSION).equalsIgnoreCase("false")) {// CloudSEE
			cloudseeLayout.setVisibility(View.VISIBLE);
			neturalLayout.setVisibility(View.GONE);
		} else {
			cloudseeLayout.setVisibility(View.GONE);
			neturalLayout.setVisibility(View.VISIBLE);
		}

		if (!ConfigUtil.isConnected(JVWelcomeActivity.this)) {
			alertNetDialog();
		} else {
			ConfigUtil.is3G(JVWelcomeActivity.this, true);
		}
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
					intent.putExtra("ArrayFlag", Consts.LANGUAGE_ZH);
				} else {// 英文或其他
					intent.putExtra("ArrayFlag", Consts.LANGUAGE_EN);
				}
			} else {
				User user = UserUtil.getLastUser();
				if (ConfigUtil.getNetWorkConnection(JVWelcomeActivity.this)
						&& null != user
						&& !"".equalsIgnoreCase(user.getUserName())
						&& !"".equalsIgnoreCase(user.getUserPwd())) {
					statusHashMap.put(Consts.KEY_USERNAME, user.getUserName());
					statusHashMap.put(Consts.KEY_PASSWORD, user.getUserPwd());
					createDialog(R.string.logining, true);

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

			// // [Neo] TODO set timer for timeout
			// while (false == HAS_LOADED) {
			//
			// try {
			// sleep(100);
			// } catch (InterruptedException e) {
			// }
			// }

			ConfigUtil.initCloudSDK(getApplication());// 初始化CloudSDK

			if (ConfigUtil.getNetWorkConnection(JVWelcomeActivity.this)) {
				boolean initASdkState = ConfigUtil
						.initAccountSDK(getApplication());// 初始化账号SDK

				ConfigUtil.getIMEI(JVWelcomeActivity.this);

				if (!initASdkState) {// 初始化Sdk失败
					initASdkState = ConfigUtil.initAccountSDK(getApplication());// 初始化账号SDK
				}
				if (!initASdkState) {// 初始化Sdk失败
					handler.sendMessage(handler.obtainMessage(
							Consts.WHAT_INIT_ACCOUNT_SDK_FAILED, 0, 0));
				}

			}
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
		case Consts.WHAT_INIT_ACCOUNT_SDK_FAILED:// 初始化SDK失败
			showTextToast(R.string.str_initsdk_failed);
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
