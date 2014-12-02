package com.jovision.activities;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

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
		public void OnFuncEnabled(int func_index, int enabled);
	}

	private OnFuncEnabledListener mListener;
	private ImageView img_func_swalert;
	private ImageView img_func_swmotion;
	private int func_alert_enabled = -1;
	private int func_motion_enabled = -1;
	private String strParam = "";
	private String alarmTime0 = "";
	private String startTime = "", endTime = "";
	private String startHour = "", startMin = "";
	private String endHour = "", endMin = "";

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
		Bundle data = getArguments();// 获得从activity中传递过来的值
		strParam = data.getString("KEY_PARAM");

		try {
			JSONObject paramObject = new JSONObject(strParam);
			func_alert_enabled = paramObject.optInt("bAlarmEnable", -1);
			func_motion_enabled = paramObject.optInt("bMDEnable", -1);
			alarmTime0 = paramObject.optString("alarmTime0", "");
			Pattern pattern = Pattern.compile("-");
			String[] strs = pattern.split("alarmTime0");
			for (int i = 0; i < strs.length; i++) {
				System.out.println(strs[i]);
			}
			if (strs.length == 2) {
				startTime = strs[0];
				endTime = strs[1];
				Pattern pattern_s = Pattern.compile(":");
				String[] strs_s = pattern.split("startTime");
				for (int i = 0; i < strs_s.length; i++) {
					System.out.println(strs_s[i]);
				}
				startHour = strs_s[0];
				startMin = strs_s[1];

				Pattern pattern_e = Pattern.compile(":");
				String[] strs_e = pattern.split("endTime");
				for (int i = 0; i < strs_e.length; i++) {
					System.out.println(strs_e[i]);
				}
				endHour = strs_e[0];
				endMin = strs_e[1];
			} else {
				startTime = "";
			}

			switch (func_alert_enabled) {
			case 0:

				break;
			case 1:
				break;
			case -1:
				break;
			default:
				break;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			if (func_alert_enabled == 1) {
				// 打开--->关闭
				mListener.OnFuncEnabled(1, 0);
			} else if (func_alert_enabled == 0) {
				// 关闭--->打开
				mListener.OnFuncEnabled(1, 1);
			} else {
				// 隐藏
			}
			break;
		case R.id.function_switch_21:
			if (func_motion_enabled == 1) {
				// 打开--->关闭
				mListener.OnFuncEnabled(2, 0);
			} else if (func_motion_enabled == 0) {
				// 关闭--->打开
				mListener.OnFuncEnabled(2, 1);
			} else {
				// 隐藏
			}
			break;
		default:
			break;
		}
	}
}
