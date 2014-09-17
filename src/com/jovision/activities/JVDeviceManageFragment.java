package com.jovision.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jovetech.CloudSee.temp.R;

/**
 * 设备管理
 */
public class JVDeviceManageFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_devicemanage, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mActivity = getActivity();
	}

	@Override
	public void onTabAction(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}
}
