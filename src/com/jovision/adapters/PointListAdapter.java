package com.jovision.adapters;

import java.util.ArrayList;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovision.bean.ConnPoint;
import com.jovision.commons.AsyncImageLoader;
import com.jovision.commons.AsyncImageLoader.ImageCallback;
import com.jovision.commons.BaseApp;
import com.jovision.commons.Url;

public class PointListAdapter extends BaseAdapter {

	// AsyncImageLoader asyncImageLoader;

	public ArrayList<ConnPoint> pointList = new ArrayList<ConnPoint>();
	public Context mContext = null;
	public LayoutInflater inflater;
	public int screenWidth = 0;
	public int currentPointIndex = -1;
	private RelativeLayout.LayoutParams reParams;
	private RelativeLayout.LayoutParams ImageParams;
	private Bitmap coonBitmap;
	private int nameColor = Color.BLACK;

	public PointListAdapter(Context con) {
		mContext = con;
		// asyncImageLoader = new AsyncImageLoader();
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		screenWidth = dm.widthPixels;

		reParams = new RelativeLayout.LayoutParams(
				screenWidth / 4 / 4 * 3 + 15, screenWidth / 4 / 4 * 3);
		// reParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		ImageParams = new RelativeLayout.LayoutParams(screenWidth / 4 / 4 * 3,
				screenWidth / 4 / 4 * 3);
		ImageParams.setMargins(15, 0, 0, 0);
	}

	public void setData(ArrayList<ConnPoint> list, int color) {
		pointList = list;
		nameColor = color;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size = 0;
		if (null != pointList && 0 != pointList.size()) {
			size = pointList.size();
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		ConnPoint cp = null;
		if (null != pointList && 0 != pointList.size()
				&& position < pointList.size()) {
			cp = pointList.get(position);
		}
		return cp;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final PointHolder pointHolder;
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.connpoint_item, null);
			pointHolder = new PointHolder();
			pointHolder.imgLayout = (RelativeLayout) convertView
					.findViewById(R.id.pointimglayout);

			// 播放界面设置4:3的播放比例

			pointHolder.imgLayout.setLayoutParams(reParams);
			// pointHolder.imgLayout
			pointHolder.pointImg = (ImageView) convertView
					.findViewById(R.id.connpointimg);
			pointHolder.pointImg.setLayoutParams(ImageParams);
			pointHolder.pointName = (TextView) convertView
					.findViewById(R.id.connpointname);
			pointHolder.pointSelect = (ImageView) convertView
					.findViewById(R.id.pointselected);

			convertView.setTag(pointHolder);
		} else {
			pointHolder = (PointHolder) convertView.getTag();
		}

		pointHolder.pointName.setTextColor(nameColor);
		// final PointHolder holder = pointHolder;

		try {
			if (null != pointList && 0 != pointList.size()) {
				if (position < pointList.size()) {
					pointHolder.pointName
							.setText(pointList.get(position).pointName);
					if (null != pointList.get(position).connImage
							&& !"".equalsIgnoreCase(pointList.get(position).connImage.imageUrl)) {
						if (BaseApp.LOADIMAGE) {
							// Random ran=new Random();
							// int randomPos =
							// ran.nextInt(pointList.get(position).connImageList.size());
							// //获得随机数

							String url = Url.DOWNLOAD_IMAGE_URL
									+ pointList.get(position).connImage.imageUrl
											.trim();
							// Bitmap bitmap1 = ImageUtil.getBitmap(url);

							pointHolder.pointImg.setTag(url);
							// Log.e("图片地址",
							// "url: "+url+" , channel: "+position);
							// Bitmap bitmap1 = null;
							coonBitmap = AsyncImageLoader.getInstance().imageCache
									.get(url);

							if (coonBitmap == null) {

								coonBitmap = AsyncImageLoader.getInstance()
										.loadBitmap(url, new ImageCallback() {
											@Override
											public void imageLoaded(
													Bitmap imageBitmap,
													String imageUrl) {
												ImageView imageViewByTag = (ImageView) pointHolder
														.findViewWithTag(imageUrl);
												if (imageViewByTag != null) {
													// Bitmap bitmap2 =
													// ImageUtil.getRoundedCornerBitmap(imageBitmap,10.0f);
													// if(null != bitmap2){
													// imageViewByTag.setImageBitmap(bitmap2);
													// }
													// Log.e("tags",
													// "load image allright");
													Drawable bitmapDrawable = new BitmapDrawable(
															imageBitmap);
													imageViewByTag
															.setBackgroundDrawable(bitmapDrawable);
												} else {
													// load image failed from
													// Internet
													// imageViewByTag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.point_default_img));
												}
											}
										});
							}
							// else{
							// Drawable bitmapDrawable = new
							// BitmapDrawable(bitmap1);
							// pointHolder.pointImg.setBackgroundDrawable(bitmapDrawable);
							// }

							// Bitmap bitmap2 =
							// ImageUtil.getRoundedCornerBitmap(bitmap1,10.0f);

							if (null != coonBitmap) {
								Drawable bitmapDrawable = new BitmapDrawable(
										coonBitmap);
								pointHolder.pointImg
										.setBackgroundDrawable(bitmapDrawable);
							} else {
								pointHolder.pointImg
										.setBackgroundDrawable(mContext
												.getResources()
												.getDrawable(
														R.drawable.point_default_img));
							}
						} else {
							pointHolder.pointImg.setBackgroundDrawable(mContext
									.getResources().getDrawable(
											R.drawable.point_default_img));
						}
					} else {
						pointHolder.pointImg.setBackgroundDrawable(mContext
								.getResources().getDrawable(
										R.drawable.point_default_img));
					}
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		pointHolder.pointSelect.setVisibility(View.GONE);

		if (currentPointIndex == position) {
			pointHolder.pointSelect.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	// 通道
	class PointHolder {
		RelativeLayout imgLayout;
		ImageView pointImg;
		TextView pointName;
		ImageView pointSelect;

		public ImageView findViewWithTag(String imageUrl) {
			// TODO Auto-generated method stub

			if (imageUrl.equalsIgnoreCase(pointImg.getTag().toString())) {
				return pointImg;
			}
			return null;
		}
	}
}
