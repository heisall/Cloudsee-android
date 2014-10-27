/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package barcode.zxing.view;

import java.util.Collection;
import java.util.HashSet;

import android.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.google.zxing.ResultPoint;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 */
public final class ViewfinderView extends View {
	/**
	 * 刷新界面的时间
	 */
	// private static final long ANIMATION_DELAY = 0L;
	private static final int OPAQUE = 0xFF;

	/**
	 * 四个绿色边角对应的长度
	 */
	private int ScreenRate;

	/**
	 * 四个绿色边角对应的宽度
	 */
	private static final int CORNER_WIDTH = 4;
	/**
	 * 扫描框中的中间线的宽度
	 */
	private static final int MIDDLE_LINE_WIDTH = 4;

	/**
	 * 扫描框中的中间线的与扫描框左右的间隙
	 */
	private static final int MIDDLE_LINE_PADDING = 5;

	/**
	 * 中间那条线每次刷新移动的距离
	 */
	private static final int SPEEN_DISTANCE = 4;

	/**
	 * 手机的屏幕密度
	 */
	private static float density;
	/**
	 * 字体大小
	 */
	private static final int TEXT_SIZE = 16;
	/**
	 * 字体距离扫描框下面的距离
	 */
	private static final int TEXT_PADDING_TOP = 30;

	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/**
	 * 中间滑动线的最顶端位置
	 */
	private int slideTop;

	/**
	 * 中间滑动线的最底端位置
	 */
	private int slideBottom;

	/**
	 * 将扫描的二维码拍下来，这里没有这个功能，暂时不考虑
	 */
	private Bitmap resultBitmap;
	private final int maskColor;
	private final int resultColor;

	private final int resultPointColor;
	private Collection<ResultPoint> possibleResultPoints;
	private Collection<ResultPoint> lastPossibleResultPoints;

	boolean isFirst;

	boolean scrollType = true;// true:从上往下；false：从下往上
	private Bitmap progressBar;
	int progressBarWidth;
	private Display display;

	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		density = context.getResources().getDisplayMetrics().density;
		// 将像素转换成dp
		ScreenRate = (int) (20 * density);

		paint = new Paint();
		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask);
		resultColor = resources.getColor(R.color.result_view);

		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new HashSet<ResultPoint>(5);
		progressBar = BitmapFactory.decodeResource(getResources(),
				R.drawable.scanbar_progress);

		progressBarWidth = progressBar.getWidth();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = manager.getDefaultDisplay();
	}

	@Override
	public void onDraw(Canvas canvas) {
		// 中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
		// Rect frame = CameraManager.get().getFramingRect();
		// if (frame == null) {
		// return;
		// }
		int width1 = display.getWidth() / 2;
		int height1 = width1;
		int leftOffset = (display.getWidth() - width1) / 2;
		int topOffset = (display.getHeight() - height1) / 2;
		// 初始化中间线滑动的最上边和最下边
		if (!isFirst) {
			isFirst = true;
			slideTop = topOffset;
			slideBottom = topOffset + height1;
		}

		// 获取屏幕的宽和高
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		// 画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
		// 扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
		canvas.drawRect(0, 0, width, height, paint);
		// canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		// canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
		// paint);
		// canvas.drawRect(0, frame.bottom + 1, width, height, paint);

		if (resultBitmap != null) {
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(OPAQUE);
			canvas.drawBitmap(resultBitmap, leftOffset, topOffset, paint);
		} else {

			// 画扫描框边上的角，总共8个部分
			paint.setColor(Color.WHITE);
			canvas.drawRect(leftOffset, topOffset, leftOffset + ScreenRate,
					topOffset + CORNER_WIDTH, paint);
			canvas.drawRect(leftOffset, topOffset, leftOffset + CORNER_WIDTH,
					topOffset + ScreenRate, paint);
			canvas.drawRect(leftOffset + width1 - ScreenRate, topOffset,
					leftOffset + width1, topOffset + CORNER_WIDTH, paint);
			canvas.drawRect(leftOffset + width1 - CORNER_WIDTH, topOffset,
					leftOffset + width1, topOffset + ScreenRate, paint);
			canvas.drawRect(leftOffset, topOffset + height1 - CORNER_WIDTH,
					leftOffset + ScreenRate, topOffset + height1, paint);
			canvas.drawRect(leftOffset, topOffset + height1 - ScreenRate,
					leftOffset + CORNER_WIDTH, topOffset + height1, paint);
			canvas.drawRect(leftOffset + width1 - ScreenRate, topOffset
					+ height1 - CORNER_WIDTH, leftOffset + width1, topOffset
					+ height1, paint);
			canvas.drawRect(leftOffset + width1 - CORNER_WIDTH, topOffset
					+ height1 - ScreenRate, leftOffset + width1, topOffset
					+ height1, paint);

			// 绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
			if (scrollType) {
				// slideTop += SPEEN_DISTANCE;
				// if(slideTop >= topOffset + height1){
				// slideTop = topOffset;
				// }

				slideTop += SPEEN_DISTANCE;
				if (slideTop >= topOffset + height1) {
					slideTop = topOffset + height1;
					scrollType = false;
				}
			} else {
				slideTop -= SPEEN_DISTANCE;
				if (slideTop <= topOffset) {
					slideTop = topOffset;
					scrollType = true;
				}
			}

			// canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop -
			// MIDDLE_LINE_WIDTH/2, frame.right - MIDDLE_LINE_PADDING,slideTop +
			// MIDDLE_LINE_WIDTH/2, paint);
			if (null != progressBar && !progressBar.isRecycled()) {
				canvas.drawBitmap(progressBar, leftOffset
						+ (width - leftOffset - leftOffset - progressBarWidth)
						/ 2, slideTop, paint);

			}
			//
			// //画扫描框下面的字
			// paint.setColor(Color.WHITE);
			// paint.setTextSize(TEXT_SIZE * density);
			// paint.setAlpha(0x40);
			// paint.setTypeface(Typeface.create("System", Typeface.BOLD));
			// canvas.drawText(getResources().getString(R.string.str_scanning_device),
			// frame.left, (float) (frame.bottom + (float)TEXT_PADDING_TOP
			// *density), paint);

			// Collection<ResultPoint> currentPossible = possibleResultPoints;
			// Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			// if (currentPossible.isEmpty()) {
			// lastPossibleResultPoints = null;
			// } else {
			// possibleResultPoints = new HashSet<ResultPoint>(5);
			// lastPossibleResultPoints = currentPossible;
			// paint.setAlpha(OPAQUE);
			// paint.setColor(resultPointColor);
			// for (ResultPoint point : currentPossible) {
			// canvas.drawCircle(frame.left + point.getX(), frame.top
			// + point.getY(), 6.0f, paint);
			// }
			// }
			// if (currentLast != null) {
			// paint.setAlpha(OPAQUE / 2);
			// paint.setColor(resultPointColor);
			// for (ResultPoint point : currentLast) {
			// canvas.drawCircle(frame.left + point.getX(), frame.top
			// + point.getY(), 3.0f, paint);
			// }
			// }

			// 只刷新扫描框的内容，其他地方不刷新
			// postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
			// frame.right, frame.bottom);
			// postInvalidate(frame.left, frame.top,
			// frame.right, frame.bottom);
			postInvalidate(0, 0, width, height);

		}
	}

	public void drawViewfinder() {
		resultBitmap = null;
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		possibleResultPoints.add(point);
	}

}
