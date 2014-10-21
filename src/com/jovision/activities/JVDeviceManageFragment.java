package com.jovision.activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.jovetech.CloudSee.temp.R;
import com.jovision.IHandlerLikeNotify;
import com.jovision.adapters.ManageListAdapter;
import com.jovision.adapters.TabPagerAdapter;
import com.jovision.bean.Device;
import com.jovision.utils.CacheUtil;

/**
 * 设备管理
 */
public class JVDeviceManageFragment extends BaseFragment {

	private String TAG = "JVDeviceManageFragment";

	/** scroll layout */
	private HorizontalScrollView mHorizontalScrollView;
	private LinearLayout mLinearLayout;
	private ViewPager managePager;
	private ImageView mImageView;
	private int mScreenWidth;
	private int item_width;

	private int endPosition;
	private int beginPosition;
	private int currentFragmentIndex;
	private boolean isEnd;
	private RelativeLayout devmorere;
	private RelativeLayout devmore_hie;
	private TextView device_num;
	private ListView devicemanage_listView;
	private ManageListAdapter adapter;
	/** 两个列表界面 */
	private LinearLayout dataLayout;// 数据界面
	private RelativeLayout relalist;
	private RelativeLayout relative;
	/** 帮助引导 */
	private LinearLayout quickSetSV; // 快速配置界面
	private Button quickSet;
	private Button addDevice;

	private ArrayList<Fragment> fragments;

	/** intent传递过来的设备和通道下标 */
	private int deviceIndex;
	private ArrayList<Device> manageDeviceList = new ArrayList<Device>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_devicemanage, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mParent = getView();
		mActivity = (BaseActivity) getActivity();

		currentMenu.setText(R.string.str_help1_2);
		rightBtn.setVisibility(View.GONE);
		rightBtn.setOnClickListener(mOnClickListener);

		mScreenWidth = mActivity.disMetrics.widthPixels;
		mHorizontalScrollView = (HorizontalScrollView) mActivity
				.findViewById(R.id.hsv_view);
		mLinearLayout = (LinearLayout) mActivity.findViewById(R.id.hsv_content);
		mImageView = (ImageView) mActivity.findViewById(R.id.img);
		item_width = (int) ((mScreenWidth / 3.0 + 0.5f));
		mImageView.getLayoutParams().width = item_width;
		managePager = (ViewPager) mParent.findViewById(R.id.manage_pagerer);

		/** 管理功能 */
		device_num = (TextView) mParent.findViewById(R.id.device_num);
		dataLayout = (LinearLayout) mParent.findViewById(R.id.datalayout);
		relalist = (RelativeLayout) mParent.findViewById(R.id.relalist);
		relative = (RelativeLayout) mParent.findViewById(R.id.relative);

		/** 引导加设备 */
		quickSetSV = (LinearLayout) mParent
				.findViewById(R.id.quickinstalllayout);
		quickSet = (Button) mParent.findViewById(R.id.quickinstall);
		addDevice = (Button) mParent.findViewById(R.id.adddevice);
		quickSet.setOnClickListener(mOnClickListener);
		addDevice.setOnClickListener(mOnClickListener);

		devicemanage_listView = (ListView) mParent
				.findViewById(R.id.device_listView);
		devmorere = (RelativeLayout) mParent.findViewById(R.id.devmorere);
		devmore_hie = (RelativeLayout) mParent.findViewById(R.id.devmore_hie);
		devmorere.setOnClickListener(mOnClickListener);
		devmore_hie.setOnClickListener(mOnClickListener);
		ListViewClick();
	}

	// 设备列表的点击事件
	private void ListViewClick() {
		devicemanage_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				for (int i = 0; i < manageDeviceList.size(); i++) {
					if (i == position) {
						manageDeviceList.get(i).setIsselect(true);
					} else {
						manageDeviceList.get(i).setIsselect(false);
					}
				}
				adapter.notifyDataSetChanged();
				deviceIndex = position;
				((ManageFragment) fragments.get(position))
						.setDevIndex(deviceIndex);
				managePager.setCurrentItem(position);

				relalist.setVisibility(View.GONE);
				devicemanage_listView.setVisibility(View.GONE);
				managePager.setVisibility(View.VISIBLE);
				relative.setVisibility(View.VISIBLE);
			}
		});
	}

	private void initNav() {
		adapter = new ManageListAdapter(
				(BaseFragment) JVDeviceManageFragment.this);
		adapter.setData(manageDeviceList);
		devicemanage_listView.setAdapter(adapter);
		int size = manageDeviceList.size();
		fragments = new ArrayList<Fragment>();
		mLinearLayout.removeAllViews();
		for (int i = 0; i < size; i++) {
			// [Neo] viewpager
			Bundle data = new Bundle();
			data.putInt("DeviceIndex", i);
			ManageFragment fragment = new ManageFragment();
			fragment.setArguments(data);
			fragments.add(fragment);

			// [Neo] nav
			RelativeLayout layout = new RelativeLayout(mActivity);
			TextView view = new TextView(mActivity);
			view.setText(manageDeviceList.get(i).getFullNo());
			view.setSingleLine(true);
			view.setTextSize(16);
			view.setId(i);
			view.setTextColor(mActivity.getResources().getColor(
					R.color.devicemanagename));
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			layout.addView(view, params);
			mLinearLayout.addView(layout, (int) (mScreenWidth / 3 + 0.5f), 50);
			layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					int index = (Integer) view.getTag();
					deviceIndex = index;
					((ManageFragment) fragments.get(index))
							.setDevIndex(deviceIndex);
					managePager.setCurrentItem(index);
				}
			});
			layout.setTag(i);
		}

	}

	private void initViewPager() {
		TabPagerAdapter fragmentPagerAdapter = new TabPagerAdapter(
				getChildFragmentManager(), fragments);
		managePager.setAdapter(fragmentPagerAdapter);
		managePager.setOnPageChangeListener(new ManagePageChangeListener());
		// [Neo] no need to reset this one
		// fragmentPagerAdapter.setFragments(fragments);
	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_right:

				break;
			case R.id.devmorere:
				device_num.setText(mActivity.getResources().getString(
						R.string.str_fre)
						+ manageDeviceList.size()
						+ mActivity.getResources().getString(R.string.str_aft));
				relalist.setVisibility(View.VISIBLE);
				devicemanage_listView.setVisibility(View.VISIBLE);
				managePager.setVisibility(View.GONE);
				relative.setVisibility(View.GONE);
				break;
			case R.id.devmore_hie:
				adapter.notifyDataSetChanged();
				relalist.setVisibility(View.GONE);
				devicemanage_listView.setVisibility(View.GONE);
				managePager.setVisibility(View.VISIBLE);
				relative.setVisibility(View.VISIBLE);
				break;
			case R.id.quickinstall:
				((ShakeActivity) mActivity).startSearch(false);
				break;
			case R.id.adddevice:
				Intent addIntent = new Intent();
				addIntent.setClass(mActivity, JVAddDeviceActivity.class);
				addIntent.putExtra("QR", false);
				mActivity.startActivity(addIntent);
				break;
			default:
				break;
			}

		}
	};

	public class ManagePageChangeListener implements OnPageChangeListener {

		@SuppressLint("ResourceAsColor")
		@Override
		public void onPageSelected(final int position) {
			// MyLog.v(TAG, "onPageSelected---position="+position);
			Animation animation = new TranslateAnimation(endPosition, position
					* item_width, 0, 0);

			beginPosition = position * item_width;

			currentFragmentIndex = position;
			if (animation != null) {
				animation.setFillAfter(true);
				animation.setDuration(0);
				mImageView.startAnimation(animation);
				mHorizontalScrollView.smoothScrollTo((currentFragmentIndex - 1)
						* item_width, 0);
			}
			for (int i = 0; i < manageDeviceList.size(); i++) {
				if (position == i) {
					manageDeviceList.get(i).setIsselect(true);
					TextView view = (TextView) mLinearLayout.getChildAt(i)
							.findViewById(i);
					view.setTextColor(mActivity.getResources().getColor(
							R.color.quickinstall_btn_normal));
				} else {
					manageDeviceList.get(i).setIsselect(false);
					TextView view = (TextView) mLinearLayout.getChildAt(i)
							.findViewById(i);
					view.setTextColor(mActivity.getResources().getColor(
							R.color.devicemanagename));
				}
			}
			deviceIndex = position;
			((ManageFragment) fragments.get(position)).setDevIndex(deviceIndex);
			managePager.setCurrentItem(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			// MyLog.v(TAG, "onPageScrolled---position="+position);
			if (!isEnd) {
				if (currentFragmentIndex == position) {
					endPosition = item_width * currentFragmentIndex
							+ (int) (item_width * positionOffset);
				}
				if (currentFragmentIndex == position + 1) {
					endPosition = item_width * currentFragmentIndex
							- (int) (item_width * (1 - positionOffset));
				}

				Animation mAnimation = new TranslateAnimation(beginPosition,
						endPosition, 0, 0);
				mAnimation.setFillAfter(true);
				mAnimation.setDuration(0);
				mImageView.startAnimation(mAnimation);
				mHorizontalScrollView.invalidate();
				beginPosition = endPosition;
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_DRAGGING) {
				isEnd = false;
			} else if (state == ViewPager.SCROLL_STATE_SETTLING) {
				isEnd = true;
				beginPosition = currentFragmentIndex * item_width;
				if (managePager.getCurrentItem() == currentFragmentIndex) {
					// 未跳入下一个页面
					mImageView.clearAnimation();
					Animation animation = null;
					// 恢复位置
					animation = new TranslateAnimation(endPosition,
							currentFragmentIndex * item_width, 0, 0);
					animation.setFillAfter(true);
					animation.setDuration(1);
					mImageView.startAnimation(animation);
					mHorizontalScrollView.invalidate();
					endPosition = currentFragmentIndex * item_width;
				}
				// MyLog.v(TAG,
				// "onPageScrollStateChanged---currentFragmentIndex="+currentFragmentIndex+"---deviceIndex="+deviceIndex);
			}
		}

	}

	@Override
	public void onResume() {
		manageDeviceList = CacheUtil.getDevList();
		for (int i = 0; i < manageDeviceList.size(); i++) {
			if (i == deviceIndex) {
				manageDeviceList.get(i).setIsselect(true);
			} else {
				manageDeviceList.get(i).setIsselect(false);
			}
		}

		if (null == manageDeviceList || 0 == manageDeviceList.size()) {
			dataLayout.setVisibility(View.GONE);
			quickSetSV.setVisibility(View.VISIBLE);
		} else {
			dataLayout.setVisibility(View.VISIBLE);
			quickSetSV.setVisibility(View.GONE);
			// 初始化导航
			initNav();
			// 初始化viewPager
			initViewPager();
			managePager.setCurrentItem(deviceIndex);
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		CacheUtil.saveDevList(manageDeviceList);
		super.onPause();
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO 以后增加过滤
		try {
			Fragment currentFrag = fragments.get(deviceIndex);
			if (null != currentFrag) {
				((IHandlerLikeNotify) currentFrag).onNotify(what, arg1, arg2,
						obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}

}
