package com.jovision.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.IHandlerLikeNotify;
import com.jovision.activities.JVFragmentIndicator.OnIndicateListener;
import com.jovision.adapters.MyPagerAdp;
import com.jovision.bean.Device;
import com.jovision.commons.CheckUpdateTask;
import com.jovision.commons.MyActivityManager;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.CacheUtil;
import com.jovision.utils.ConfigUtil;
import com.jovision.utils.PlayUtil;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

public class JVTabActivity extends ShakeActivity implements
		OnPageChangeListener {

	private static final String TAG = "JVTabActivity";

	public static final int TAB_BACK = 0x20;
	int flag = 0;
	private int currentIndex = 0;// 当前页卡index

	protected NotificationManager mNotifyer;
	protected int timer = 16;
	protected RelativeLayout helpbg_relative;
	protected Timer offlineTimer = new Timer();
	private BaseFragment mFragments[] = new BaseFragment[4];

	private ViewPager viewpager;

	private MyPagerAdp adp;

	private List<View> pics;

	private boolean page1;

	private boolean page2;

	private boolean localFlag;

	private ArrayList<Device> myDeviceList = new ArrayList<Device>();
	// 当前页面索引
	private int currentImage = 0;
	// 前一个页面索引
	private int oldImage = 0;
	// 点集合
	private List<ImageView> dots;
	private LinearLayout ll_dot;

	/** 3分钟广播 */
	private Timer broadTimer;
	private TimerTask broadTimerTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// // 如果savedInstanceState!=null，说明在应用在后台被干掉，或者应用崩掉需要重新create
		// if (savedInstanceState != null) {
		// Intent newApp = new Intent(JVTabActivity.this,
		// JVWelcomeActivity.class);
		// startActivity(newApp);
		// finish();
		// }
		MyLog.v(TAG, "onCreate----E");
		MyActivityManager.getActivityManager().pushAlarmActivity(this);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// 开启logcat输出，方便debug，发布时请关闭
		XGPushConfig.enableDebug(this, false);
		// 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(),
		// XGIOperateCallback)带callback版本
		// 如果需要绑定账号，请使用registerPush(getApplicationContext(),"account")版本
		// 具体可参考详细的开发指南
		// 传递的参数为ApplicationContext
		XGPushManager.registerPush(getApplicationContext(),
				new XGIOperateCallback() {
					@Override
					public void onSuccess(Object data, int flag) {
						MyLog.d("TPush", "注册成功，设备token为：" + data);
						if (MySharedPreference.getString(Consts.KEY_DEV_TOKEN)
								.equals("")) {
							MySharedPreference.putString(Consts.KEY_DEV_TOKEN,
									data.toString());
							AccountUtil
									.reportClientPlatformInfo(JVTabActivity.this);
						} else {
							MySharedPreference.putString(Consts.KEY_DEV_TOKEN,
									data.toString());
						}
					}

					@Override
					public void onFail(Object data, int errCode, String msg) {
						MyLog.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
						JVTabActivity.this.showTextToast("注册失败，错误码：" + errCode
								+ ",错误信息：" + msg);
					}
				});

		MyLog.v(TAG, "onCreate----X");
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void getPic() {
		flag = 0;
		pics = new ArrayList<View>();
		View view1 = LayoutInflater.from(JVTabActivity.this).inflate(
				R.layout.help_item1, null);
		View view2 = LayoutInflater.from(JVTabActivity.this).inflate(
				R.layout.help_item2, null);
		View view3 = LayoutInflater.from(JVTabActivity.this).inflate(
				R.layout.help_item3, null);
		View view4 = LayoutInflater.from(JVTabActivity.this).inflate(
				R.layout.help_item4, null);
		View view6 = LayoutInflater.from(JVTabActivity.this).inflate(
				R.layout.help_item6, null);
		if (localFlag) {
			pics.add(view1);
			pics.add(view2);
			pics.add(view3);
			pics.add(view4);
			pics.add(view6);
			initDot(4);
		} else {
			pics.add(view1);
			pics.add(view2);
			pics.add(view3);
			pics.add(view4);
			pics.add(view6);
			initDot(4);
		}
	}

	private void getPicone() {
		flag = 1;
		pics = new ArrayList<View>();
		View view = LayoutInflater.from(JVTabActivity.this).inflate(
				R.layout.help_item, null);
		View view6 = LayoutInflater.from(JVTabActivity.this).inflate(
				R.layout.help_item6, null);
		pics.add(view);
		pics.add(view6);
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				viewpager.setVisibility(View.GONE);
				ll_dot.setVisibility(View.GONE);
			}
		});
	}

	private void initDot(int dotnum) {
		dots = new ArrayList<ImageView>();
		// 得到点的父布局
		for (int i = 0; i < dotnum; i++) {
			ll_dot.getChildAt(i);// 得到点
			dots.add((ImageView) ll_dot.getChildAt(i));
			dots.get(i).setEnabled(false);// 将点设置为白色
		}
		dots.get(currentImage).setEnabled(true); // 因为默认显示第一张图片，将第一个点设置为黑色
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyLog.v(TAG, "onResume----E");
		Intent intent = getIntent();
		int index = intent.getIntExtra("tabIndex", -1);

		if (-1 != index) {
			currentIndex = index;
			android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
			if (null != manager) {
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.tab_fragment, mFragments[currentIndex])
						.commit();
			} else {
				MyLog.e(TAG, "TAB_onresume_manager null" + currentIndex);
				this.finish();
			}

		}
		MyLog.v(TAG, "onResume----X");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// //添加设备请求
		// if(JVMyDeviceFragment.ADD_DEV_REQUEST == requestCode){
		// if(JVAddDeviceActivity.ADD_DEV_SUCCESS == resultCode){
		// PlayUtil.broadCast(this);
		// }
		// }

	}

	@Override
	public void onBackPressed() {
		BaseFragment currentFrag = mFragments[currentIndex];
		if (null != currentFrag) {
			((IHandlerLikeNotify) currentFrag).onNotify(TAB_BACK, 0, 0, null);
		}
		openExitDialog();
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case 0:
			viewpager.setCurrentItem(arg1);
			break;
		default:
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
		// TODO 增加过滤
		switch (what) {
		case Consts.CALL_LAN_SEARCH: {
			// PlayUtil.broadIp(obj,JVTabActivity.this);
			// myDeviceList = CacheUtil.getDevList();
			JSONObject broadObj;
			try {
				broadObj = new JSONObject(obj.toString());
				if (0 == broadObj.optInt("timeout")) {
					String gid = broadObj.optString("gid");
					int no = broadObj.optInt("no");

					if (0 == no) {
						return;
					}
					String ip = broadObj.optString("ip");
					int port = broadObj.optInt("port");
					String broadDevNum = gid + no;
					int netmod = broadObj.optInt("netmod");
					PlayUtil.hasDev(myDeviceList, broadDevNum, ip, port, netmod);

				} else if (1 == broadObj.optInt("timeout")) {
					PlayUtil.sortList(myDeviceList, JVTabActivity.this);
					CacheUtil.saveDevList(myDeviceList);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			BaseFragment currentFrag = mFragments[currentIndex];
			if (null != currentFrag) {
				((IHandlerLikeNotify) currentFrag).onNotify(what, arg1, arg2,
						obj);
			}
			break;
		}
		default:
			BaseFragment currentFrag = mFragments[currentIndex];
			if (null != currentFrag) {
				((IHandlerLikeNotify) currentFrag).onNotify(what, arg1, arg2,
						obj);
			}
			break;
		}
	}

	@Override
	protected void initSettings() {
		MySharedPreference.init(getApplication());
		ConfigUtil.getJNIVersion();
		Intent intent = getIntent();
		currentIndex = intent.getIntExtra("tabIndex", 0);
		localFlag = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));
	}

	@Override
	protected void initUi() {
		super.initUi();
		MyLog.v(TAG, "initUi----E");
		setContentView(R.layout.tab_layout);
		startBroadTimer();
		viewpager = (ViewPager) findViewById(R.id.tab_viewpager);
		viewpager.setOnPageChangeListener(JVTabActivity.this);
		JVFragmentIndicator mIndicator = (JVFragmentIndicator) findViewById(R.id.indicator);
		JVFragmentIndicator.setIndicator(currentIndex);

		Boolean local = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));
		if (local) {
			Consts.DEVICE_LIST = Consts.LOCAL_DEVICE_LIST;
		} else {
			Consts.DEVICE_LIST = Consts.CACHE_DEVICE_LIST;
			// 在线登陆,检查更新
			CheckUpdateTask task = new CheckUpdateTask(JVTabActivity.this);
			String[] strParams = new String[3];
			strParams[0] = "0";// 0,自动检查更新
			task.execute(strParams);
		}

		mFragments[0] = new JVMyDeviceFragment();
		mFragments[1] = new JVInfoFragment();
		mFragments[2] = new JVDeviceManageFragment();
		mFragments[3] = new JVMoreFragment();
		if (!MySharedPreference.getBoolean("page2")) {
			ll_dot = (LinearLayout) findViewById(R.id.tab_ll_dot);
			ll_dot.setVisibility(View.GONE);
			viewpager.setCurrentItem(0);
			viewpager.setVisibility(View.VISIBLE);
			getPicone();
			adp = new MyPagerAdp(pics);
			viewpager.setAdapter(adp);
			MySharedPreference.putBoolean("page2", true);
		}
		mIndicator.setOnIndicateListener(new OnIndicateListener() {
			@Override
			public void onIndicate(View v, int which) {
				try {
					currentIndex = which;
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.tab_fragment, mFragments[which])
							.commit();
					switch (which) {
					case 0:
						if (!page2 && !MySharedPreference.getBoolean("page2")) {
							ll_dot = (LinearLayout) findViewById(R.id.tab_ll_dot);
							ll_dot.setVisibility(View.GONE);
							viewpager.setCurrentItem(0);
							viewpager.setVisibility(View.VISIBLE);
							getPicone();
							adp = new MyPagerAdp(pics);
							viewpager.setAdapter(adp);
							MySharedPreference.putBoolean("page2", true);
						} else {
							if (MySharedPreference.getBoolean("HELP")
									&& !MySharedPreference.getBoolean("page2")) {
								ll_dot = (LinearLayout) findViewById(R.id.tab_ll_dot);
								ll_dot.setVisibility(View.GONE);
								viewpager.setCurrentItem(0);
								viewpager.setVisibility(View.VISIBLE);
								getPicone();
								adp = new MyPagerAdp(pics);
								viewpager.setAdapter(adp);
								MySharedPreference.putBoolean("page2", false);
								page2 = true;
							}
						}
						break;
					case 2:
						myDeviceList = CacheUtil.getDevList();
						if (0 != myDeviceList.size()) {
							if (!page1
									&& !MySharedPreference.getBoolean("page1")) {
								ll_dot = (LinearLayout) findViewById(R.id.tab_ll_dot);
								ll_dot.setVisibility(View.VISIBLE);
								viewpager.setCurrentItem(0);
								viewpager.setVisibility(View.VISIBLE);
								getPic();
								adp = new MyPagerAdp(pics);
								viewpager.setAdapter(adp);
								MySharedPreference.putBoolean("page1", true);
							} else {
								if (MySharedPreference.getBoolean("HELP")
										&& !MySharedPreference
												.getBoolean("page1")) {
									ll_dot = (LinearLayout) findViewById(R.id.tab_ll_dot);
									ll_dot.setVisibility(View.VISIBLE);
									viewpager.setCurrentItem(0);
									viewpager.setVisibility(View.VISIBLE);
									getPic();
									adp = new MyPagerAdp(pics);
									viewpager.setAdapter(adp);
									MySharedPreference.putBoolean("page1",
											false);
									page1 = true;
								}
							}
						}
						break;
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (MySharedPreference.getBoolean("page2")
						&& MySharedPreference.getBoolean("page1")) {
					MySharedPreference.putBoolean("HELP", false);
				}
			}
		});

		android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
		if (null != manager) {
			manager.beginTransaction()
					.replace(R.id.tab_fragment, mFragments[0]).commit();
		} else {
			MyLog.e(TAG, "TAB_initUI_manager null" + currentIndex);
			this.finish();
		}
		MyLog.v(TAG, "initUi----X");
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		currentImage = arg0; // 获取当前页面索引
		if (flag == 0) {
			if (!localFlag) {
				if (arg0 != 4) {
					dots.get(oldImage).setEnabled(false); // 前一个点设置为白色
					dots.get(currentImage).setEnabled(true); // 当前点设置为黑色
					oldImage = currentImage; // 改变前一个索引
					currentImage = (currentImage) % 4; // 有几张就对几求余
					onNotify(0, currentImage, 0, null);
				}
				if (arg0 == 4) {
					viewpager.setVisibility(View.GONE);
					ll_dot.setVisibility(View.GONE);
				}
			} else {
				if (arg0 != 3) {
					dots.get(oldImage).setEnabled(false); // 前一个点设置为白色
					dots.get(currentImage).setEnabled(true); // 当前点设置为黑色
					oldImage = currentImage; // 改变前一个索引
					currentImage = (currentImage) % 3; // 有几张就对几求余
					onNotify(0, currentImage, 0, null);
				}
				if (arg0 == 3) {
					viewpager.setVisibility(View.GONE);
					ll_dot.setVisibility(View.GONE);
				}
			}
		} else if (flag == 1) {
			if (arg0 == 1) {
				viewpager.setVisibility(View.GONE);
				ll_dot.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 3分钟广播
	 */
	public void startBroadTimer() {
		// 本地 非3G加广播设备
		if (!ConfigUtil.is3G(JVTabActivity.this, false) && localFlag) {
			if (null != broadTimer) {
				broadTimer.cancel();
			}
			broadTimer = new Timer();
			broadTimerTask = new TimerTask() {
				@Override
				public void run() {
					MyLog.e(TAG, "startBroadTimer--E");
					myDeviceList = CacheUtil.getDevList();
					PlayUtil.deleteDevIp(myDeviceList);
					PlayUtil.broadCast(JVTabActivity.this);
					MyLog.e(TAG, "startBroadTimer--X");
				}
			};
			broadTimer.schedule(broadTimerTask, 3 * 60 * 1000, 3 * 60 * 1000);
		}
	}

	public void stopBroadTimer() {
		if (null != broadTimer) {
			MyLog.e("注销停止broadTimer", "stop--broadTimer");
			broadTimer.cancel();
			broadTimer = null;
		}
		if (null != broadTimerTask) {
			MyLog.e("注销停止broadTimerTask", "stop--broadTimerTask");
			broadTimerTask.cancel();
			broadTimerTask = null;
		}
	}

}
