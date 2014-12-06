package com.jovision.adapters;

import java.io.File;
import java.util.ArrayList;

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
import com.jovision.bean.Filebean;
import com.jovision.utils.BitmapCache;

public class MediaAdapter extends BaseAdapter {

	public File[] fileArray;
	public ArrayList<Filebean> dataList;
	public Context mContext = null;
	public LayoutInflater inflater;
	private String media;// 区分图片还是视频
	private boolean loadImg = true;
	private boolean isdelect;

	public MediaAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(ArrayList<Filebean> dataList, String media,
			File[] fileArray, boolean loadImg, boolean isdelect) {
		this.dataList = dataList;
		this.fileArray = fileArray;
		this.media = media;
		this.loadImg = loadImg;
		this.isdelect = isdelect;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (null != fileArray) {
			count = fileArray.length;
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		return fileArray[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		FileHolder fileHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.media_item, null);
			fileHolder = new FileHolder();
			fileHolder.fileselect = (ImageView) convertView
					.findViewById(R.id.fileselect);
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
		try {
			if ("image".equalsIgnoreCase(media)) {
				fileHolder.playImg.setVisibility(View.GONE);
			} else if ("video".equalsIgnoreCase(media)) {
				fileHolder.playImg.setVisibility(View.VISIBLE);
			}
			if (!isdelect) {
				if (dataList.get(position).isSelect()) {
					fileHolder.fileselect.setVisibility(View.VISIBLE);
				} else {
					fileHolder.fileselect.setVisibility(View.GONE);
				}
			}
			int width = ((BaseActivity) mContext).disMetrics.widthPixels / 3;
			RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
					width, width - 40);
			fileHolder.fileImageView.setLayoutParams(rllp);
			Bitmap bmp = null;
			if (loadImg) {
				bmp = BitmapCache.getInstance().getBitmap(
						fileArray[position].getAbsolutePath(), media, "");
			} else {
				bmp = BitmapCache.getInstance().getCacheBitmap(
						fileArray[position].getAbsolutePath());
			}
			if (null != bmp) {
				fileHolder.fileImageView.setImageBitmap(bmp);
			}
			fileHolder.fileName.setText(fileArray[position].getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	class FileHolder {
		ImageView fileImageView;
		ImageView playImg;
		TextView fileName;
		ImageView fileselect;
	}
}
