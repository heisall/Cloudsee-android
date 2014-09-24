package com.jovision.Handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.jovision.Consts;
import com.jovision.activities.BaseActivity;
import com.jovision.adapters.DeviceListAdapter;

public class GetDeviceListHandler extends Handler {
	private Context context;
	private DeviceListAdapter adapter;

	public GetDeviceListHandler(Context con, DeviceListAdapter listAdapter) {
		context = con;
		adapter = listAdapter;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.arg1) {
		case Consts.DEVICE_GETDATA_SUCCESS:
			((BaseActivity) context).dismissDialog();
			break;
		case Consts.DEVICE_NO_DEVICE:
			((BaseActivity) context).dismissDialog();
			break;
		case Consts.DEVICE_GETDATA_FAILED:
			((BaseActivity) context).dismissDialog();
			break;
		}
	}
}
