package com.jovision.activities;

import javax.security.auth.PrivateCredentialPermission;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.DeviceSettingsActivity.OnMainListener;
import com.jovision.utils.DateTimePickDialogUtil;

public class DevSettingsAlarmTimeFragment extends Fragment implements
		OnClickListener, OnMainListener {
	private View rootView;// 缓存Fragment view
	private OnAlarmTimeActionListener mListener;
	private String startTime, endTime;
	private TextView tv_stime, tv_etime;
	private RelativeLayout rl_start, rl_end;
	private Button btn_all_day, btn_save;
	protected Toast toast;

	public interface OnAlarmTimeActionListener {
		public void OnAlarmTimeSaved(String startTime, String endTime);

		public void OnAlarmTimeSavedResult(int ret);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnAlarmTimeActionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnAlarmTimeActionListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(
					R.layout.dev_settings_alarmtime_fragment, container, false);
		}

		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		Bundle data = getArguments();// 获得从activity中传递过来的值
		startTime = data.getString("START_TIME");
		endTime = data.getString("END_TIME");
		InitViews();
		return rootView;
	}

	private void InitViews() {
		tv_stime = (TextView) rootView.findViewById(R.id.tv_start_time);
		tv_etime = (TextView) rootView.findViewById(R.id.tv_end_time);
		rl_start = (RelativeLayout) rootView.findViewById(R.id.rl_start_time);
		rl_end = (RelativeLayout) rootView.findViewById(R.id.rl_end_time);
		btn_all_day = (Button) rootView.findViewById(R.id.btn_all_day);
		btn_save = (Button) rootView.findViewById(R.id.btn_save);

		tv_stime.setText(startTime);
		tv_etime.setText(endTime);
		rl_start.setOnClickListener(this);
		rl_end.setOnClickListener(this);
		btn_all_day.setOnClickListener(this);
		btn_save.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_start_time:
			DateTimePickDialogUtil dateTimePicKDialogS = new DateTimePickDialogUtil(
					getActivity(), null, startTime, 0);
			dateTimePicKDialogS.dateTimePicKDialog(tv_stime);
			break;
		case R.id.rl_end_time:
			DateTimePickDialogUtil dateTimePicKDialogE = new DateTimePickDialogUtil(
					getActivity(), null, endTime, 1);
			dateTimePicKDialogE.dateTimePicKDialog(tv_etime);
			break;
		case R.id.btn_all_day:
			tv_stime.setText("00:00");
			tv_etime.setText("23:59");
			break;
		case R.id.btn_save:
			startTime = tv_stime.getText().toString();
			endTime = tv_etime.getText().toString();
			timeComputruer(startTime, endTime);
			break;
		default:
			break;
		}
	}
	private void timeComputruer(String startTime,String endTime) {
		String start[] = startTime.split(":");
		String end[] = endTime.split(":");
		for (int i = 0; i < end.length; i++) {
			if ((Integer.valueOf(start[0]))>(Integer.valueOf(end[0]))){
				showTextToast(getActivity(), R.string.str_computre_time1);
			}else if ((Integer.valueOf(start[0])) == (Integer.valueOf(end[0]))) {
				if ((Integer.valueOf(start[1]))>(Integer.valueOf(end[1]))) {
					showTextToast(getActivity(), R.string.str_computre_time1);
				}else if((Integer.valueOf(start[1]))<(Integer.valueOf(end[1]))){
					mListener.OnAlarmTimeSaved(startTime, endTime);
				}else {
					showTextToast(getActivity(), R.string.str_computre_time3);
				}
			}else {
				mListener.OnAlarmTimeSaved(startTime, endTime);
			}
		}
	}
	@Override
	public void onMainAction(int packet_type, int packet_subtype, int ex_type,
			int destFlag) {
		// TODO Auto-generated method stub
		mListener.OnAlarmTimeSavedResult(0);
	}
	/**
	 * 弹系统消息
	 * 
	 * @param context
	 * @param id
	 */
	public void showTextToast(Context context, int id) {
		String msg = context.getResources().getString(id);
		if (toast == null) {
			toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}
}
