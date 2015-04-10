
package com.jovision.customize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.MainApplication;
import com.jovision.activities.AlarmInfoActivity;
import com.jovision.activities.AlarmSettingsActivity;
import com.jovision.activities.BaseActivity;
import com.jovision.activities.JVMoreFragment.OnFuncActionListener;
import com.jovision.activities.JVWebViewActivity;
import com.jovision.commons.GetDemoTask;
import com.jovision.commons.MyLog;
import com.jovision.commons.MySharedPreference;
import com.jovision.customize.CustomizePageView.OnTabTouchedListener;
import com.jovision.utils.ConfigUtil;

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
    private MainApplication mApplication;
    private BaseActivity mActivity;
    private ImageView mClose;
    private LinearLayout mPanelHolder;
    private CustomizePageView mCustomizePageView;

    // 功能菜单项数组
    private String menuText[];
    // 功能菜单Tag数组
    private String menuTag[];
    // 功能菜单列表
    private List<Map<String, String>> menuList = new ArrayList<Map<String, String>>();

    private OnFuncActionListener mListener;
    // 布局中子元素的动画延时时间
    private float mAnimationDelay = 0.1F;

    public CustomizeBoard(Activity activity) {
        super(activity);
        mActivity = (BaseActivity) activity;
        mApplication = (MainApplication) mActivity.getApplication();

        try {
            mListener = (OnFuncActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "must implement OnFuncEnabledListener");
        }

        initDatas();

        // 初始化自定义分享面板
        initViews(activity);

        initEvents();
    }

    private void initDatas() {
        // 初始化菜单数组
        menuText = mActivity.getResources().getStringArray(
                R.array.array_customize_board);
        menuTag = mActivity.getResources().getStringArray(
                R.array.array_customize_item_tag);
        for (int i = 0, length = menuText.length; i < length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("item_tag", menuTag[i]);
            map.put("item_text", menuText[i]);
            // 获取图片资源
            int iconResId = getItemImageByTag(menuTag[i]);
            map.put("item_image", String.valueOf(iconResId));
            menuList.add(map);
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
        mCustomizePageView.setIndicator(menuList);
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
        // mPanelHolder.startAnimation(CustomizeAnimation.enterAnimation(0L));
        // 开始动画
        // 得到一个LayoutAnimationController对象；
        LayoutAnimationController lac = new LayoutAnimationController(
                CustomizeAnimation.enterAnimation(0L));
        // 设置控件显示间隔时间；
        lac.setDelay(mAnimationDelay);
        // 为ListView设置LayoutAnimationController属性；
        mCustomizePageView.setLayoutAnimation(lac);
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
        // // 加载退出动画
        // final Animation exit = CustomizeAnimation.exitAnimation(0L);
        // exit.setAnimationListener(new AnimationListener() {
        //
        // @Override
        // public void onAnimationStart(Animation animation) {
        // }
        //
        // @Override
        // public void onAnimationEnd(Animation animation) {
        // // 关闭popupwindow
        // mPanelHolder.post(new Runnable() {
        // @Override
        // public void run() {
        // CustomizeBoard.super.dismiss();
        // }
        // });
        // }
        //
        // @Override
        // public void onAnimationRepeat(Animation animation) {
        // }
        //
        // });
        // mPanelHolder.setVisibility(View.GONE);// 取出布局
        // mPanelHolder.startAnimation(exit);// 开始退出动画
        // 由于布局动画只在显示的时候加载,所以先隐藏再显示
        mCustomizePageView.setVisibility(View.GONE);
        mCustomizePageView.setVisibility(View.VISIBLE);
        // 开始动画
        // 得到一个LayoutAnimationController对象；
        LayoutAnimationController lac = new LayoutAnimationController(
                CustomizeAnimation.exitAnimation(0L));
        // 设置控件显示的顺序；
        lac.setOrder(LayoutAnimationController.ORDER_REVERSE);
        // 设置控件显示间隔时间；
        lac.setDelay(0.1F);
        // 为ListView设置LayoutAnimationController属性；
        mCustomizePageView.setLayoutAnimation(lac);
        mCustomizePageView.setLayoutAnimationListener(new AnimationListener() {

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
    }

    // 元素是否已经被放大
    private boolean mIsScaleOut;

    /**
     * 处理自定义面板上的元素的onTouch事件
     */
    @Override
    public boolean onItemTouch(int position, String tag, View v, MotionEvent event) {
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
                    // 点击事件
                    clickItemEvents(tag);
                }
                break;
        }

        return true;
    }

    /**
     * 通过元素的标记获取相应的图片
     * 
     * @param tag 标记
     * @return iconResId 图片资源
     */
    private int getItemImageByTag(String tag) {
        int iconResId = R.drawable.customize_item_no_image;
        if (tag.equals(Consts.MORE_ALARMSWITCH)) {// 报警设置
            iconResId = R.drawable.tabbar_compose_camera;
        } else if (tag.equals(Consts.MORE_ALARMMSG)) {// 报警消息
            iconResId = R.drawable.tabbar_compose_weibo;
        } else if (tag.equals(Consts.MORE_GCSURL)) {// 工程商入驻
            iconResId = R.drawable.tabbar_compose_idea;
        } else if (tag.equals("unknown")) {// 未知
            iconResId = R.drawable.tabbar_compose_photo;
        }

        return iconResId;
    }

    /**
     * 处理自定义面板上的元素的click事件
     */
    private void clickItemEvents(String tag) {
        if (tag.equals(Consts.MORE_ALARMSWITCH)) {// 报警设置
            alarmSwitch();
        } else if (tag.equals(Consts.MORE_ALARMMSG)) {// 报警消息
            alarmMsg();
        } else if (tag.equals(Consts.MORE_GCSURL)) {// 工程商入驻
            gcsurl();
        } else if (tag.equals("unknown")) {// 未知
            Toast.makeText(mActivity, tag,
                    Toast.LENGTH_SHORT).show();
        }
        // 关闭面板
        CustomizeBoard.super.dismiss();
    }

    // ------------------------------------------------------
    // ## 面板上元素的click事件对应的操作
    // ## 操作代码来自JVMoreFragment的listViewClick()
    // ------------------------------------------------------
    /**
     * 报警设置
     */
    private void alarmSwitch() {
        boolean localFlag = Boolean.valueOf(mActivity.statusHashMap
                .get(Consts.LOCAL_LOGIN));
        if (localFlag)// 本地登录
        {
            mActivity.showTextToast(R.string.more_nologin);
        } else {
            Intent intent = new Intent(mActivity,
                    AlarmSettingsActivity.class);
            mActivity.startActivity(intent);
        }
    }

    /**
     * 报警消息
     */
    private void alarmMsg() {
        boolean localFlag = Boolean.valueOf(mActivity.statusHashMap
                .get(Consts.LOCAL_LOGIN));
        if (localFlag) {
            mActivity.showTextToast(R.string.more_nologin);
        } else {
            if (!ConfigUtil.isConnected(mActivity)) {
                mActivity.alertNetDialog();
            } else {
                mApplication.setNewPushCnt(0);
                Intent intent = new Intent(mActivity,
                        AlarmInfoActivity.class);
                mActivity.startActivity(intent);
            }
        }
    }

    /**
     * 工程商入驻
     */
    private void gcsurl() {
        if (!MySharedPreference
                .getBoolean(Consts.MORE_GCSURL)) {
            MySharedPreference.putBoolean(
                    Consts.MORE_GCSURL, true);
            mListener.OnFuncEnabled(0, 1);
        }
        if (!ConfigUtil.isConnected(mActivity)) {
            mActivity.alertNetDialog();
        } else {
            if (null != ((BaseActivity) mActivity).statusHashMap
                    .get(Consts.MORE_GCSURL)) {
                Intent intentAD0 = new Intent(mActivity,
                        JVWebViewActivity.class);
                intentAD0
                        .putExtra(
                                "URL",
                                ((BaseActivity) mActivity).statusHashMap
                                        .get(Consts.MORE_GCSURL));
                intentAD0.putExtra("title", -2);
                mActivity.startActivity(intentAD0);
            } else {
                if ("false".equals(mActivity.statusHashMap
                        .get(Consts.KEY_INIT_ACCOUNT_SDK))) {
                    MyLog.e("Login", "初始化账号SDK失败");
                    ConfigUtil
                            .initAccountSDK(((MainApplication) mActivity
                                    .getApplication()));// 初始化账号SDK
                }
                GetDemoTask UrlTask = new GetDemoTask(
                        mActivity);
                String[] demoParams = new String[3];
                demoParams[1] = "0";
                UrlTask.execute(demoParams);
            }
        }
    }

}
