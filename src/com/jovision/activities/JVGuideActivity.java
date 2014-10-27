package com.jovision.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.ConfigUtil;
import com.jovision.views.GuidViewPager;

public class JVGuideActivity extends BaseActivity {

	private int[] imgIdArray = { R.drawable.guide1, R.drawable.guide2,
			R.drawable.guide3, R.drawable.guide4 };
	private int[] imgIDArrayEn = { R.drawable.guide1e, R.drawable.guide2e,
			R.drawable.guide3e, R.drawable.guide4e };

	private int[] imageArray = null;
	private Button startBtn;
	// private int arrayFlag = 1;// 图片数组标志位

	private GuidViewPager guideScroll; // 图片容器
	private LinearLayout ovalLayout; // 圆点容器
	private List<View> listViews; // 图片组

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		handler.sendMessage(handler.obtainMessage(what, arg1, arg2, obj));
	}

	@Override
	protected void initSettings() {
		if (ConfigUtil.isLanZH()) {
			imageArray = imgIdArray;
		} else {
			imageArray = imgIDArrayEn;
		}
	}

	@Override
	protected void initUi() {
		setContentView(R.layout.guide_layout);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		/** 广告条 */
		guideScroll = (GuidViewPager) findViewById(R.id.imagescroll);
		ovalLayout = (LinearLayout) findViewById(R.id.dot_layout);
		InitViewPager();// 初始化图片
		// 开始滚动
		guideScroll.start(JVGuideActivity.this, listViews, 400000000,
				ovalLayout, R.layout.dot_item, R.id.ad_item_v,
				R.drawable.dot_focused, R.drawable.dot_normal);

		startBtn = (Button) findViewById(R.id.start_btn);
		startBtn.setVisibility(View.GONE);
		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(JVGuideActivity.this, JVLoginActivity.class);
				MySharedPreference.putBoolean(Consts.KEY_SHOW_GUID, false);
				JVGuideActivity.this.startActivity(intent);
				JVGuideActivity.this.finish();
			}
		});

	}

	/**
	 * 初始化图片
	 */
	private void InitViewPager() {
		listViews = new ArrayList<View>();

		for (int i = 0; i < imageArray.length; i++) {
			ImageView imageView = new ImageView(JVGuideActivity.this);
			imageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {// 设置图片点击事件
				}
			});
			imageView.setImageResource(imageArray[i]);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			listViews.add(imageView);
		}
	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			JVGuideActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		switch (what) {
		case Consts.GUID_PAGE_SCROLL:
			if (arg1 == (listViews.size() - 1)) {
				startBtn.setVisibility(View.VISIBLE);
			} else {
				startBtn.setVisibility(View.GONE);
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
