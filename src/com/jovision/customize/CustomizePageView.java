
package com.jovision.customize;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;

import java.util.List;
import java.util.Map;

/**
 * 自定义面板上的自定义元素布局
 */
public class CustomizePageView extends RelativeLayout {

    /**
     * Title text used when no title is provided by the adapter.
     */
    private static final CharSequence EMPTY_TITLE = "";
    // 面板上的元素
    List<Map<String, String>> tabItems;

    private Context mContext;
    private int mHeight;
    private int mWidth;
    private int mItemMarginHorizontalAverage;
    private int mItemMarginVerticalAverage;
    private int mItemMarginHorizontal;
    private int mItemMarginVertical;
    private OnTabTouchedListener mTabTouchedListener;

    public CustomizePageView(Context paramContext) {
        this(paramContext, null);
        this.mContext = paramContext;
    }

    public CustomizePageView(Context paramContext,
            AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        this.mContext = paramContext;
        init();
    }

    private void init() {
        WindowManager localWindowManager = (WindowManager) this.mContext
                .getSystemService("window");
        this.mHeight = localWindowManager.getDefaultDisplay().getHeight();
        this.mWidth = localWindowManager.getDefaultDisplay().getWidth();
        this.mItemMarginVertical = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.composer_item_margin_vertical);
    }

    protected int getSuggestedMinimumWidth() {
        return this.mWidth;
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
            int paramInt3, int paramInt4) {
        int childCount = getChildCount();
        int itemMarginTop;
        for (int i = 0; i < childCount; ++i) {
            View childView = getChildAt(i);
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            this.mItemMarginHorizontal = (1 + (this.mWidth - childWidth * 3) / 4);
            this.mItemMarginHorizontalAverage = ((this.mWidth - childWidth * 3 - 2 * this.mItemMarginHorizontal) / 2);
            this.mItemMarginVerticalAverage = ((this.mHeight - childHeight * 2 - 1 * this.mItemMarginVertical) / 2);
            itemMarginTop = this.mContext.getResources().getDimensionPixelSize(
                    R.dimen.composer_item_margin_top);
            if (this.mHeight <= this.mWidth)
                break;
            this.mItemMarginVerticalAverage = (itemMarginTop + this.mItemMarginVerticalAverage);
            int left = i % 3 * (childWidth + this.mItemMarginHorizontal)
                    + this.mItemMarginHorizontalAverage;
            int top = i / 3 * (childHeight + this.mItemMarginVertical)
                    + this.mItemMarginVerticalAverage;
            int right = left + childWidth;
            int bottom = top + childHeight;
            getChildAt(i).layout(left, top, right, bottom);
        }
    }

    protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3,
            int paramInt4) {
        WindowManager localWindowManager = (WindowManager) this.mContext
                .getSystemService("window");
        this.mHeight = localWindowManager.getDefaultDisplay().getHeight();
        this.mWidth = localWindowManager.getDefaultDisplay().getWidth();
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    /**
     * Interface for a callback when the item has been touched.
     */
    public interface OnTabTouchedListener {
        public boolean onItemTouch(int position, View v, MotionEvent event);
    }

    /**
     * Tab touch
     * 
     * @param listener
     */
    public void setOnTabTouchedListener(OnTabTouchedListener listener) {
        mTabTouchedListener = listener;
    }

    /**
     * touch events
     */
    private final View.OnTouchListener mItemTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TabView tabView = (TabView) v;
            final int newTouched = tabView.mIndex;
            return mTabTouchedListener.onItemTouch(newTouched, v, event);
        }
    };

    /**
     * 初始化面板上的元素
     * 
     * @param tabItems
     */
    public void setIndicator(List<Map<String, String>> tabItems) {
        if (tabItems == null || tabItems.size() <= 0) {
            return;
        }

        this.tabItems = tabItems;

        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        removeAllViews();
        final int count = tabItems.size();

        for (int i = 0; i < count; i++) {

            // indicator title
            CharSequence title = tabItems.get(i).get("item_text");
            if (title == null) {
                title = EMPTY_TITLE;
            }

            // indicator icon
            int iconResId = Integer.parseInt(tabItems.get(i).get("item_image"));

            // addTab(i, title, iconResId);

            final TabView tabView = new TabView(getContext());
            tabView.mIndex = i;
            tabView.setOnTouchListener(mItemTouchListener);
            tabView.setText(title);

            if (iconResId > 0) {
                tabView.setIcon(iconResId);
            }

            addView(tabView);

        }

        requestLayout();
    }

    // ---------------------------------------------------
    // ## Custom View
    // ---------------------------------------------------
    private class TabView extends LinearLayout {
        private int mIndex;
        private ImageView mImageView;
        private TextView mTextView;

        public TabView(Context context) {
            super(context, null);
            this.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            View view;
            view = View.inflate(context, R.layout.customize_board_item, null);
            // title
            mTextView = (TextView) view.findViewById(R.id.customize_item_text);
            // icon
            mImageView = (ImageView) view
                    .findViewById(R.id.customize_item_image);
            this.addView(view);
        }

        public void setText(CharSequence text) {
            mTextView.setText(text);
        }

        public void setIcon(int resId) {
            if (resId > 0) {
                mImageView.setImageResource(resId);
            }
        }
    }
}
