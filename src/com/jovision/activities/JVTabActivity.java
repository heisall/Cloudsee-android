package com.jovision.activities;

import android.support.v4.app.Fragment;
import android.view.View;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.activities.JVFragmentIndicator.OnIndicateListener;
import com.jovision.commons.MyLog;

public class JVTabActivity extends BaseActivity {

	private static final String TAG = "JVTabActivity";
	protected Fragment[] mFragments;
	protected int[] frageIDArray = { R.id.mydevice_fragement,
			R.id.info_fragement, R.id.demo_fragement,
			R.id.devicemanage_fragement, R.id.more_fragement };

	private void setFragmentIndicator(int whichIsDefault) {
		int length = frageIDArray.length;
		mFragments = new Fragment[length];
		for (int i = 0; i < length; i++) {
			mFragments[i] = getSupportFragmentManager().findFragmentById(
					frageIDArray[i]);
		}

		for (int i = 0; i < length; i++) {
			getSupportFragmentManager().beginTransaction().hide(mFragments[i])
					.commit();
		}
		getSupportFragmentManager().beginTransaction()
				.show(mFragments[whichIsDefault]).commit();
		tabListener = (OnTabListener) mFragments[whichIsDefault];

		JVFragmentIndicator mIndicator = (JVFragmentIndicator) findViewById(R.id.indicator);
		JVFragmentIndicator.setIndicator(whichIsDefault);
		mIndicator.setOnIndicateListener(new OnIndicateListener() {
			@Override
			public void onIndicate(View v, int which) {
				try {
					int length = frageIDArray.length;
					for (int i = 0; i < length; i++) {
						getSupportFragmentManager().beginTransaction()
								.hide(mFragments[i]).commit();
					}
					getSupportFragmentManager().beginTransaction()
							.show(mFragments[which]).commit();
					tabListener = (OnTabListener) mFragments[which];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		tabListener.onTabAction(Consts.TAB_ONSTART, 0, 0, 0);
		MyLog.v("TAG", "TAB_ONSTART");
	}

	@Override
	protected void onResume() {
		super.onResume();
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
		tabListener.onTabAction(what, arg1, arg2, obj);
		MyLog.v("TAG", "onHandler");
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
		MyLog.v("TAG", "onNotify");
	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void freeMe() {
		// TODO Auto-generated method stub

	}

	private OnTabListener tabListener;

	// @Override
	// public void onAttachFragment(Fragment fragment) {
	// // tabListener = (OnTabListener)mFragments[0];
	// super.onAttachFragment(fragment);
	// }

	// 接口
	public interface OnTabListener {

		public void onTabAction(int what, int arg1, int arg2, Object obj);

	}

	// @Override
	// public void onFragmentAction(int what, int arg1, int arg2, Object obj) {
	// // TODO Auto-generated method stub
	//
	// }

}
