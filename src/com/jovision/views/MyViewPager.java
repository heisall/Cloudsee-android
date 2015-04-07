
package com.jovision.views;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jovision.activities.PlayActivity;

public class MyViewPager extends ViewPager {

    private boolean disableSliding;
    private static Context mContext;

    public boolean isDisableSliding() {
        return disableSliding;
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewPager(Context context) {
        super(context);
    }

    public void setContext(Context context) {
        mContext = context;
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

        if (Configuration.ORIENTATION_LANDSCAPE == ((PlayActivity) mContext).configuration.orientation) {
            return false;
        }

        return (disableSliding ? false : super.onInterceptTouchEvent(arg0));
    }

}
