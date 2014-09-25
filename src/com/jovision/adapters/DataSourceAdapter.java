package com.jovision.adapters;

import java.util.ArrayList;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jovision.activities.JVPlayActivity;
import com.jovision.bean.Device;
import com.jovision.commons.JVConst;
import com.jovision.views.MyGridView;

public class DataSourceAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<Device> deviceList = new ArrayList<Device>();
	private int deviceIndex = -1;
	private PointListAdapter plAdapter;

	private int index1 = -1;
	private int index2 = -1;

	public DataSourceAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Device> list, int arg0, int arg1) {
		deviceList = list;
		index1 = arg0;
		index2 = arg1;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return deviceList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return deviceList.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DeviceHolder deviceHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.datasource_item, null);
			deviceHolder = new DeviceHolder();
			deviceHolder.connPointName = (TextView) convertView
					.findViewById(R.id.devicenum);
			deviceHolder.pointGridView = (MyGridView) convertView
					.findViewById(R.id.datasourcegridview);
			convertView.setTag(deviceHolder);
		} else {
			deviceHolder = (DeviceHolder) convertView.getTag();
		}

		deviceHolder.connPointName.setTextColor(mContext.getResources()
				.getColor(R.color.black));
		deviceHolder.connPointName.setText(deviceList.get(position).deviceNum);

		if (null != deviceList.get(position).pointList
				&& 0 != deviceList.get(position).pointList.size()) {
			plAdapter = new PointListAdapter(mContext);
			plAdapter.setData(deviceList.get(position).pointList, Color.BLACK);
			deviceHolder.pointGridView.setAdapter(plAdapter);

			if (index1 == position) {
				deviceHolder.pointGridView.setSelection(index1);
				plAdapter.currentPointIndex = index2;
			}

			MyOnItemClickListener myOnItemClickListener = new MyOnItemClickListener(
					position);
			deviceHolder.pointGridView
					.setOnItemClickListener(myOnItemClickListener);
		}

		return convertView;
	}

	class MyOnItemClickListener implements OnItemClickListener {
		int deIndex = -1;

		public MyOnItemClickListener(int arg) {
			deIndex = arg;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			plAdapter.currentPointIndex = arg2;
			plAdapter.notifyDataSetChanged();

			Intent playIntent = new Intent();
			playIntent.setClass(mContext, JVPlayActivity.class);
			playIntent.putExtra("DeviceIndex", deIndex);
			playIntent.putExtra("PointIndex", arg2);
			((Activity) mContext).setResult(JVConst.DATA_SOURCE_RESULT,
					playIntent);
			((Activity) mContext).finish();
		}

	}

	class DeviceHolder {
		TextView connPointName;
		MyGridView pointGridView;
	}

}
