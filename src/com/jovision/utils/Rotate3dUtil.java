package com.jovision.utils;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

<<<<<<< HEAD
public class Rotate3dUtil extends Animation{  
	
		private final float mFromDegrees;  
	    private final float mToDegrees;  
	    private final float mCenterX;  
	    private final float mCenterY;  
	    private final float mDepthZ;  
	    private final boolean mReverse;  
	    private Camera mCamera;  
	    public Rotate3dUtil(float fromDegrees, float toDegrees,  
	            float centerX, float centerY, float depthZ, boolean reverse) {  
	        mFromDegrees = fromDegrees;  
	        mToDegrees = toDegrees;  
	        mCenterX = centerX;  
	        mCenterY = centerY;  
	        mDepthZ = depthZ;  
	        mReverse = reverse;  
	    }  
	    @Override  
	    public void initialize(int width, int height, int parentWidth, int parentHeight) {  
	        super.initialize(width, height, parentWidth, parentHeight);  
	        mCamera = new Camera();  
	    }  
	    @Override  
	    protected void applyTransformation(float interpolatedTime, Transformation t) {  
	        final float fromDegrees = mFromDegrees;  
	        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);  
	        final float centerX = mCenterX;  
	        final float centerY = mCenterY;  
	        final Camera camera = mCamera;  
	        final Matrix matrix = t.getMatrix();  
	        camera.save();  
	        if (mReverse) {  
	            camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);  
	        } else {  
	            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));  
	        }  
	        //设置绕Y轴旋转
	        camera.rotateY(degrees);  
	        //设置canera作用矩阵
	        camera.getMatrix(matrix);  
	        camera.restore();  
	        
	        //设置翻转中心点
	        matrix.preTranslate(-centerX, -centerY);  
	        matrix.postTranslate(centerX, centerY);  
	    }  
	} 
=======
public class Rotate3dUtil extends Animation {
	private final float mFromDegrees;
	private final float mToDegrees;
	private final float mCenterX;
	private final float mCenterY;
	private final float mDepthZ;
	private final boolean mReverse;
	private Camera mCamera;

	public Rotate3dUtil(float fromDegrees, float toDegrees, float centerX,
			float centerY, float depthZ, boolean reverse) {
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
		mDepthZ = depthZ;
		mReverse = reverse;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float fromDegrees = mFromDegrees;
		float degrees = fromDegrees
				+ ((mToDegrees - fromDegrees) * interpolatedTime);
		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;
		final Matrix matrix = t.getMatrix();
		camera.save();
		if (mReverse) {
			camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
		} else {
			camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
		}
		camera.rotateY(degrees);
		camera.getMatrix(matrix);
		camera.restore();

		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}
>>>>>>> 8d1dec862944e5c3a6af3797113a71359fa8a31f
