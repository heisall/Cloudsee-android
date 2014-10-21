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

	private Activity activity;
	private LayoutInflater inflater;
	private BaseFragment fragment;
	private ArrayList<Device> dataList;

	public ManageListAdapter(Activity activitys) {
		activity = activitys;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public ManageListAdapter(BaseFragment fragments) {
		fragment = fragments;
		inflater = (LayoutInflater) fragment.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
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

			Holder.manage_item_img = (ImageView) convertView
					.findViewById(R.id.manage_item_img);
			convertView.setTag(Holder);
		} else {
			Holder = (DeviceHolder) convertView.getTag();
		}
		if (dataList.get(position).getIsselect()) {
			Holder.manage_item_img.setVisibility(View.VISIBLE);
			Holder.listitem_img
					.setImageResource(R.drawable.devicemanage_selected_icon);
			if (activity==null) {
				Holder.listitem_name.setTextColor(fragment.getActivity().getResources().getColor(R.color.dialogchannaltext));
			}else {
				Holder.listitem_name.setTextColor(activity.getResources().getColor(R.color.dialogchannaltext));
			}
		} else {
			Holder.manage_item_img.setVisibility(View.GONE);
			if (activity==null) {
				Holder.listitem_name.setTextColor(fragment.getActivity().getResources().getColor(R.color.more_fragment_color2));
			}else {
				Holder.listitem_name.setTextColor(activity.getResources().getColor(R.color.more_fragment_color2));
			}
			Holder.listitem_img
					.setImageResource(R.drawable.devicemanage_normal_icon);
		}
		if (2 == dataList.get(position).getIsDevice()) {
			Holder.listitem_name.setText(dataList.get(position).getDoMain());
		} else {
			Holder.listitem_name.setText(dataList.get(position).getFullNo());
		}

		return convertView;
	}

	class DeviceHolder {
		private ImageView listitem_img;
		private TextView listitem_name;
		private ImageView manage_item_img;
	}

}
