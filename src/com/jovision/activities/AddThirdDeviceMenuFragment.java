package com.jovision.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.ActionReceiver;
import com.jovision.Consts;
import com.jovision.Jni;
import com.jovision.commons.JVNetConst;

public class AddThirdDeviceMenuFragment extends Fragment implements
		OnClickListener {
	private View rootView;// 缓存Fragment view
	private Button doorBtn; // 门磁
	private Button braceletBtn; // 手环

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
		default:
			break;
		}
	}
}
