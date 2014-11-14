package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;

public class MediaSelectorAdapter extends BaseAdapter {
	private int[] imgSrcArray = { R.drawable.media_image,
			R.drawable.media_video, R.drawable.media_other };
	private Context mContext;
	private LayoutInflater inflater;
	private ArrayList<String> mediaList = new ArrayList<String>();
	public int selectIndex = -1;

	public MediaSelectorAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<String> list) {
		mediaList = list;
	}

	@Override
	public int getCount() {
		return mediaList.size();
	}

	@Override
	public Object getItem(int position) {
		return mediaList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.mediaselecter_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mediaLayout = (RelativeLayout) convertView
					.findViewById(R.id.mediaitemlayout);
			viewHolder.mediaImageView = (ImageView) convertView
					.findViewById(R.id.media_image);
			viewHolder.mediaName = (TextView) convertView
					.findViewById(R.id.media_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mediaImageView.setImageResource(imgSrcArray[position]);
		viewHolder.mediaName.setText(mediaList.get(position));
		return convertView;
	}

	class ViewHolder {
		RelativeLayout mediaLayout;
		ImageView mediaImageView;
		TextView mediaName;
	}

}