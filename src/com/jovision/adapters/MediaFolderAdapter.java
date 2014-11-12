package com.jovision.adapters;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.views.MyGridView;

public class MediaFolderAdapter extends BaseAdapter {

	public ArrayList<File> folderList = new ArrayList<File>();
	public Context mContext = null;
	public LayoutInflater inflater;
	private String media;// 区分图片还是视频

	public MediaFolderAdapter(Context con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(String tag, ArrayList<File> list) {
		folderList = list;
		media = tag;
	}

	@Override
	public int getCount() {
		return folderList.size();
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
		MediaAdapter mediaAdaper = new MediaAdapter(mContext);
		mediaAdaper.setData(media, folderList.get(position).listFiles());
		folderHolder.fileGridView.setAdapter(mediaAdaper);
		return convertView;
	}

	class FolderHolder {
		TextView folderName;
		MyGridView fileGridView;
	}

}
