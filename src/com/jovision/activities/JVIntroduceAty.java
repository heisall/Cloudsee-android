package com.jovision.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jovetech.CloudSee.temp.R;
import com.jovision.adapters.MyPagerAdp;

public class JVIntroduceAty extends Activity implements OnPageChangeListener {
	private ViewPager viewpager; // 显示滑动图片
	private List<View> pics; // 图片集合
	// 图片数组
	private int[] images = { R.drawable.one, R.drawable.two, R.drawable.three };
	private List<ImageView> dots; // 点集合
	private int currentImage = 0; // 当前页面索引
	private int oldImage = 0; // 前一个页面索引
	private MyPagerAdp adp; // ViewPager适配器

	private int viewnum;

	// 更新布局
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewpager.setCurrentItem(msg.arg1);
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.introduce);

		viewnum = getIntent().getIntExtra("viewnum", 2);

		initView();
		initDot();
		getPic();
	}

	/**
	 * 
	 * 功能描述：获取到图片 创 建 人：闫帅 日 期：2014年10月09日 修 改 人：
	 */
	private void getPic() {
		pics = new ArrayList<View>();
		for (int i = 0; i < viewnum; i++) {
			ImageView v = new ImageView(this);
			v.setBackgroundResource(images[i]);
			if (i == viewnum - 1) {
				v.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						startActivity(new Intent(JVIntroduceAty.this,
								JVFeedbackActivity.class));
						finish();
					}
				});
			}
			pics.add(v);
		}
		// //将最后一个图单独设置成一个布局，加入按钮并设置按钮的点击事件
		// View v =
		// LayoutInflater.from(IntroduceAty.this).inflate(R.layout.last_picture,
		// null);
		// pics.add(v);
		// ImageView button =
		// (ImageView)v.findViewById(R.id.last_picture_tiyan);
		// button.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// startActivity(new
		// Intent(IntroduceAty.this,LocationSourceActivity.class));
		// finish();
		// }
		// });
		// 初始化ViewPager的适配器
		adp = new MyPagerAdp(pics);
		viewpager.setAdapter(adp);
		// 监听页面滑动事件
		viewpager.setOnPageChangeListener(this);
	}

	/**
	 * 
	 * 功能描述：获取点对象 创 建 人：闫帅 日 期：2014年10月09日 修 改 人：
	 */
	private void initDot() {
		dots = new ArrayList<ImageView>();
		// 得到点的父布局
		LinearLayout ll_dot = (LinearLayout) findViewById(R.id.ll_dot);
		for (int i = 0; i < viewnum; i++) {
			ll_dot.getChildAt(i);// 得到点
			dots.add((ImageView) ll_dot.getChildAt(i));
			dots.get(i).setEnabled(false);// 将点设置为白色
		}
		dots.get(currentImage).setEnabled(true); // 因为默认显示第一张图片，将第一个点设置为黑色
	}

	/**
	 * 
	 * 功能描述：初始化控件，绑定ViewPager 创 建 人：闫帅 日 期：2014年10月09日 修 改 人：
	 */
	private void initView() {
		viewpager = (ViewPager) findViewById(R.id.viewpager);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		currentImage = arg0; // 获取当前页面索引
		dots.get(oldImage).setEnabled(false); // 前一个点设置为白色
		dots.get(currentImage).setEnabled(true); // 当前点设置为黑色
		oldImage = currentImage; // 改变前一个索引

		currentImage = (currentImage) % viewnum; // 有几张就对几求余
		Message msg = new Message();
		msg.arg1 = currentImage;
		handler.sendMessage(msg);
	}
}
