package com.jovision.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jovetech.CloudSee.temp.R;

public class DeviceSettingsMainFragment extends Fragment implements
		OnClickListener {

	private View rootView;// 缓存Fragment view

	public interface OnFuncEnabledListener {
		public void OnFuncEnabled(int func_index, boolean benabled);
	}

	private OnFuncEnabledListener mListener;
	private ImageView img_func_swalert;
	private ImageView img_func_swmotion;
	private boolean func_alert_enabled = false;
	private boolean func_motion_enabled = false;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFuncEnabledListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnFuncEnabledListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.dev_settings_main_fragment,
					container, false);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		img_func_swalert = (ImageView) rootView
				.findViewById(R.id.function_switch_11);
		img_func_swmotion = (ImageView) rootView
				.findViewById(R.id.function_switch_21);

		img_func_swalert.setOnClickListener(this);
		img_func_swmotion.setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.function_switch_11:
			if (func_alert_enabled) {
				// 打开--->关闭
				mListener.OnFuncEnabled(1, !func_alert_enabled);
			} else {
				// 关闭--->打开
				mListener.OnFuncEnabled(1, !func_alert_enabled);
			}
			break;
		case R.id.function_switch_21:
			if (func_motion_enabled) {
				// 打开--->关闭
				mListener.OnFuncEnabled(1, !func_motion_enabled);
			} else {
				// 关闭--->打开
				mListener.OnFuncEnabled(1, !func_motion_enabled);
			}
			break;
		default:
			break;
		}
	}
}
