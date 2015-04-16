/**
 * 视频广场评论小图点击后显示的大图
 */

package com.jovision.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.utils.ImageUtil;
import com.jovision.views.TouchImageView;

public class JVNetImageViewActivity extends BaseActivity {
    Bitmap imageBitmap = null;
    String imageUrl = "";
    TouchImageView imageView;

    @Override
    public void onHandler(int what, int arg1, int arg2, Object obj) {
        switch (what) {
            case Consts.WHAT_SHOW_PRO: {
                createDialog("", false);
                break;
            }
            case Consts.WHAT_DISMISS_PROGRESS: {
                if (null != imageBitmap) {
                    imageView.setImageBitmap(imageBitmap);
                }
                dismissDialog();
                break;
            }
        }
    }

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {

    }

    @Override
    protected void initSettings() {
        Intent intent = getIntent();
        if (null != intent) {
            imageUrl = intent.getStringExtra("ImageUrl");
        }
    }

    @Override
    protected void initUi() {
        setContentView(R.layout.net_imageview_layout);
        leftBtn = (Button) findViewById(R.id.btn_left);
        currentMenu = (TextView) findViewById(R.id.currentmenu);
        rightBtn = (Button) findViewById(R.id.btn_right);
        currentMenu.setText("");
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(mOnClickListener);
        rightBtn.setVisibility(View.GONE);
        imageView = (TouchImageView) findViewById(R.id.img);
        imageView.setMaxZoom(4f);
        if (null != imageUrl) {
            new GetImageFromNetThread().start();
        }
    }

    /**
     * 从网上获取图片线程
     * 
     * @author Administrator
     */
    class GetImageFromNetThread extends Thread {

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage(Consts.WHAT_SHOW_PRO));
            imageBitmap = ImageUtil.getBitmap(imageUrl);
            handler.sendMessage(handler.obtainMessage(Consts.WHAT_DISMISS_PROGRESS));
            super.run();
        }

    }

    OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_left:
                    backMethod();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    protected void saveSettings() {

    }

    @Override
    protected void freeMe() {

    }

    @Override
    public void onBackPressed() {
        backMethod();
        super.onBackPressed();
    }

    protected void backMethod() {
        try {
            if (null != imageBitmap) {
                if (null != ImageUtil.imagesCache) {
                    ImageUtil.imagesCache.remove(imageUrl);
                }
                imageBitmap.recycle();
                imageBitmap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JVNetImageViewActivity.this.finish();
    }

}
