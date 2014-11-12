package com.jovision.adapters;

import java.io.File;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseActivity;

public class MediaAdapter extends BaseAdapter {

	public File[] fileArray;
	public Context mContext = null;
	public LayoutInflater inflater;
	private String media;// 区分图片还是视频

	public MediaAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(String media, File[] fileArray) {
		this.fileArray = fileArray;
		this.media = media;
	}

	@Override
	public int getCount() {
		return fileArray.length;
	}

	@Override
	public Object getItem(int arg0) {
		return fileArray[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		FileHolder fileHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.media_item, null);
			fileHolder = new FileHolder();
			fileHolder.fileImageView = (ImageView) convertView
					.findViewById(R.id.fileimage);
			fileHolder.fileName = (TextView) convertView
					.findViewById(R.id.filename);
			convertView.setTag(fileHolder);
		} else {
			fileHolder = (FileHolder) convertView.getTag();
		}
		int width = ((BaseActivity) mContext).disMetrics.widthPixels / 4;
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				width, width);
		fileHolder.fileImageView.setLayoutParams(rllp);

		fileHolder.fileName.setText(fileArray[position].getName());
		return convertView;
	}

	class FileHolder {
		ImageView fileImageView;
		TextView fileName;
	}
}
