package com.jovision.activities;

import android.R;
import android.view.View;

import com.jovision.Consts;
import com.jovision.IHandlerLikeNotify;
import com.jovision.activities.JVFragmentIndicator.OnIndicateListener;
import com.jovision.commons.MyLog;

public class JVTabActivity extends BaseActivity {

	private static final String TAG = "JVTabActivity";

	private int currentIndex = 0;// 当前页卡index

	protected BaseFragment[] mFragments;
	protected int[] frageIDArray = { R.id.mydevice_fragement,
			R.id.info_fragement, R.id.demo_fragement,
			R.id.devicemanage_fragement, R.id.more_fragement };

	private void setFragmentIndicator(int whichIsDefault) {
		int length = frageIDArray.length;
		mFragments = new BaseFragment[length];
		for (int i = 0; i < length; i++) {
			mFragments[i] = (BaseFragment) getSupportFragmentManager()
					.findFragmentById(frageIDArray[i]);
		}

		for (int i = 0; i < length; i++) {
			getSupportFragmentManager().beginTransaction().hide(mFragments[i])
					.commit();
		}
		getSupportFragmentManager().beginTransaction()
				.show(mFragments[whichIsDefault]).commit();
		// tabListener = (OnTabListener) mFragments[whichIsDefault];

		JVFragmentIndicator mIndicator = (JVFragmentIndicator) findViewById(R.id.indicator);
		JVFragmentIndicator.setIndicator(whichIsDefault);
		mIndicator.setOnIndicateListener(new OnIndicateListener() {
			@Override
			public void onIndicate(View v, int which) {
				try {
					currentIndex = which;
					int length = frageIDArray.length;
					for (int i = 0; i < length; i++) {
						getSupportFragmentManager().beginTransaction()
								.hide(mFragments[i]).commit();
					}
					getSupportFragmentManager().beginTransaction()
							.show(mFragments[which]).commit();
					// tabListener = (OnTabListener) mFragments[which];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		BaseFragment currentFrag = mFragments[currentIndex];
		if (null != currentFrag) {
			((IHandlerLikeNotify) currentFrag).onNotify(Consts.TAB_ONSTART, 0,
					0, null);
		}
		MyLog.v(TAG, "TAB_onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		openExitDialog();
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		MyLog.v(TAG, "onHandler");
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO 增加过滤
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
		MyLog.v(TAG, "onNotify");
	}

	@Override
	protected void initSettings() {

	}

	@Override
	protected void initUi() {
		Boolean local = Boolean.valueOf(statusHashMap.get(Consts.LOCAL_LOGIN));
		if (local) {
			Consts.DEVICE_LIST = Consts.LOCAL_DEVICE_LIST;
		} else {
			Consts.DEVICE_LIST = Consts.CACHE_DEVICE_LIST;
		}

		setContentView(R.layout.tab_layout);
		setFragmentIndicator(0);
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

}
