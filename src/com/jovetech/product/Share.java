
package com.jovetech.product;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;

import com.jovision.views.CustomShareBoard;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 分享类 有分享的界面必须实现IShare接口
 */
public class Share implements IShare {
    private static Share single = null;
    private Activity mActivity;
    private IShare mShare;
    /** umeng share **/
    private static final String DESCRIPTOR = "com.umeng.share";
    private final UMSocialService mController = UMServiceFactory
            .getUMSocialService(DESCRIPTOR);

    private Share() {
    }

    public static Share getInstance(Activity activity) {
        if (single == null) {
            single = new Share();
        }
        single.mActivity = activity;
        single.mShare = (IShare) single.mActivity;
        return single;
    }

    /**
     * 获取分享的controller
     * 
     * @return mController
     */
    public UMSocialService getShareController() {
        return mController;
    }

    /**
     * @功能描述 : 打开自定义的分享面板</br>
     * @return
     */
    public void openSharePane() {
        CustomShareBoard shareBoard = new CustomShareBoard(mActivity);
        shareBoard.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.BOTTOM,
                0, 0);
    }

    /**
     * @功能描述 : 配置分享平台参数</br>
     * @return
     */
    public void configPlatforms() {
        // 添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }

    /**
     * @功能描述 : 添加微信平台分享</br>
     * @return
     */
    private void addWXPlatform() {
        // 微信开发平台注册应用的AppID
        String appId = "wx21141328bd509074";
        String appSecret = "f9172781187afc8853803f681ab668bf";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mActivity, appId, appSecret);
        wxHandler.addToSocialSDK();
        // 设置不显示提示：大于32k 压缩图片
        wxHandler.showCompressToast(false);

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mActivity, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 设置不显示提示：大于32k 压缩图片
        wxCircleHandler.showCompressToast(false);
    }

    /**
     * 分享配置
     */
    private void addShareConfig() {
        // 关闭默认的Toast提示,回避Toast重复问题
        mController.getConfig().closeToast();
        /*
         * 关闭新浪微博分享时的获取地理位置功能 原因是友盟不支持多语言,定位失败的Toast被写死在了代码里
         */
        mController.getConfig().setDefaultShareLocation(false);
    }

    /**
     * 设置分享内容
     */
    @Override
    public void setShareContent() {
        addShareConfig();
        mShare.setShareContent();
    }

    /**
     * 使用SSO授权必须进行callback
     * 
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void setAuthorizeCallBack(int requestCode, int resultCode, Intent data) {
        /** 使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
                requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
