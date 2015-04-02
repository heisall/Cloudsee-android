package com.jovision.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseFragment;

public class PopWindowAdapter extends BaseAdapter {

	private LayoutInflater inflater;

	private BaseFragment fragment;
	private String[] array;
	private int[] drawablearray;

	public PopWindowAdapter(BaseFragment fragments) {
		fragment = fragments;
		inflater = (LayoutInflater) fragment.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(String[] arrays, int[] drawablearrays) {
		array = arrays;
		drawablearray = drawablearrays;
	}

	@Override
	public int getCount() {
		return array.length;
	}

	@Override
	public Object getItem(int arg0) {
		return array[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		DeviceHolder Holder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.poplist_item, null);
			Holder = new DeviceHolder();

			Holder.listitem_img = (ImageView) convertView
					.findViewById(R.id.listitem_img);

			Holder.listitem_name = (TextView) convertView
					.findViewById(R.id.listitem_name);

			convertView.setTag(Holder);
		} else {
			Holder = (DeviceHolder) convertView.getTag();
		}

		if (position < drawablearray.length) {
			Holder.listitem_name.setText(array[position]);
			Holder.listitem_img.setImageResource(drawablearray[position]);
		}
		return convertView;
	}

	class DeviceHolder {
		private ImageView listitem_img;
		private TextView listitem_name;
	}

}
