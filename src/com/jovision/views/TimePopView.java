
package com.jovision.views;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.utils.WheelMain;

public class TimePopView extends PopupWindow {

    private View mMenuView;
    private RelativeLayout pop_outside;
    private View timePicker1;
    public WheelMain wheelMain;
    private TextView save;
    private TextView quit;
    public ImageView relativeOne_img;
    public ImageView relativeTwo_img;
    public ImageView relativeThree_img;
    public RelativeLayout relativeOne;
    public RelativeLayout relativeTwo;
    public RelativeLayout relativeThree;
    public TextView Currenttype;

    public TimePopView(Activity context, OnClickListener itemsOnClick, int type) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (0 == type) {
            mMenuView = inflater.inflate(R.layout.dialog_time, null);
            pop_outside = (RelativeLayout) mMenuView.findViewById(R.id.pop_outside);
            timePicker1 = (LinearLayout) mMenuView.findViewById(R.id.timePicker1);
            wheelMain = new WheelMain(timePicker1);
            save = (TextView) mMenuView.findViewById(R.id.save);
            quit = (TextView) mMenuView.findViewById(R.id.quit);
            wheelMain.initDateTimePicker();
        } else if (1 == type) {
            mMenuView = inflater.inflate(R.layout.dialog_date, null);
            pop_outside = (RelativeLayout) mMenuView.findViewById(R.id.pop_outside);

            save = (TextView) mMenuView.findViewById(R.id.save);
            quit = (TextView) mMenuView.findViewById(R.id.quit);
            relativeOne = (RelativeLayout) mMenuView.findViewById(R.id.relativeone);
            relativeTwo = (RelativeLayout) mMenuView.findViewById(R.id.relativetwo);
            relativeThree = (RelativeLayout) mMenuView.findViewById(R.id.relativethree);
            relativeOne_img = (ImageView) mMenuView.findViewById(R.id.relativeone_img);
            relativeTwo_img = (ImageView) mMenuView.findViewById(R.id.relativetwo_img);
            relativeThree_img = (ImageView) mMenuView.findViewById(R.id.relativethree_img);
            Currenttype = (TextView) mMenuView.findViewById(R.id.currenttype);

            relativeOne.setOnClickListener(itemsOnClick);
            relativeTwo.setOnClickListener(itemsOnClick);
            relativeThree.setOnClickListener(itemsOnClick);
        }

        save.setOnClickListener(itemsOnClick);
        quit.setOnClickListener(itemsOnClick);
        pop_outside.setOnClickListener(itemsOnClick);
        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popupAnimation);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
    }

}
