package com.jovision.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.IHandlerLikeNotify;
import com.jovision.IHandlerNotify;
import com.jovision.MainApplication;

/**
 * Fragment 基类
 */
public abstract class BaseFragment extends Fragment implements IHandlerNotify,
		IHandlerLikeNotify {
	private final String TAG = "BaseFragment";

	protected FragHandler fragHandler = new FragHandler(this);
	IHandlerNotify fragNotify = this;

	protected View mParent;
	protected BaseActivity mActivity;

	/** topBar */
	protected LinearLayout topBar;
	protected Button leftBtn;
	protected TextView currentMenu;
	protected Button rightBtn;

	protected static class FragHandler extends Handler {

		private BaseFragment fragment;

		public FragHandler(BaseFragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void handleMessage(Message msg) {
			fragment.fragNotify
					.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
			super.handleMessage(msg);
		}

	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = (BaseActivity) getActivity();
		mParent = getView();

		if (null == mActivity
				| false == mActivity instanceof IHandlerLikeNotify) {
			throw new ClassCastException(
					"mActivity must an IHandlerLikeNotify impl");
		}

		topBar = (LinearLayout) mParent.findViewById(R.id.top_bar);
		leftBtn = (Button) mParent.findViewById(R.id.btn_left);
		currentMenu = (TextView) mParent.findViewById(R.id.currentmenu);
		rightBtn = (Button) mParent.findViewById(R.id.btn_right);
		try {
			if (null != leftBtn) {
				leftBtn.setOnClickListener(mOnClickListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_left:
				((BaseActivity) mActivity).openExitDialog();
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		super.onResume();
		((MainApplication) mActivity.getApplication()).setCurrentNotifyer(this);
	}

}
