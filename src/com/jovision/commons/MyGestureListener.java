//package com.jovision.commons;
//
//import android.content.Context;
//import android.os.Message;
//import android.view.GestureDetector.SimpleOnGestureListener;
//import android.view.MotionEvent;
//
//import com.jovision.activities.JVPlayActivity;
//
//public class MyGestureListener extends SimpleOnGestureListener {
//
//	int MAJOR_MOVE_X = 80;
//	int MAJOR_MOVE_Y = 80;
//	private Context mContext;
//
//	public MyGestureListener(Context context) {
//		mContext = context;
//	}
//
//	@Override
//	public boolean onDown(MotionEvent e) {
//		return false;
//	}
//
//	@Override
//	public void onShowPress(MotionEvent e) {
//	}
//
//	@Override
//	public boolean onSingleTapUp(MotionEvent e) {
//		// Toast.makeText(mContext, "手抬起 ", Toast.LENGTH_SHORT).show();
//		return false;
//
//	}
//
//	// @Override
//	// public boolean onScroll(MotionEvent e1, MotionEvent e2,
//	//
//	// float distanceX, float distanceY) {
//	//
//	// Toast.makeText(mContext, "SCROLL " + e2.getAction(), Toast.LENGTH_SHORT)
//	// .show();
//	//
//	// return false;
//	//
//	// }
//
//	@Override
//	public void onLongPress(MotionEvent e) {
//		// Toast.makeText(mContext, "长按 ", Toast.LENGTH_SHORT).show();
//	}
//
//	@Override
//	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//			float velocityY) {
//		Message msg = JVPlayActivity.getInstance().playHandler.obtainMessage();
//		// 支持手势云台
//		if (BaseApp.GESPTZ) {
//			int dx = (int) (e2.getX() - e1.getX()); // 计算水平滑动的距离
//			int dy = (int) (e2.getY() - e1.getY()); // 计算垂直滑动的距离
//			if (Math.abs(dx) > MAJOR_MOVE_X) { // 降噪处理，必须有较大的动作才识别
//				if (velocityX > 0) {
//					// 向右边
//					// Toast.makeText(mContext, "向右边 ",
//					// Toast.LENGTH_SHORT).show();
//					msg.what = JVConst.GES_PTZ_RIGHT;
//
//				} else if (velocityX < 0) {
//					// 向左边
//					// Toast.makeText(mContext, "向左边",
//					// Toast.LENGTH_SHORT).show();
//					msg.what = JVConst.GES_PTZ_LEFT;
//				}
//				JVPlayActivity.getInstance().playHandler.sendMessage(msg);
//				return true;
//			} else if (Math.abs(dy) > MAJOR_MOVE_Y) { // 降噪处理，必须有较大的动作才识别
//				if (velocityY > 0) {
//					// 向下边
//					// Toast.makeText(mContext, "向下边 ",
//					// Toast.LENGTH_SHORT).show();
//					msg.what = JVConst.GES_PTZ_DOWN;
//				} else if (velocityY < 0) {
//					// 向上边
//					// Toast.makeText(mContext, "向上边 ",
//					// Toast.LENGTH_SHORT).show();
//					msg.what = JVConst.GES_PTZ_UP;
//				}
//				JVPlayActivity.getInstance().playHandler.sendMessage(msg);
//				return true; // 当然可以处理velocityY处理向上和向下的动作
//			}
//		}
//
//		return true;
//	}
//
//	@Override
//	public boolean onDoubleTap(MotionEvent e) {
//
//		// Toast.makeText(mContext, "DOUBLE " + e.getAction(),
//		// Toast.LENGTH_SHORT)
//		// .show();
//
//		return false;
//
//	}
//
//	@Override
//	public boolean onDoubleTapEvent(MotionEvent e) {
//
//		// Toast.makeText(mContext, "DOUBLE EVENT " + e.getAction(),
//		// Toast.LENGTH_SHORT).show();
//
//		return false;
//
//	}
//
//	@Override
//	public boolean onSingleTapConfirmed(MotionEvent e) {
//
//		// Toast.makeText(mContext, "SINGLE CONF " + e.getAction(),
//		// Toast.LENGTH_SHORT).show();
//
//		return false;
//
//	}
//
// }
