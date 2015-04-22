
package com.jovision.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MyPagerAdp extends PagerAdapter {

    private List<View> pics;

    public MyPagerAdp(List<View> pics) {
        this.pics = pics;
    }

    @Override
    public int getCount() {
        return pics.size();
    }

    /**
     * �Ƴ�view
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    /**
     * ���view
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(pics.get(position));
        return pics.get(position);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

}
