
package com.jovision.customize;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovetech.product.ITabItem;

import java.util.List;

/**
 * 自定义带图标的TabIndicator
 */
public class CustomizeIconTabIndicator extends LinearLayout {
    /**
     * Title text used when no title is provided by the adapter.
     */
    private static final CharSequence EMPTY_TITLE = "";

    /**
     * Tabviews array
     */
    private static View[] mIndicators;

    /**
     * Interface for a callback when the selected tab has been selected.
     */
    public interface OnTabSelectedListener {
        /**
         * Callback when the selected tab has been reselected.
         * 
         * @param position Position of the current center item.
         * @param oldPosition old position
         * @param tag tab indicator tag
         * @param isCustomize is/not customize
         */
        void onTabSelected(int position, int oldPosition, char tag,
                boolean isCustomize);

        /**
         * Callback when the selected tab has been reselected.
         * 
         * @param position Position of the current center item.
         */
        void onTabReselected(int position, char tag, boolean isCustomize);
    }

    // 选项卡
    private List<ITabItem> tabItems;

    private Runnable mTabSelector;

    private final LinearLayout mTabLayout;

    private int mSelectedTabIndex;

    private OnTabSelectedListener mTabSelectedListener;

    private int mTabWidth;

    private Context mContext;

    public CustomizeIconTabIndicator(Context context) {
        this(context, null);
    }

    public CustomizeIconTabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setHorizontalScrollBarEnabled(false);

        mTabLayout = new LinearLayout(context, null);
        addView(mTabLayout, new ViewGroup.LayoutParams(MATCH_PARENT,
                MATCH_PARENT));
    }

    /**
     * 选中Tab
     * 
     * @param listener
     */
    public void setOnTabSelectedListener(OnTabSelectedListener listener) {
        mTabSelectedListener = listener;
    }

    /**
     * tab的click事件
     */
    private final View.OnClickListener mTabClickListener = new View.OnClickListener() {
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            // 具体的点击事件处理
            clickEvents(tabView);
        }
    };

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == View.MeasureSpec.EXACTLY;

        final int childCount = mTabLayout.getChildCount();

        if (childCount > 1
                && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            mTabWidth = MeasureSpec.getSize(widthMeasureSpec) / childCount;
        } else {
            mTabWidth = -1;
        }

        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex);
        }
    }

    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft()
                        - (getWidth() - tabView.getWidth()) / 2;
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    /**
     * 添加Tab到选项卡
     * 
     * @param index
     * @param tag
     * @param text
     * @param iconResId
     * @param isCustomize
     * @param viewType
     * @param info
     */
    private void addTab(int index, char tag, CharSequence text, int iconResId,
            boolean isCustomize, int viewType, CharSequence info) {
        final TabView tabView = new TabView(getContext(), viewType);
        mIndicators[index] = tabView;
        tabView.mIndex = index;
        tabView.mMark = tag;
        tabView.mIsCustomize = isCustomize;
        tabView.setOnClickListener(mTabClickListener);
        tabView.setText(text);

        // 消息
        if (!isCustomize) {
            RelativeLayout rlty = (RelativeLayout) tabView
                    .findViewById(R.id.tab_info);
            if (!info.equals(EMPTY_TITLE)) {
                rlty.setVisibility(View.VISIBLE);
                tabView.setInfoText(info);
            } else {
                rlty.setVisibility(View.GONE);
                tabView.setInfoText(EMPTY_TITLE);
            }
        }

        if (iconResId > 0) {
            tabView.setIcon(iconResId);
        }

        mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0,
                MATCH_PARENT, 1));
    }

    /**
     * 设置tab选项卡,默认初始选中位置为0
     * 
     * @param fragmentFactorys fragments factory
     */
    public void setIndicator(List<ITabItem> tabItems) {
        if (tabItems == null || tabItems.size() <= 0) {
            return;
        }

        this.tabItems = tabItems;

        notifyDataSetChanged();
    }

    /**
     * 设置tab选项卡,支持设置初始选中位置
     * 
     * @param fragmentFactorys fragments factory
     * @param initialPosition position 初始选中位置
     */
    public void setIndicator(List<ITabItem> tabItems, int initialPosition) {
        setIndicator(tabItems);
        setCurrentItem(initialPosition);
    }

    public void notifyDataSetChanged() {
        mTabLayout.removeAllViews();
        mIndicators = null;
        final int count = tabItems.size();
        mIndicators = new View[count];
        for (int i = 0; i < count; i++) {
            // indicator title
            CharSequence title = tabItems.get(i).getName(mContext);
            if (title == null) {
                title = EMPTY_TITLE;
            }

            // indicator message
            // CharSequence infoText = null;
            // if (infoText == null) {
            // infoText = EMPTY_TITLE;
            // }

            // indicator icon
            int iconResId = tabItems.get(i).getDrawable();

            // indicator layout
            int viewType = tabItems.get(i).getLayoutType();

            // tab indicator tag
            char tag = tabItems.get(i).getTag();

            // 是不是定制Tab
            boolean isCustomize = tabItems.get(i).isCustomize();

            addTab(i, tag, title, iconResId, isCustomize, viewType, EMPTY_TITLE);

        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    private void setCurrentItem(int item) {

        mSelectedTabIndex = item;

        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
            }
        }
    }

    /**
     * 跳转到某个Tabs
     * 
     * @param which 要跳转的tab的位置
     */
    public void jumpFragment(int which) {
        TabView tabView = (TabView) mIndicators[which];
        // 执行Tab的点击事件
        clickEvents(tabView);
    }

    /**
     * tab的具体click事件处理
     * 
     * @param tabView
     */
    private void clickEvents(TabView tabView) {
        final int newSelected = tabView.mIndex;
        final int oldSelected = mSelectedTabIndex;
        final boolean isCustomize = tabView.mIsCustomize;
        final char tag = tabView.mMark;
        if (oldSelected == newSelected && mTabSelectedListener != null) {
            mTabSelectedListener.onTabReselected(newSelected, tag,
                    isCustomize);
        } else {
            // 点击定制Tab后不选中
            if (!isCustomize) {
                setCurrentItem(newSelected);
            }
            mTabSelectedListener.onTabSelected(newSelected, oldSelected,
                    tag, isCustomize);
        }
    }

    // ---------------------------------------------------
    // ## Custom View
    // ---------------------------------------------------
    private class TabView extends LinearLayout {
        private int mIndex;
        private char mMark;// fragment factory的标识
        private boolean mIsCustomize;
        private ImageView mImageView, mInfoImage;
        private TextView mTextView, mInfoText;

        public TabView(Context context, int viewType) {
            super(context, null);
            this.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            View view;
            // check layout
            switch (viewType) {
                case 1:
                    view = View.inflate(context, R.layout.tab_item_customize, null);
                    break;
                case 0:
                default:
                    view = View.inflate(context, R.layout.tab_item, null);
                    // 消息
                    mInfoImage = (ImageView) view.findViewById(R.id.tab_info_img);
                    mInfoText = (TextView) view.findViewById(R.id.tab_info_text);
            }
            // indicator's title
            mTextView = (TextView) view.findViewById(R.id.tab_title);
            // indicator's icon
            mImageView = (ImageView) view.findViewById(R.id.tab_icon);
            this.addView(view);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Re-measure if we went beyond our maximum size.
            if (mTabWidth > 0) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mTabWidth,
                        MeasureSpec.EXACTLY), heightMeasureSpec);
            }
        }

        public void setText(CharSequence text) {
            mTextView.setText(text);
        }

        // 消息内容
        public void setInfoText(CharSequence text) {
            mInfoText.setText(text);
        }

        public void setIcon(int resId) {
            if (resId > 0) {
                mImageView.setImageResource(resId);
            }
        }

        // 消息背景图片//TODO
        // public void setInfoIcon(int resId) {
        // if (resId > 0) {
        // mInfoImage.setImageResource(resId);
        // }
        // }
    }

    // ---------------------------------------------------
    // ## Update Custom View's Message
    // ---------------------------------------------------
    public void updateIndicator(int which, int msgCount, boolean show,
            int count) {
        TabView tabView = (TabView) mIndicators[which];
        RelativeLayout rlty = (RelativeLayout) tabView
                .findViewById(R.id.tab_info);

        // 最后一个Tab的位置
        int lastPosition = mIndicators.length - 1;
        switch (which) {
            case 1: {// 报警消息条数,这个屏蔽掉，因为换位置了，避免出错，重新定义个值
                // tabInfoText.setText(String.valueOf(msgCount));
                //
                // if (show) {
                // tabInfo.setVisibility(View.VISIBLE);
                // } else {
                // tabInfo.setVisibility(View.GONE);
                // }
                break;
            }
            default:
                if (which == lastPosition) {// 更多功能，新
                    if (show) {
                        rlty.setVisibility(View.VISIBLE);
                        tabView.setInfoText(count + "");
                    } else {
                        rlty.setVisibility(View.GONE);
                        tabView.setInfoText(EMPTY_TITLE);
                    }
                    break;
                }
                break;
        }

    }

}
