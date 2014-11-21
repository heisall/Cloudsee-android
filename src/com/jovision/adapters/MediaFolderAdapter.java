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
import com.jovision.activities.BaseActivity;
import com.jovision.activities.JVImageViewActivity;
import com.jovision.activities.JVMediaListActivity;
import com.jovision.activities.JVVideoActivity;
import com.jovision.bean.Filebean;
import com.jovision.views.MyGridView;

public class MediaFolderAdapter extends BaseAdapter {

	public ArrayList<File> folderList = new ArrayList<File>();
	public ArrayList<Filebean> daArrayList = new ArrayList<Filebean>();
	public static BaseActivity mContext;
	public LayoutInflater inflater;
	private String media;// 区分图片还是视频
	private boolean loadImg = true;
	private boolean isdelect;
	private boolean isselectall;

	public MediaFolderAdapter(BaseActivity con) {
		mContext = con;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(String media, ArrayList<File> folderList,
			boolean isdelect, boolean isselectall) {
		this.folderList = folderList;
		this.media = media;
		this.isdelect = isdelect;
		this.isselectall = isselectall;
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
		daArrayList = JVMediaListActivity.fileMap.get(folderPath);
		mediaAdaper.setData(daArrayList, media, fileArray, loadImg, isdelect,
				isselectall);
		folderHolder.fileGridView.setAdapter(mediaAdaper);
		folderHolder.fileGridView
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						if (!isdelect) {
							daArrayList = JVMediaListActivity.fileMap
									.get(folderPath);
							mediaAdaper.setData(daArrayList, media, fileArray,
									loadImg, isdelect, isselectall);
							for (int i = 0; i < daArrayList.size(); i++) {
								if (position == i) {
									if (daArrayList.get(i).isSelect()) {
										daArrayList.get(i).setSelect(false);
										JVMediaListActivity.fileSelectSum--;
									} else {
										daArrayList.get(i).setSelect(true);
										JVMediaListActivity.fileSelectSum++;
									}
								}
							}
						}
						mediaAdaper.setSelect(position);
						mediaAdaper.notifyDataSetChanged();
						if (JVMediaListActivity.fileSelectSum == JVMediaListActivity.fileSum) {
							mContext.onNotify(JVMediaListActivity.FILE_SUM,
									JVMediaListActivity.fileSelectSum, 1, null);
						} else {
							mContext.onNotify(JVMediaListActivity.FILE_SUM,
									JVMediaListActivity.fileSelectSum, 0, null);
						}
						if ("image".equalsIgnoreCase(media)) {
							if (isdelect) {
								Intent imageIntent = new Intent();
								imageIntent.setClass(mContext,
										JVImageViewActivity.class);
								imageIntent.putExtra("FolderPath", folderPath);
								imageIntent.putExtra("FileIndex", position);
								mContext.startActivity(imageIntent);
							}
						} else if ("video".equalsIgnoreCase(media)) {
							if (isdelect) {
								Intent videoIntent = new Intent();
								videoIntent.setClass(mContext,
										JVVideoActivity.class);
								videoIntent.putExtra("URL",
										fileArray[position].getAbsolutePath());
								videoIntent.putExtra("IS_LOCAL", true);
								mContext.startActivity(videoIntent);
							}
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
