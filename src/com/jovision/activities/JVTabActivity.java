package com.jovision.activities;

import java.util.Timer;

import android.app.NotificationManager;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.IHandlerLikeNotify;
import com.jovision.activities.JVFragmentIndicator.OnIndicateListener;
import com.jovision.commons.CheckUpdateTask;
import com.jovision.commons.MyLog;
import com.jovision.views.AlarmDialog;

public class JVTabActivity extends ShakeActivity {

	private static final String TAG = "JVTabActivity";

	public static final int TAB_BACK = 0x20;

	private int currentIndex = 0;// 当前页卡index

	protected NotificationManager mNotifyer;
	protected int timer = 16;
	protected Timer offlineTimer = new Timer();
	private BaseFragment mFragments[] = new BaseFragment[5];

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
		MyLog.v(TAG, "TAB_onResume" + currentIndex);
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
		case Consts.PUSH_MESSAGE:
			//弹出对话框
			AlarmDialog.getInstance(this).Show(obj.toString()); 
			break;

		default:
			break;
		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {

		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));//所有界面弹出报警
		// TODO 增加过滤
		switch (what) {
		// case Consts.CALL_LAN_SEARCH:{
		// BaseFragment currentFrag = mFragments[currentIndex];
		// if (null != currentFrag) {
		// ((IHandlerLikeNotify) currentFrag).onNotify(what, arg1, arg2,
		// obj);
		// }
		// break;
		// }
		default:
			BaseFragment currentFrag = mFragments[currentIndex];
			if (null != currentFrag) {
				((IHandlerLikeNotify) currentFrag).onNotify(what, arg1, arg2,
						obj);
			}
			break;
		}
		MyLog.v(TAG, "onNotify");
	}

	@Override
	protected void initSettings() {
		Intent intent = getIntent();
		currentIndex = intent.getIntExtra("tabIndex", 0);
	}

	@Override
	protected void initUi() {
		super.initUi();
		setContentView(R.layout.tab_layout);
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
		// mFragments[2] = new JVDemoFragment();
		mFragments[2] = new JVDeviceManageFragment();
		mFragments[3] = new JVMoreFragment();

		mIndicator.setOnIndicateListener(new OnIndicateListener() {
			@Override
			public void onIndicate(View v, int which) {
				try {
					currentIndex = which;
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.tab_fragment, mFragments[which])
							.commit();
				} catch (Exception e) {
					e.printStackTrace();
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

}
