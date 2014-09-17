package com.jovision.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jovetech.CloudSee.temp.R;

/**
 * 消息
 */
public class JVInfoFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_info, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mActivity = getActivity();
	}

	private void backHomeFragment() {
		// getFragmentManager().beginTransaction()
		// .hide(JVTabActivity.mFragments[1])
		// .show(JVTabActivity.mFragments[0]).commit();
		JVFragmentIndicator.setIndicator(0);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
		}
	}

	@Override
	public void onTabAction(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

}
