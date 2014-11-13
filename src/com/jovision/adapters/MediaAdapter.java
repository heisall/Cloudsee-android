package com.jovision.adapters;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.BaseActivity;
import com.jovision.utils.BitmapCache;

public class MediaAdapter extends BaseAdapter {

	public File[] fileArray;
	public Context mContext = null;
	public LayoutInflater inflater;
	private String media;// 区分图片还是视频
	private boolean loadImg = true;

	public MediaAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(String media, File[] fileArray, boolean loadImg) {
		this.fileArray = fileArray;
		this.media = media;
		this.loadImg = loadImg;
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

			fileHolder.playImg = (ImageView) convertView
					.findViewById(R.id.playimg);
			fileHolder.fileName = (TextView) convertView
					.findViewById(R.id.filename);
			convertView.setTag(fileHolder);
		} else {
			fileHolder = (FileHolder) convertView.getTag();
		}

		if ("image".equalsIgnoreCase(media)) {
			fileHolder.playImg.setVisibility(View.GONE);
		} else if ("video".equalsIgnoreCase(media)) {
			fileHolder.playImg.setVisibility(View.VISIBLE);
		}

		int width = ((BaseActivity) mContext).disMetrics.widthPixels / 3;
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				width, width);
		fileHolder.fileImageView.setLayoutParams(rllp);
		Bitmap bmp = null;
		if (loadImg) {
			bmp = BitmapCache.getInstance().getBitmap(
					fileArray[position].getAbsolutePath(), media);
		} else {
			bmp = BitmapCache.getInstance().getCacheBitmap(
					fileArray[position].getAbsolutePath());
		}

		if (null != bmp) {
			fileHolder.fileImageView.setImageBitmap(bmp);
		}
		fileHolder.fileName.setText(fileArray[position].getName());
		return convertView;
	}

	class FileHolder {
		ImageView fileImageView;
		ImageView playImg;
		TextView fileName;
	}
}
