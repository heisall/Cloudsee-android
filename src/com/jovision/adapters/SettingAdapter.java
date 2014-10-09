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
import com.jovision.bean.SettingBean;

public class SettingAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private ArrayList<SettingBean> dataList;

	private Activity activity;

	public SettingAdapter(Activity activity, ArrayList<SettingBean> dataList) {
		this.activity = activity;
		this.dataList = dataList;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = inflater.inflate(R.layout.setting_item, null);
			Holder = new DeviceHolder();

			Holder.setting_img = (ImageView) convertView
					.findViewById(R.id.setting_item_img);

			Holder.setting_name = (TextView) convertView
					.findViewById(R.id.setting_item_text);

			convertView.setTag(Holder);
		} else {
			Holder = (DeviceHolder) convertView.getTag();
		}
		Holder.setting_name.setText(dataList.get(position).getSetting_name());
		if (dataList.get(position).getIsclick()) {
			Holder.setting_img.setImageResource(R.drawable.setting_selector_2);
		} else {
			Holder.setting_img.setImageResource(R.drawable.setting_selector_1);
		}
		return convertView;
	}

	class DeviceHolder {
		private ImageView setting_img;
		private TextView setting_name;
	}

}
