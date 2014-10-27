package com.jovision.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.jovetech.CloudSee.temp.R;

public class AddThirdDeviceMenuFragment extends Fragment implements
		OnClickListener {
	private View rootView;// 缓存Fragment view
	private Button doorBtn; // 门磁
	private Button braceletBtn; // 手环
	private Button telecontrolBtn; // 手环

	public interface OnDeviceClassSelectedListener {
		public void OnDeviceClassSelected(int index);
	}

	private OnDeviceClassSelectedListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnDeviceClassSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnDeviceClassSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.add_thirddev_menu_fragment,
					container, false);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		doorBtn = (Button) rootView.findViewById(R.id.add_door_btn);
		doorBtn.setOnClickListener(this);
		braceletBtn = (Button) rootView.findViewById(R.id.add_bracelet_btn);
		braceletBtn.setOnClickListener(this);
		telecontrolBtn = (Button) rootView
				.findViewById(R.id.add_telecontrol_btn);
		telecontrolBtn.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_door_btn:// 门磁设备
			mListener.OnDeviceClassSelected(1);
			// 实际应该起个线程，然后开始播放动画
			// TODO
			break;
		case R.id.add_bracelet_btn:// 手环设备
			mListener.OnDeviceClassSelected(2);

			// 实际应该起个线程，然后开始播放动画
			// TODO
			break;
		case R.id.add_telecontrol_btn:// 遥控
			mListener.OnDeviceClassSelected(3);
			break;
		default:
			break;
		}
	}
}
