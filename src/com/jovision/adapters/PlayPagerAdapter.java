
package com.jovision.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PlayPagerAdapter extends PagerAdapter {
    private ArrayList<View> mPageList;
    private Context mContext;

    public PlayPagerAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<View> pageList) {
        mPageList = pageList;
    }

    @Override
    public int getCount() {
        return mPageList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return (arg0 == arg1);
    }

    @Override
    public void destroyItem(ViewGroup container, final int position,
            Object object) {
        if (position < mPageList.size()) {
            container.removeView(mPageList.get(position));
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // [Neo] TODO wait for connect to resume
        // mWindowManager.resumePage(position);
        container.addView(mPageList.get(position));
        return mPageList.get(position);
    }

}
