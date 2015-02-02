package com.jovision.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.IHandlerLikeNotify;

public class AlarmInfoActivity extends BaseActivity {

	private JVInfoFragment alarmInfoFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.alarm_info_layout);
		if (findViewById(R.id.fragment_container) != null) {
			alarmInfoFragment = new JVInfoFragment();
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			ft.add(R.id.fragment_container, alarmInfoFragment)
					.commitAllowingStateLoss();
		}
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub
		switch (what) {
		case Consts.NEW_PUSH_MSG_TAG: {
			// 还有更新数量信息，别忘了，最后再搞
			if (null != alarmInfoFragment) {
				((IHandlerLikeNotify) alarmInfoFragment).onNotify(what, arg1,
						arg2, obj);
			}
		}
			break;
		default:
			if (null != alarmInfoFragment) {
				((IHandlerLikeNotify) alarmInfoFragment).onNotify(what, arg1,
						arg2, obj);
			}
			break;
		}
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
