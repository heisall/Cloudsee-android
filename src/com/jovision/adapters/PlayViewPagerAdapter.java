//package com.jovision.adapters;
//
//import java.util.ArrayList;
//
//import android.support.v4.view.PagerAdapter;
//import android.view.View;
//import android.view.ViewGroup;
//
///**
// * 多窗口播放滑页适配器
// * 
// * @author neo
// * 
// */
//public class PlayViewPagerAdapter extends PagerAdapter {
//
//	private ArrayList<View> list;
//
//	public void update(ArrayList<View> list) {
//		if (null != list) {
//			this.list = list;
//			notifyDataSetChanged();
//		}
//	}
//
//	@Override
//	public int getCount() {
//		int result = 0;
//		if (null != list) {
//			result = list.size();
//		}
//		return result;
//	}
//
//	@Override
//	public boolean isViewFromObject(View arg0, Object arg1) {
//		// [Neo] TODO
//		return (arg0 == arg1);
//	}
//
//	@Override
//	public void destroyItem(ViewGroup container, int position, Object object) {
//		if (null != list && list.size() > position) {
//			container.removeView(list.get(position));
//		}
//	}
//
//	@Override
//	public Object instantiateItem(ViewGroup container, int position) {
//		Object result = null;
//		if (null != list && list.size() > position) {
//			container.addView(list.get(position));
//			result = list.get(position);
//		}
//		return result;
//	}
// }
