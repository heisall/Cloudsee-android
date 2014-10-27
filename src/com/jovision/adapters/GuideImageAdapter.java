package com.jovision.adapters;

import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GuideImageAdapter extends BaseAdapter {
	int[] dataArray = null;
	Context mContext = null;
	public LayoutInflater inflater;

	public GuideImageAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(int[] array) {
		dataArray = array;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataArray.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return dataArray[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.image_item, null);
			viewHolder.guideImageView = (ImageView) convertView
					.findViewById(R.id.img);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		try {
			viewHolder.guideImageView.setBackgroundDrawable(mContext
					.getResources().getDrawable(dataArray[position]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	class ViewHolder {
		ImageView guideImageView;
	}

}
