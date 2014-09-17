package com.jovision.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jovision.commons.BaseApp;

/**
 * 轮显adapter
 * 
 * @author Administrator
 * 
 */
public class ViewPagerAdapter extends PagerAdapter {
	// ArrayList<JVMatrixView> viewList = new ArrayList<JVMatrixView>();
	// private ArrayList<JVConnectInfo> jvcList;
	// private ArrayList<JVConnectInfo> currentPageList;
	private ArrayList<View> gridViewList = new ArrayList<View>();
	private int pageNum = 0;
	private Context context;

	// public HashMap<Integer,GridView> viewHashMap = new
	// HashMap<Integer,GridView>();
	//
	// RelativeLayout.LayoutParams reParamsV;
	// RelativeLayout.LayoutParams reParamsH;

	public ViewPagerAdapter(Context context, ArrayList<View> list) {
		this.context = context;
		// this.pageNum = page;
		this.gridViewList = list;
		// //竖屏
		// if (Configuration.ORIENTATION_PORTRAIT ==
		// JVPlayActivity.getInstance().config.orientation) {
		// reParamsV = new RelativeLayout.LayoutParams(
		// ViewGroup.LayoutParams.MATCH_PARENT,
		// (int)JVPlayActivity.playProportion*JVPlayActivity.screenWidth);
		// reParamsH = new RelativeLayout.LayoutParams(
		// ViewGroup.LayoutParams.MATCH_PARENT,
		// ViewGroup.LayoutParams.MATCH_PARENT);
		// }else{
		// reParamsH = new RelativeLayout.LayoutParams(
		// ViewGroup.LayoutParams.MATCH_PARENT,
		// ViewGroup.LayoutParams.MATCH_PARENT);
		// reParamsV = new RelativeLayout.LayoutParams(
		// ViewGroup.LayoutParams.MATCH_PARENT,
		// ViewGroup.LayoutParams.MATCH_PARENT);
		// }
	}

	// public void setData(ArrayList<JVMatrixView> arg,int page){
	// viewList = arg;
	// pageNum = page;
	// }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gridViewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		// super.destroyItem(container, position, object);
		if (position < gridViewList.size()) {
			((ViewPager) container).removeView(gridViewList.get(position));
		}

	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		// Log.e("%%%%%%%%%%position", position+"");

		// return super.instantiateItem(container, position);
		// Log.e("%%%%%%%%%%count", ((ViewPager)container).getChildCount()+"");
		if (BaseApp.SCREEN == BaseApp.SINGLE_SCREEN) {
			RelativeLayout gridView = (RelativeLayout) gridViewList
					.get(position);
			((ViewPager) container).addView(gridView, 0);
			// jvmView.canDraw = true;
			return gridView;
		}

		View gridView = (View) gridViewList.get(position);
		((ViewPager) container).addView(gridView, 0);
		// jvmView.canDraw = true;
		return gridView;

		//
		// GridView videoGridView = new GridView(context);
		// if(1 == JVPlayActivity.SCREEN){
		// if(null != jvcList && page < jvcList.size()){
		// if(null == currentPageList){
		// currentPageList = new ArrayList<JVConnectInfo>();
		// }
		// currentPageList.clear();
		// currentPageList.add(jvcList.get(page));
		// }
		//
		//
		// //// return super.instantiateItem(container, position);
		// // Log.e("%%%%%%%%%%position", position+"");
		// //// Log.e("%%%%%%%%%%count",
		// ((ViewPager)container).getChildCount()+"");
		// // ImageView jvmView = viewList.get(position);
		// // ((ViewPager)container).addView(jvmView,0);
		// //// jvmView.canDraw = true;
		// // return jvmView;
		// //竖屏
		// if (Configuration.ORIENTATION_PORTRAIT ==
		// JVPlayActivity.getInstance().config.orientation) {
		// videoGridView.setLayoutParams(reParamsV);
		// }else{
		// videoGridView.setLayoutParams(reParamsH);
		// }
		// videoGridView.setNumColumns(1);
		// VideoGridViewAdapter videoAdapter = new
		// VideoGridViewAdapter(context,page);
		// videoAdapter.setData(currentPageList);
		// videoGridView.setAdapter(videoAdapter);
		// }else if(4 == JVPlayActivity.SCREEN){
		//
		// if(null != jvcList && page <= jvcList.size()/4){
		// if(null == currentPageList){
		// currentPageList = new ArrayList<JVConnectInfo>();
		// }
		// currentPageList.clear();
		// int start = page*4;
		// int end = 0;
		// if((page+1)*4 <= jvcList.size()){//当前页够4个
		// end = (page+1)*4;
		// }else{
		// end = jvcList.size();
		// }
		//
		// for(int i = start; i < end ; i++){
		// currentPageList.add(jvcList.get(i));
		// }
		// }
		//
		//
		// while(currentPageList.size() < 4){
		// JVConnectInfo jvc = new JVConnectInfo();
		// jvc.setChannel(-100);
		// currentPageList.add(jvc);
		// }
		//
		// if(currentPageList.size() < 4){
		// for(int i = 0 ; i < 4 - currentPageList.size(); i++){
		// JVConnectInfo jvc = new JVConnectInfo();
		// jvc.setChannel(-100);
		// currentPageList.add(jvc);
		// }
		// }
		//
		// //竖屏
		// if (Configuration.ORIENTATION_PORTRAIT ==
		// JVPlayActivity.getInstance().config.orientation) {
		// videoGridView.setLayoutParams(reParamsV);
		// }else{
		// videoGridView.setLayoutParams(reParamsH);
		// }
		//
		// Log.e("currentPageList.size()---", currentPageList.size()+"");
		//
		// videoGridView.setNumColumns(2);
		// videoGridView.setVerticalSpacing(1);
		// videoGridView.setHorizontalSpacing(1);
		// VideoGridViewAdapter videoAdapter = new
		// VideoGridViewAdapter(context,page);
		// videoAdapter.setData(currentPageList);
		// videoGridView.setAdapter(videoAdapter);
		// }else if(9 == JVPlayActivity.SCREEN){
		//
		// if(null != jvcList && page <= jvcList.size()/9){
		// if(null == currentPageList){
		// currentPageList = new ArrayList<JVConnectInfo>();
		// }
		// currentPageList.clear();
		// int start = page*9;
		// int end = 0;
		// if((page+1)*9 <= jvcList.size()){//当前页够9个
		// end = (page+1)*9;
		// }else{
		// end = jvcList.size();
		// }
		//
		// for(int i = start; i < end ; i++){
		// currentPageList.add(jvcList.get(i));
		// }
		// }
		//
		// while(currentPageList.size() < 9){
		// JVConnectInfo jvc = new JVConnectInfo();
		// jvc.setChannel(-100);
		// currentPageList.add(jvc);
		// }
		//
		// //竖屏
		// if (Configuration.ORIENTATION_PORTRAIT ==
		// JVPlayActivity.getInstance().config.orientation) {
		// videoGridView.setLayoutParams(reParamsV);
		// }else{
		// videoGridView.setLayoutParams(reParamsH);
		// }
		// videoGridView.setNumColumns(3);
		// videoGridView.setVerticalSpacing(1);
		// videoGridView.setHorizontalSpacing(1);
		// VideoGridViewAdapter videoAdapter = new
		// VideoGridViewAdapter(context,page);
		// videoAdapter.setData(currentPageList);
		// videoGridView.setAdapter(videoAdapter);
		// }
		//
		// ((ViewPager)container).addView(videoGridView,0);
		//
		// viewHashMap.put(page, videoGridView);
		//
		// return videoGridView;
	}

}
