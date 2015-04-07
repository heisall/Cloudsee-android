
package com.jovision.commons;

import android.graphics.Point;
import android.util.FloatMath;
import android.view.MotionEvent;

/**
 * 手势分发
 * 
 * @author neo
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
    /** 手势放大 */
    public static final int GESTURE_TO_BIGGER = 0x05;
    /** 手势缩小 */
    public static final int GESTURE_TO_SMALLER = 0x06;

    /** click 事件 */
    public static final int CLICK_EVENT = 0x07;

    private static final int DEFAULT_BLIND_AERA_R = 8;
    private static final int DEFAULT_BLIND_SPACE_ZOOMING = 50;

    private Long lastTime = 0l;

    private int gesture;
    private int distance;

    private float lastDownX;
    private float lastDownY;
    private float lastDownX1;
    private float lastDownY1;

    private float lastSpace;

    private Point vector;
    private Point middle;

    private boolean multiMode;
    private boolean ignoreReport;

    private OnGestureListener listener;

    /**
     * 构造，传入监听器
     * 
     * @param listener
     */
    public MyGestureDispatcher(OnGestureListener listener) {
        multiMode = false;
        ignoreReport = false;

        gesture = GESTURE_TO_NULL;
        distance = 0;

        lastSpace = 0f;
        vector = new Point(0, 0);
        middle = new Point(-1, -1);

        this.listener = listener;
    }

    public boolean isIgnoreReport() {
        return ignoreReport;
    }

    public void setIgnoreReport(boolean ignoreReport) {
        this.ignoreReport = ignoreReport;
    }

    @SuppressWarnings("unused")
    private void debug(MotionEvent event) {
        System.out.println(String.format("%.0f, %.0f : %.0f, %.0f",
                event.getX(), event.getY(), event.getX(1), event.getY(1)));
    }

    private float getSpace(MotionEvent event) {
        float x = event.getX() - event.getX(1);
        float y = event.getY() - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void genMid(MotionEvent event) {
        middle.set(-1, -1);

        if (event.getPointerCount() > 1) {
            float x = event.getX() + event.getX(1);
            float y = event.getY() + event.getY(1);
            middle.set((int) (x / 2), (int) (y / 2));
        }
    }

    private void genOnePointMid(MotionEvent event) {
        if (1 == event.getPointerCount()) {
            float x = event.getX();
            float y = event.getY();
            middle.set((int) (x), (int) (y));
        }
    }

    /**
     * 需要监听手势的触摸事件，某次都要调用哦
     * 
     * @param event
     * @return 手势是否被判断出来并通知给监听器
     */
    public boolean motion(MotionEvent event) {
        boolean result = false;

        if (ignoreReport) {
            return result;
        }

        int count = event.getPointerCount();
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                lastTime = System.currentTimeMillis();
                vector.set(0, 0);
                lastDownX = event.getX();
                lastDownY = event.getY();
                gesture = GESTURE_TO_NULL;
                break;
            }

            case MotionEvent.ACTION_UP:
                multiMode = false;
                gesture = CLICK_EVENT;
                // TODO
                if (null != listener && GESTURE_TO_NULL != gesture) {
                    genOnePointMid(event);
                    int time = (int) (System.currentTimeMillis() - lastTime);
                    listener.onGesture(gesture, time, vector, middle);
                    result = true;
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                lastDownX1 = event.getX(1);
                lastDownY1 = event.getY(1);
                lastSpace = getSpace(event);
                multiMode = true;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (1 == count && false == multiMode) {
                    float upOffset = lastDownY - event.getY();
                    float rightOffset = event.getX() - lastDownX;

                    distance = (int) FloatMath.sqrt(upOffset * upOffset
                            + rightOffset * rightOffset);

                    // [Neo] 盲区判断
                    if (distance < DEFAULT_BLIND_AERA_R) {
                        break;
                    }

                    vector.set((int) rightOffset, (int) upOffset);

                    lastDownX = event.getX();
                    lastDownY = event.getY();

                    // [Neo] 点与面的关系，哇咔咔
                    if (upOffset + rightOffset > 0 && upOffset - rightOffset < 0) {
                        gesture = GESTURE_TO_RIGHT;
                    } else if (upOffset + rightOffset > 0
                            && upOffset - rightOffset > 0) {
                        gesture = GESTURE_TO_UP;
                    } else if (upOffset + rightOffset < 0
                            && upOffset - rightOffset > 0) {
                        gesture = GESTURE_TO_LEFT;
                    } else if (upOffset + rightOffset < 0
                            && upOffset - rightOffset < 0) {
                        gesture = GESTURE_TO_DOWN;
                    } else {
                        gesture = GESTURE_TO_NULL;
                    }

                } else if (2 == count) {

                    distance = (int) (getSpace(event) - lastSpace);

                    if (Math.abs(distance) < DEFAULT_BLIND_SPACE_ZOOMING) {
                        break;
                    }

                    int magic = (distance > 0) ? 1 : -1;
                    vector.set((int) ((Math.abs(event.getX() - lastDownX) + Math
                            .abs(event.getX(1) - lastDownX1)) * magic),
                            (int) ((Math.abs(event.getY() - lastDownY) + Math
                                    .abs(event.getY(1) - lastDownY1)) * magic));

                    genMid(event);
                    lastSpace = getSpace(event);

                    lastDownX = event.getX();
                    lastDownY = event.getY();
                    lastDownX1 = event.getX(1);
                    lastDownY1 = event.getY(1);

                    if (magic > 0) {
                        gesture = GESTURE_TO_BIGGER;
                    } else {
                        gesture = GESTURE_TO_SMALLER;
                    }

                } else {
                    break;
                }

                if (null != listener && GESTURE_TO_NULL != gesture) {
                    listener.onGesture(gesture, distance, vector, middle);
                    result = true;
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
     */
    public interface OnGestureListener {

        /**
         * 手势方向事件
         * 
         * @param gesture 方向，参考 {@link MyGestureDispatcher#GESTURE_TO_LEFT}，
         *            {@link MyGestureDispatcher#GESTURE_TO_UP}，
         *            {@link MyGestureDispatcher#GESTURE_TO_RIGHT}，
         *            {@link MyGestureDispatcher#GESTURE_TO_DOWN},
         *            {@link MyGestureDispatcher#GESTURE_TO_BIGGER},
         *            {@link MyGestureDispatcher#GESTURE_TO_SMALLER}
         * @param distance 距离
         * @param vector 向量
         * @param middle 中心点
         */
        public void onGesture(int gesture, int distance, Point vector,
                Point middle);
    }

}
