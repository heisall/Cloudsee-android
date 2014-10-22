package com.jovision.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

	private boolean disableSliding;

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewPager(Context context) {
		super(context);
	}

	/**
	 * 设置禁用滑动
	 * 
	 * @param disableSliding
	 */
	public void setDisableSliding(boolean disableSliding) {
		this.disableSliding = disableSliding;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return (disableSliding ? false : super.onInterceptTouchEvent(arg0));
	}

}
