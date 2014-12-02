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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.DeviceSettingsActivity.OnMainListener;
import com.jovision.commons.JVNetConst;

public class DeviceSettingsMainFragment extends Fragment implements
		OnClickListener, OnMainListener {

	private View rootView;// 缓存Fragment view

	public interface OnFuncEnabledListener {
		public void OnFuncEnabled(int func_index, int enabled);
	}

	private OnFuncEnabledListener mListener;
	private Button func_swalert;
	private Button func_swmotion;
	private int func_alert_enabled = -1;
	private int func_motion_enabled = -1;
	private String strParam = "";
	private String alarmTime0 = "";
	private String startTime = "", endTime = "";
	private String startHour = "", startMin = "";
	private String endHour = "", endMin = "";
	private RelativeLayout functionlayout1, functionlayout2, functionlayout3;
	private RelativeLayout functiontips1, functiontips2, functiontips3;
	private TextView alarmTime0TextView;

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
		func_swalert = (Button) rootView.findViewById(R.id.function_switch_11);
		func_swmotion = (Button) rootView.findViewById(R.id.function_switch_21);
		functionlayout1 = (RelativeLayout) rootView
				.findViewById(R.id.funclayout1);
		functionlayout2 = (RelativeLayout) rootView
				.findViewById(R.id.funclayout2);
		functionlayout3 = (RelativeLayout) rootView
				.findViewById(R.id.funclayout3);

		functiontips1 = (RelativeLayout) rootView.findViewById(R.id.rl_tips_01);
		functiontips2 = (RelativeLayout) rootView.findViewById(R.id.rl_tips_02);
		functiontips3 = (RelativeLayout) rootView.findViewById(R.id.rl_tips_03);

		alarmTime0TextView = (TextView) rootView
				.findViewById(R.id.funtion_titile_32);

		func_swalert.setOnClickListener(this);
		func_swmotion.setOnClickListener(this);
		functionlayout3.setOnClickListener(this);

		Bundle data = getArguments();// 获得从activity中传递过来的值
		strParam = data.getString("KEY_PARAM");

		try {
			JSONObject paramObject = new JSONObject(strParam);
			func_alert_enabled = paramObject.optInt("bAlarmEnable", -1);
			func_motion_enabled = paramObject.optInt("bMDEnable", -1);
			alarmTime0 = paramObject.optString("alarmTime0", "");
			Pattern pattern = Pattern.compile("-");
			String[] strs = pattern.split(alarmTime0);
			for (int i = 0; i < strs.length; i++) {
				System.out.println(strs[i]);
			}
			if (strs.length == 2) {
				startTime = strs[0];
				endTime = strs[1];
				Pattern pattern_s = Pattern.compile(":");
				String[] strs_s = pattern_s.split(startTime);
				for (int i = 0; i < strs_s.length; i++) {
					System.out.println(strs_s[i]);
				}
				startHour = strs_s[0];
				startMin = strs_s[1];

				Pattern pattern_e = Pattern.compile(":");
				String[] strs_e = pattern_e.split(endTime);
				for (int i = 0; i < strs_e.length; i++) {
					System.out.println(strs_e[i]);
				}
				endHour = strs_e[0];
				endMin = strs_e[1];

				startTime = String.format("%s:%s", startHour, startMin);
				endTime = String.format("%s:%s", endHour, endMin);

				alarmTime0TextView.setText(startTime + " - " + endTime);
			} else {
				startTime = "";
			}

			switch (func_alert_enabled) {
			case 0:
				func_swalert
						.setBackgroundResource(R.drawable.morefragment_normal_icon);
				break;
			case 1:
				func_swalert
						.setBackgroundResource(R.drawable.morefragment_selector_icon);
				break;
			case -1:
				functionlayout1.setVisibility(View.GONE);
				functiontips1.setVisibility(View.GONE);
				break;
			default:
				break;
			}

			switch (func_motion_enabled) {
			case 0:
				func_swmotion
						.setBackgroundResource(R.drawable.morefragment_normal_icon);
				break;
			case 1:
				func_swmotion
						.setBackgroundResource(R.drawable.morefragment_selector_icon);
				break;
			case -1:
				functionlayout2.setVisibility(View.GONE);
				functiontips2.setVisibility(View.GONE);
				break;
			default:
				break;
			}

			if (alarmTime0.equals("") || startTime.equals("")) {
				functionlayout3.setVisibility(View.GONE);
				functiontips3.setVisibility(View.GONE);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	@Override
	public void onMainAction(int packet_type, int packet_subtype, int ex_type) {
		// TODO Auto-generated method stub
		switch (packet_type) {
		case JVNetConst.RC_EXTEND: {
			switch (packet_subtype) {
			case JVNetConst.RC_EX_MD:
				if (ex_type == JVNetConst.EX_MD_SUBMIT) {
					if (func_motion_enabled == 1) {
						// 打开--->关闭
						func_motion_enabled = 0;
						func_swmotion
								.setBackgroundResource(R.drawable.morefragment_normal_icon);
					} else if (func_motion_enabled == 0) {
						// 关闭--->打开
						func_motion_enabled = 1;
						func_swmotion
								.setBackgroundResource(R.drawable.morefragment_selector_icon);
					} else {
						// 隐藏
					}
				}
				break;
			case JVNetConst.RC_EX_ALARM:
				if (ex_type == JVNetConst.EX_ALARM_SUBMIT) {
					if (func_alert_enabled == 1) {
						// 打开--->关闭
						func_alert_enabled = 0;
						func_swalert
								.setBackgroundResource(R.drawable.morefragment_normal_icon);
					} else if (func_alert_enabled == 0) {
						// 关闭--->打开
						func_alert_enabled = 1;
						func_swalert
								.setBackgroundResource(R.drawable.morefragment_selector_icon);
					} else {
						// 隐藏
					}
				}
				break;
			default:
				break;
			}
		}
			break;

		default:
			break;
		}
	}
}
