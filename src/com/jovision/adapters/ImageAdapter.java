package com.jovision.adapters;

import java.util.ArrayList;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.jovision.bean.NewProduct;
import com.jovision.commons.AsyncImageLoader;
import com.jovision.commons.AsyncImageLoader.ImageCallback;
import com.jovision.commons.Url;

public class ImageAdapter extends BaseAdapter {

	public ArrayList<NewProduct> galleryList = new ArrayList<NewProduct>();
	public Context mContext = null;
	public LayoutInflater inflater;
	private Bitmap bitmap;

	// AsyncImageLoader asyncImageLoader;

	public ImageAdapter(Context con) {
		mContext = con;
		// asyncImageLoader = new AsyncImageLoader();
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// public void recyleBmp()
	// {
	// if(null != bitmap && !bitmap.isRecycled())
	// {
	// bitmap.recycle();
	// bitmap = null;
	// }
	// }
	public void setData(ArrayList<NewProduct> list) {
		galleryList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int size = 0;
		if (null != galleryList && 0 != galleryList.size()) {
			size = galleryList.size();
		}

		return size;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		NewProduct np = new NewProduct();
		if (null != galleryList && 0 != galleryList.size()
				&& position < galleryList.size()) {
			np = galleryList.get(position % galleryList.size());
		}
		return np;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GalleryHolder galleryHolder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.newpro_image_item, null);
			galleryHolder = new GalleryHolder();
			galleryHolder.galleryImg = (ImageView) convertView
					.findViewById(R.id.img);
			convertView.setTag(galleryHolder);
		} else {
			galleryHolder = (GalleryHolder) convertView.getTag();
		}

		final GalleryHolder holder = galleryHolder;

		if (position < 0) {
			position = 0;
		}
		if (null != galleryList && 0 != galleryList.size()) {

			String url = Url.DOWNLOAD_IMAGE_URL
					+ galleryList.get(position % galleryList.size()).newProImgUrl;
			// Bitmap bitmap = ImageUtil.getBitmap1(url);
			holder.galleryImg.setTag(url);
			// Log.e("tags", "newProjectURl: "+url);

			// 调用这个方法如果本地已存在不会重复下载
			// bitmap = ImageUtil.getbitmapAndwrite(mContext,url);

			bitmap = AsyncImageLoader.getInstance().imageCache.get(url);
			// Log.e("tag---------------------",
			// "size: "+AsyncImageLoader.getInstance().imageCache.size());
			if (null == bitmap) {
				// Log.e("tag---------------------", "not from cache");
				bitmap = AsyncImageLoader.getInstance().loadBitmap(url,
						new ImageCallback() {
							@Override
							public void imageLoaded(Bitmap imageBitmap,
									String imageUrl) {
								ImageView imageViewByTag = (ImageView) holder
										.findViewWithTag(imageUrl);
								if (imageViewByTag != null) {
									Drawable bitmapDrawable = new BitmapDrawable(
											imageBitmap);
									imageViewByTag
											.setBackgroundDrawable(bitmapDrawable);
								} else {
									// load image failed from Internet
								}
							}
						});
			}
			// else{
			// Log.e("tag---------------------", " from cache");
			// }

			if (null != bitmap) {
				Drawable bitmapDrawable = new BitmapDrawable(bitmap);
				galleryHolder.galleryImg.setBackgroundDrawable(bitmapDrawable);
				// galleryHolder.galleryImg.setImageBitmap(bitmap);
			} else {
				galleryHolder.galleryImg.setBackgroundDrawable(mContext
						.getResources().getDrawable(
								R.drawable.viewflow_default_img));
			}
		}

		return convertView;
	}

	class GalleryHolder {
		ImageView galleryImg;

		public ImageView findViewWithTag(String imageUrl) {
			// TODO Auto-generated method stub

			if (imageUrl.equalsIgnoreCase(galleryImg.getTag().toString())) {
				return galleryImg;
			}
			return null;
		}

	}

}
