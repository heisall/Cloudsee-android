
package com.jovision.customize;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 自定义动画
 */
public class CustomizeAnimation {
    /**
     * Plus Tab按下后页面进入动画
     * 
     * @param offset 动画之间的间隔
     * @return 动画集
     */
    public static AnimationSet enterAnimation(long offset) {
        AnimationSet localAnimationSet = new AnimationSet(false);
        TranslateAnimation localTranslateAnimation = new TranslateAnimation(1,
                0.0F, 1, 0.0F, 2, 1.0F, 1, 0.0F);
        localTranslateAnimation.setDuration(400L);
        localTranslateAnimation.setInterpolator(new CustomizeInterpolator(
                0.15F, 1.24F, 0.55F, 1.0F));
        localTranslateAnimation.setStartOffset(offset);
        AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.8F, 1.0F);
        localAlphaAnimation.setDuration(200L);
        localAlphaAnimation.setInterpolator(new CustomizeInterpolator(0.0F,
                0.0F, 0.58F, 1.0F));
        localAlphaAnimation.setStartOffset(offset);
        localAnimationSet.addAnimation(localTranslateAnimation);
        localAnimationSet.addAnimation(localAlphaAnimation);
        localAnimationSet.setFillAfter(true);
        return localAnimationSet;
    }

    /**
     * Plus 页面关闭时的动画
     * 
     * @param offset 动画之间的间隔
     * @return 动画集
     */
    public static AnimationSet exitAnimation(long offset) {
        AnimationSet localAnimationSet = new AnimationSet(false);
        TranslateAnimation localTranslateAnimation = new TranslateAnimation(1,
                0.0F, 1, 0.0F, 1, 0.0F, 2, 1.0F);
        localTranslateAnimation.setDuration(300L);
        localTranslateAnimation.setInterpolator(new CustomizeInterpolator(0.7F,
                0.0F, 1.0F, 1.0F));
        localTranslateAnimation.setStartOffset(offset);
        AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.0F);
        localAlphaAnimation.setDuration(200L);
        localAlphaAnimation.setStartOffset(offset);
        localAnimationSet.addAnimation(localTranslateAnimation);
        localAnimationSet.addAnimation(localAlphaAnimation);
        localAnimationSet.setFillAfter(true);
        return localAnimationSet;
    }

    /**
     * 放大动画
     * 
     * @return
     */
    public static AnimationSet ScaleOutAnimation() {
        AnimationSet localAnimationSet = new AnimationSet(true);
        ScaleAnimation localScaleAnimation = new ScaleAnimation(1.0F, 1.2F,
                1.0F, 1.2F, 1, 0.5F, 1, 0.5F);
        localScaleAnimation.setDuration(100L);
        localAnimationSet.addAnimation(localScaleAnimation);
        localAnimationSet.setFillAfter(true);
        return localAnimationSet;
    }

    /**
     * 缩小动画
     * 
     * @return
     */
    public static AnimationSet ScaleInAnimation() {
        AnimationSet localAnimationSet = new AnimationSet(true);
        ScaleAnimation localScaleAnimation = new ScaleAnimation(1.2F, 1.0F,
                1.2F, 1.0F, 1, 0.5F, 1, 0.5F);
        localScaleAnimation.setDuration(100L);
        localAnimationSet.addAnimation(localScaleAnimation);
        localAnimationSet.setFillAfter(true);
        return localAnimationSet;
    }

    /**
     * 渐变动画
     */
    public static Animation GradualChangeAnimation() {
        AlphaAnimation localAlphaAnimation = new AlphaAnimation(1.0F, 0.5F);
        localAlphaAnimation.setDuration(150L);
        localAlphaAnimation.setInterpolator(new AccelerateInterpolator());
        localAlphaAnimation.setFillAfter(true);
        return localAlphaAnimation;
    }
}
