package com.jovision.activities;

import android.view.View;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.IHandlerLikeNotify;
import com.jovision.activities.JVFragmentIndicator.OnIndicateListener;
import com.jovision.commons.MyLog;

public class JVTabActivity extends BaseActivity {

	private static final String TAG = "JVTabActivity";

	private int currentIndex = 0;// 当前页卡index

	private BaseFragment mFragments[] = new BaseFragment[5];

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

		mFragments[0] = new JVMyDeviceFragment();
		mFragments[1] = new JVInfoFragment();
		mFragments[2] = new JVDemoFragment();
		mFragments[3] = new JVDeviceManageFragment();
		mFragments[4] = new JVMoreFragment();

		JVFragmentIndicator mIndicator = (JVFragmentIndicator) findViewById(R.id.indicator);
		JVFragmentIndicator.setIndicator(0);

		mIndicator.setOnIndicateListener(new OnIndicateListener() {
			@Override
			public void onIndicate(View v, int which) {
				try {
					currentIndex = which;
					getSupportFragmentManager().beginTransaction()
							.replace(R.id.tab_fragment, mFragments[which])
							.commit();
					// tabListener = (OnTabListener) mFragments[which];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.tab_fragment, mFragments[0]).commit();
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

}
