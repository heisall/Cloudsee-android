package com.jovision.adapters;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.JVImageViewActivity;
import com.jovision.activities.JVVideoActivity;
import com.jovision.views.MyGridView;

public class MediaFolderAdapter extends BaseAdapter {

	public ArrayList<File> folderList = new ArrayList<File>();
	public Context mContext = null;
	public LayoutInflater inflater;
	private String media;// 区分图片还是视频
	private boolean loadImg = true;

	public MediaFolderAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(String media, ArrayList<File> folderList) {
		this.folderList = folderList;
		this.media = media;
	}

	public void setLoadImage(boolean load) {
		loadImg = load;
	}

	@Override
	public int getCount() {
		int count = 0;
		if (null != folderList) {
			count = folderList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int arg0) {
		return folderList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		FolderHolder folderHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.mediafolder_item, null);
			folderHolder = new FolderHolder();
			folderHolder.folderName = (TextView) convertView
					.findViewById(R.id.foldername);
			folderHolder.fileGridView = (MyGridView) convertView
					.findViewById(R.id.filegridview);
			convertView.setTag(folderHolder);
		} else {
			folderHolder = (FolderHolder) convertView.getTag();
		}
		folderHolder.folderName.setText(folderList.get(position).getName());
		final MediaAdapter mediaAdaper = new MediaAdapter(mContext);
		final File[] fileArray = folderList.get(position).listFiles();
		final String folderPath = folderList.get(position).getAbsolutePath();
		mediaAdaper.setData(media, fileArray, loadImg);
		folderHolder.fileGridView.setAdapter(mediaAdaper);

		folderHolder.fileGridView
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						mediaAdaper.setSelect(arg2);
						mediaAdaper.notifyDataSetChanged();
						if ("image".equalsIgnoreCase(media)) {
							Intent imageIntent = new Intent();
							imageIntent.setClass(mContext,
									JVImageViewActivity.class);
							imageIntent.putExtra("FolderPath", folderPath);
							imageIntent.putExtra("FileIndex", arg2);
							mContext.startActivity(imageIntent);
						} else if ("video".equalsIgnoreCase(media)) {
							Intent videoIntent = new Intent();
							videoIntent.setClass(mContext,
									JVVideoActivity.class);
							videoIntent.putExtra("URL",
									fileArray[arg2].getAbsolutePath());
							videoIntent.putExtra("IS_LOCAL", true);
							mContext.startActivity(videoIntent);
						}
					}

				});
		return convertView;
	}

	class FolderHolder {
		TextView folderName;
		MyGridView fileGridView;
	}

}
