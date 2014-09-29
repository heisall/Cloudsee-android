package com.jovision.commons;

import android.view.MotionEvent;

/**
 * 手势分发
 * 
 * @author neo
 * 
 */
public class MyGestureDispatcher {

	/** 默认方向 */
	public static final int GESTURE_TO_NULL = 0x00;

	/** 手势向左 */
	public static final int GESTURE_TO_LEFT = 0x01;
	/** 手势向上 */
	public static final int GESTURE_TO_UP = 0x02;
	/** 手势向右 */
	public static final int GESTURE_TO_RIGHT = 0x03;
	/** 手势向下 */
	public static final int GESTURE_TO_DOWN = 0x04;

	private static final int DEFAULT_BLIND_AERA_R_SQUARE = 60;

	private int currentDirection;

	private float lastDownX;
	private float lastDownY;

	private int blindAreaRSquare;

	private boolean isReported;
	private boolean ignoreReport;

	private OnGestureListener listener;

	/**
	 * 构造，传入监听器
	 * 
	 * @param listener
	 */
	public MyGestureDispatcher(OnGestureListener listener) {
		isReported = false;
		ignoreReport = false;

		currentDirection = GESTURE_TO_NULL;
		blindAreaRSquare = DEFAULT_BLIND_AERA_R_SQUARE;

		this.listener = listener;
	}

	public boolean isReported() {
		return isReported;
	}

	public void setReported(boolean isReported) {
		this.isReported = isReported;
	}

	public boolean isIgnoreReport() {
		return ignoreReport;
	}

	public void setIgnoreReport(boolean ignoreReport) {
		this.ignoreReport = ignoreReport;
	}

	public int getBlindAreaRSquare() {
		return blindAreaRSquare;
	}

	public void setBlindAreaRSquare(int blindAreaRSquare) {
		this.blindAreaRSquare = blindAreaRSquare;
	}

	/**
	 * 需要监听手势的触摸事件，某次都要调用哦
	 * 
	 * @param event
	 * @return 手势是否被判断出来并通知给监听器
	 */
	public boolean motion(MotionEvent event) {
		boolean result = false;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			if (false == ignoreReport) {
				lastDownX = event.getX();
				lastDownY = event.getY();
				isReported = false;
				currentDirection = GESTURE_TO_NULL;
			}
			break;
		}

		case MotionEvent.ACTION_UP:
			isReported = true;
			break;

		case MotionEvent.ACTION_MOVE: {
			if (isReported || ignoreReport) {
				break;
			}

			float upOffset = lastDownY - event.getY();
			float rightOffset = event.getX() - lastDownX;

			// [Neo] 盲区判断
			if ((upOffset * upOffset + rightOffset * rightOffset) < blindAreaRSquare) {
				break;
			}

			// [Neo] 点与面的关系，哇咔咔
			if (upOffset + rightOffset > 0 && upOffset - rightOffset < 0) {
				currentDirection = GESTURE_TO_RIGHT;
			} else if (upOffset + rightOffset > 0 && upOffset - rightOffset > 0) {
				currentDirection = GESTURE_TO_UP;
			} else if (upOffset + rightOffset < 0 && upOffset - rightOffset > 0) {
				currentDirection = GESTURE_TO_LEFT;
			} else if (upOffset + rightOffset < 0 && upOffset - rightOffset < 0) {
				currentDirection = GESTURE_TO_DOWN;
			}

			if (null != listener) {
				result = true;
				isReported = true;
				listener.onGesture(currentDirection);
			}

			break;
		}

		default:
			break;
		}

		return result;
	}

	/**
	 * 手势监听器
	 * 
	 * @author neo
	 * 
	 */
	public interface OnGestureListener {

		/**
		 * 手势方向事件
		 * 
		 * @param direction
		 *            方向，参考 {@link MyGestureDispatcher#GESTURE_TO_LEFT}，
		 *            {@link MyGestureDispatcher#GESTURE_TO_UP}，
		 *            {@link MyGestureDispatcher#GESTURE_TO_RIGHT}，
		 *            {@link MyGestureDispatcher#GESTURE_TO_DOWN}
		 */
		public void onGesture(int direction);
	}

}
