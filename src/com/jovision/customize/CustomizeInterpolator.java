package com.jovision.customize;

import android.view.animation.Interpolator;

/**
 * 自定义插值器-用于Tab indicator中间的Plus Tab按钮对应的界面
 * 
 */
public class CustomizeInterpolator implements Interpolator {
    private float a;
    private float b = 0.0F;
    private float c;
    private float d = 1.0F;
    private float e;
    private float f;
    private float g;
    private float h;

    public CustomizeInterpolator(float paramFloat1, float paramFloat2,
            float paramFloat3, float paramFloat4) {
        this.e = paramFloat1;
        this.f = paramFloat2;
        this.g = paramFloat3;
        this.h = paramFloat4;
    }

    public float getInterpolation(float paramFloat) {
        float f1 = 1.0F - paramFloat;
        float x = (f1 * (f1 * f1) * this.a + paramFloat * (f1 * (3.0F * f1))
                * this.e + paramFloat * (paramFloat * (3.0F * f1)) * this.g + paramFloat
                * (paramFloat * paramFloat) * this.c);
        float y = f1 * (f1 * f1) * this.b + paramFloat * (f1 * (3.0F * f1))
                * this.f + paramFloat * (paramFloat * (3.0F * f1)) * this.h
                + paramFloat * (paramFloat * paramFloat) * this.d;
        return y;
    }
}