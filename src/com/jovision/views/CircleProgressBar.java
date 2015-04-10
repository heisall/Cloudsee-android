package com.jovision.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * Copyright (c) 2012 All rights reserved 
 * ProgressBar
 */
public class CircleProgressBar extends View {

	private int progress = 0;
	private int max = 100;

	private Paint pathPaint = null;

	private Paint fillArcPaint = null;

	private RectF oval;

//	private int[] arcColors = new int[] { 0xFF02C016, 0xFF3DF346, 0xFF40F1D5,
//			0xFF02C016 };
//  private int pathColor = 0xFFF0EEDF;
//  private int pathBorderColor = 0xFFD2D1C4;	
	private int[] shadowsColors = new int[] { 0xFF111111, 0x00AAAAAA,
			0x00AAAAAA };
    private int pathColor = 0xfff8f6f6;//中间
    private int pathBorderColor = 0xffe8e8e8; //描边
    private int[] arcColors = new int[] { 0xff7abf66, 0xff7abf66, 0xff7abf66,
            0xff7abf66 };
    
	private int pathWidth = 35;

	/** The width. */
	private int width;

	/** The height. */
	private int height;

	private int radius = 120;
	private EmbossMaskFilter emboss = null;
	float[] direction = new float[] { 1, 1, 1 };
	float light = 0.4f;
	float specular = 6;
	float blur = 3.5f;

	private BlurMaskFilter mBlur = null;

	private OnProgressListener mAbOnProgressListener = null;

	private boolean reset = false;

	public CircleProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		pathPaint = new Paint();
		pathPaint.setAntiAlias(true);
		pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		pathPaint.setStyle(Paint.Style.STROKE);
		pathPaint.setDither(true);
		pathPaint.setStrokeJoin(Paint.Join.ROUND);

		fillArcPaint = new Paint();
		fillArcPaint.setAntiAlias(true);
		fillArcPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		fillArcPaint.setStyle(Paint.Style.STROKE);
		fillArcPaint.setDither(true);
		fillArcPaint.setStrokeJoin(Paint.Join.ROUND);

		oval = new RectF();
		emboss = new EmbossMaskFilter(direction, light, specular, blur);
		mBlur = new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (reset) {
			canvas.drawColor(Color.TRANSPARENT);
			reset = false;
		}
		this.width = getMeasuredWidth();
		this.height = getMeasuredHeight();
		this.radius = getMeasuredWidth() / 2 - pathWidth;

		pathPaint.setColor(pathColor);
		pathPaint.setStrokeWidth(pathWidth);
		pathPaint.setMaskFilter(emboss);

		canvas.drawCircle(this.width / 2, this.height / 2, radius, pathPaint);

		pathPaint.setStrokeWidth(0.5f);
		pathPaint.setColor(pathBorderColor);
		canvas.drawCircle(this.width / 2, this.height / 2, radius + pathWidth
				/ 2 + 0.5f, pathPaint);
		canvas.drawCircle(this.width / 2, this.height / 2, radius - pathWidth
				/ 2 - 0.5f, pathPaint);

		/*
		 * int[] gradientColors = new int[3]; gradientColors[0] = Color.GREEN;
		 * gradientColors[1] = Color.YELLOW; gradientColors[2] = Color.RED;
		 * float[] gradientPositions = new float[3]; gradientPositions[0] =
		 * 0.0f; gradientPositions[1] = 0.5f; gradientPositions[2] = 1.0f;
		 * 
		 * 
		 * RadialGradient(this.width/2,this.height/2, radius, gradientColors,
		 * gradientPositions, TileMode.CLAMP);
		 * 
		 * paint1.setShader(radialGradientShader);
		 */

		SweepGradient sweepGradient = new SweepGradient(this.width / 2,
				this.height / 2, arcColors, null);
		fillArcPaint.setShader(sweepGradient);
		fillArcPaint.setMaskFilter(mBlur);
		fillArcPaint.setStrokeCap(Paint.Cap.ROUND);

		fillArcPaint.setStrokeWidth(pathWidth);
		oval.set(this.width / 2 - radius, this.height / 2 - radius, this.width
				/ 2 + radius, this.height / 2 + radius);
		canvas.drawArc(oval, -90, ((float) progress / max) * 360, false,
				fillArcPaint);

	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		this.invalidate();
		if (this.mAbOnProgressListener != null) {
			if (this.max <= this.progress) {
				this.mAbOnProgressListener.onComplete(progress);
			} else {
				this.mAbOnProgressListener.onProgress(progress);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int height = View.MeasureSpec.getSize(heightMeasureSpec);
		int width = View.MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(width, height);
	}

	public OnProgressListener getOnProgressListener() {
		return mAbOnProgressListener;
	}

	public void setOnProgressListener(OnProgressListener mOnProgressListener) {
		this.mAbOnProgressListener = mOnProgressListener;
	}

	public void reset() {
		reset = true;
		this.progress = 0;
		this.invalidate();
	}

}
