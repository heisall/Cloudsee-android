
package com.jovision.customize;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.customize.CustomizePageView.OnTabTouchedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 中间加号定制面板
 */
public class CustomizeBoard extends PopupWindow implements OnClickListener,
        OnTabTouchedListener {

    protected static final String TAG = "CustomizeBoard";
    private Activity mActivity;
    private ImageView mClose;
    private LinearLayout mPanelHolder;
    private CustomizePageView mCustomizePageView;

    // -----------------------------------------------
    private String menuText[] = new String[] {
            "qq1", "qq2", "qq3", "qq4"
    };
    private int[] menuImage = new int[] {
            R.drawable.tabbar_compose_camera,
            R.drawable.tabbar_compose_idea, R.drawable.tabbar_compose_photo,
            R.drawable.tabbar_compose_weibo
    };
    List<Map<String, String>> list = new ArrayList<Map<String, String>>();

    public CustomizeBoard(Activity activity) {
        super(activity);
        this.mActivity = activity;

        initDatas();

        // 初始化自定义分享面板
        initViews(activity);

        initEvents();
    }

    private void initDatas() {
        for (int i = 0, length = menuImage.length; i < length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("item_image", String.valueOf(menuImage[i]));
            map.put("item_text", menuText[i]);
            list.add(map);
        }
    }

    @SuppressWarnings("deprecation")
    private void initViews(Context context) {
        View rootView = LayoutInflater.from(context).inflate(
                R.layout.customize_board, null);
        setContentView(rootView);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.CustomizeBoardWindowAnim);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);

        mClose = (ImageView) rootView.findViewById(R.id.btn_cancel);
        mPanelHolder = (LinearLayout) rootView.findViewById(R.id.panel_holder);
        mCustomizePageView = new CustomizePageView(mActivity);
        mCustomizePageView.setIndicator(list);
        mCustomizePageView.setVisibility(View.GONE);

        ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mPanelHolder.addView(mCustomizePageView, localLayoutParams);

    }

    private void initEvents() {
        mClose.setOnClickListener(this);
        mCustomizePageView.setOnTabTouchedListener(this);

        mCustomizePageView.setVisibility(View.VISIBLE);// 显示面板上的元素
        mPanelHolder.setVisibility(View.VISIBLE);// 显示面板布局
        mPanelHolder.startAnimation(CustomizeAnimation.enterAnimation(0L));// 开始动画
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_cancel:
                // 关闭popupwindow
                CustomizeBoard.this.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void dismiss() {
        // 加载退出动画
        final Animation exit = CustomizeAnimation.exitAnimation(0L);
        exit.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 关闭popupwindow
                mPanelHolder.post(new Runnable() {
                    @Override
                    public void run() {
                        CustomizeBoard.super.dismiss();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

        });
        mPanelHolder.setVisibility(View.GONE);// 取出布局
        mPanelHolder.startAnimation(exit);// 开始退出动画
    }

    // 元素是否已经被放大
    private boolean mIsScaleOut;

    /**
     * 处理自定义面板上的元素的onTouch事件
     */
    @Override
    public boolean onItemTouch(int position, View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.startAnimation(CustomizeAnimation.ScaleOutAnimation());
                mIsScaleOut = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getX();
                int y = (int) event.getY();
                if ((x > 0) && (x < v.getWidth()) && (y > 0) && (y < v.getHeight())) {
                    if (!mIsScaleOut) {
                        v.startAnimation(CustomizeAnimation.ScaleOutAnimation());
                        mIsScaleOut = true;
                    }
                } else {
                    if (mIsScaleOut) {
                        v.startAnimation(CustomizeAnimation.ScaleInAnimation());
                        mIsScaleOut = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                int i = (int) event.getX();
                int j = (int) event.getY();
                if ((i <= 0) || (i >= v.getWidth()) || (j <= 0)
                        || (j >= v.getHeight())) {
                } else {
                    v.startAnimation(CustomizeAnimation.ScaleInAnimation());
                    Toast.makeText(mActivity, "position->" + position,
                            Toast.LENGTH_SHORT).show();
                    clickItemEvents(position);
                }
                break;
        }

        return true;
    }

    /**
     * 处理自定义面板上的元素的click事件
     */
    private void clickItemEvents(int position) {
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            default:

        }
    }

}
