package com.jovision.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jovetech.CloudSee.temp.R;

/**
 * 更多
 */
public class JVMoreFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_more, container, false);
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
