package com.jovision.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseFragment;
import com.jovision.bean.Device;

public class ManageListAdapter extends BaseAdapter {

	private BaseFragment mfragment;
	private Activity activity;
	private LayoutInflater inflater;

	private ArrayList<Device> dataList;

	public ManageListAdapter(BaseFragment fragment) {
		mfragment = fragment;
		inflater = (LayoutInflater) fragment.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	public ManageListAdapter(Activity activitys) {
		activity = activitys;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Device> dataLis) {
		dataList = dataLis;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return dataList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		DeviceHolder Holder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.manage_list_item, null);
			Holder = new DeviceHolder();

			Holder.listitem_img = (ImageView) convertView
					.findViewById(R.id.listitem_img);

			Holder.listitem_name = (TextView) convertView
					.findViewById(R.id.listitem_name);

			convertView.setTag(Holder);
		} else {
			Holder = (DeviceHolder) convertView.getTag();
		}
		Holder.listitem_name.setText(dataList.get(position).getFullNo());
		return convertView;
	}

	class DeviceHolder {
		private ImageView listitem_img;
		private TextView listitem_name;
	}

}
