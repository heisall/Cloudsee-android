package com.jovision.views;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jovision.activities.JVPlayActivity;

public class MyViewPager extends ViewPager {

	Context mContext;

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void setContext(Context context) {
		mContext = context;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		boolean flag = false;
		if (Configuration.ORIENTATION_LANDSCAPE == ((JVPlayActivity) mContext).configuration.orientation) {
			flag = false;
		} else {
			flag = super.onInterceptTouchEvent(arg0);
		}
		return flag;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		boolean flag = false;
		if (Configuration.ORIENTATION_LANDSCAPE == ((JVPlayActivity) mContext).configuration.orientation) {
			flag = false;
		} else {
			flag = super.onTouchEvent(arg0);
		}
		return flag;
	}

}
