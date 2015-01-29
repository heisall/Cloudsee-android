package com.jovision.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.PeripheralManageAdapter;
import com.jovision.commons.MyLog;

public class AddThirdDeviceMenuFragment extends Fragment {
	private View rootView;// 缓存Fragment view
	private GridView manageGridView;
	private PeripheralManageAdapter manageAdapter;
	DisplayMetrics disMetrics;
	private MyHandler myHandler;

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
			rootView = inflater.inflate(
					R.layout.new_add_thirddev_menu_fragment, container, false);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		myHandler = new MyHandler();
		manageGridView = (GridView) rootView
				.findViewById(R.id.third_alarm_gridview);
		disMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(disMetrics);
		manageAdapter = new PeripheralManageAdapter(this);
		manageAdapter.SetData(disMetrics.widthPixels);
		manageGridView.setAdapter(manageAdapter);
		manageAdapter.notifyDataSetChanged();
		// doorBtn = (Button) rootView.findViewById(R.id.add_door_btn);
		// doorBtn.setOnClickListener(this);
		// braceletBtn = (Button) rootView.findViewById(R.id.add_bracelet_btn);
		// braceletBtn.setOnClickListener(this);
		// telecontrolBtn = (Button) rootView
		// .findViewById(R.id.add_telecontrol_btn);
		// telecontrolBtn.setOnClickListener(this);

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

	class MyHandler extends Handler {
		@Override
		public void dispatchMessage(Message msg) {
			MyLog.e("PERIP", "what:" + msg.what + ", arg1:" + msg.arg1);
			switch (msg.what) {
			case Consts.WHAT_PERI_ITEM_CLICK: {
				// arg1:外设功能编号从1开始
				// 1:门磁设备 2:手环设备 3:遥控 4:烟感 5:幕帘 6:红外探测器 7:燃气泄露
				mListener.OnDeviceClassSelected(msg.arg1);
			}
				break;
			default:
				break;
			}
		}
	}

	public void MyOnNotify(int what, int arg1, int arg2, Object obj) {
		Message msg = myHandler.obtainMessage(what, arg1, arg2, obj);
		myHandler.sendMessage(msg);
	}
}
