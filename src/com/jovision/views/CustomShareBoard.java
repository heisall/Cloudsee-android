package com.jovision.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.commons.MyLog;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;

/**
 * 由于友盟分享Android版本目前没有支持国际化，所以自定义分享面板
 */
public class CustomShareBoard extends PopupWindow implements OnClickListener {

    private static final String DESCRIPTOR = "com.umeng.share";
    protected static final String TAG = "CustomShareBoard";
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(DESCRIPTOR);
    private Activity mActivity;

    public CustomShareBoard(Activity activity) {
        super(activity);
        this.mActivity = activity;
        initView(activity);
    }

    @SuppressWarnings("deprecation")
    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(
                R.layout.custom_share_board, null);
        rootView.findViewById(R.id.wechat).setOnClickListener(this);
        rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
        rootView.findViewById(R.id.sina).setOnClickListener(this);
        // 分享窗口中半透明view部分
        rootView.findViewById(R.id.share_overlay).setOnClickListener(this);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        setContentView(rootView);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.wechat:
            performShare(SHARE_MEDIA.WEIXIN);
            break;
        case R.id.wechat_circle:
            performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
            break;
        case R.id.sina:
            performShare(SHARE_MEDIA.SINA);
            break;
        case R.id.share_overlay:
        case R.id.btn_cancel:
            break;
        default:
            break;
        }
        // 关闭popupwindow
        this.dismiss();
    }

    private void performShare(SHARE_MEDIA platform) {
        mController.postShare(mActivity, platform, new SnsPostListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int stCode,
                    SocializeEntity entity) {
                if (stCode == StatusCode.ST_CODE_SUCCESSED) {
                    Toast.makeText(mActivity,
                            R.string.umeng_socialize_share_success,
                            Toast.LENGTH_SHORT).show();
                } else if (stCode == StatusCode.ST_CODE_ERROR_CANCEL) {
                    Toast.makeText(mActivity,
                            R.string.umeng_socialize_share_cancel,
                            Toast.LENGTH_SHORT).show();
                } else if (stCode == StatusCode.ST_CODE_ACCESS_EXPIRED
                        || stCode == StatusCode.ST_CODE_ACCESS_EXPIRED2) {
                    Toast.makeText(mActivity,
                            R.string.umeng_socialize_access_expired,
                            Toast.LENGTH_SHORT).show();
                    MyLog.v(TAG, "share failed, access expired. error code:"
                            + stCode);
                } else {
                    Toast.makeText(mActivity,
                            R.string.umeng_socialize_share_failed,
                            Toast.LENGTH_SHORT).show();
                    MyLog.v(TAG, "share failed, error code:" + stCode);
                }
            }
        });
    }

}