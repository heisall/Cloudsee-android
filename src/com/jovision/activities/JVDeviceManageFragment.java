package com.jovision.activities;

import java.util.ArrayList;

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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.adapters.TabPagerAdapter;
import com.jovision.bean.Device;
import com.jovision.commons.MyLog;
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

		currentMenu.setText(R.string.str_help1_1);
		rightBtn.setOnClickListener(mOnClickListener);

		mScreenWidth = mActivity.disMetrics.widthPixels;
		mHorizontalScrollView = (HorizontalScrollView) mActivity
				.findViewById(R.id.hsv_view);
		mLinearLayout = (LinearLayout) mActivity.findViewById(R.id.hsv_content);
		mImageView = (ImageView) mActivity.findViewById(R.id.img);
		item_width = (int) ((mScreenWidth / 4.0 + 0.5f));
		mImageView.getLayoutParams().width = item_width;
		managePager = (ViewPager) mActivity.findViewById(R.id.manage_pager);

		// 初始化导航
		initNav();
		// 初始化viewPager
		initViewPager();
		managePager.setCurrentItem(deviceIndex);
	}

	private void initNav() {
		manageDeviceList = CacheUtil.getDevList();
		int size = manageDeviceList.size();
		fragments = new ArrayList<Fragment>();

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
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			layout.addView(view, params);
			mLinearLayout.addView(layout, (int) (mScreenWidth / 4 + 0.5f), 50);
			layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					int index = (Integer) view.getTag();
					deviceIndex = index;
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
			case R.id.btn_right: {
				break;
			}
			}

		}
	};

	public class ManagePageChangeListener implements OnPageChangeListener {

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
			deviceIndex = position;
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
		super.onResume();
	}

	@Override
	public void onPause() {
		CacheUtil.saveDevList(manageDeviceList);
		super.onPause();
	}

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		// case Consts.TAB_ONRESUME: {// activity起来后开始加载设备
		// MyLog.v(TAG, "TAB_ONRESUME");
		// manageDeviceList = CacheUtil.getDevList();
		// // 初始化导航
		// initNav();
		// // 初始化viewPager
		// initViewPager();
		// managePager.setCurrentItem(deviceIndex);
		// break;
		// }

		}
	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		fragHandler.sendMessage(fragHandler
				.obtainMessage(what, arg1, arg2, obj));
	}

}
