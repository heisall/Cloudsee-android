package com.jovision.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.jovision.commons.BaseApp;

public class ScrollZoomImageView extends ImageView {

	public boolean first = true;
	// drawable X|Y
	private double drawableLeft;
	private double drawableTop;
	private double drawableRight;
	private double drawableBottom;
	// 缩放倍数
	public float scale;
	private float minScale = 1;
	private float maxScale = 4;
	// 缩放标尺
	private double mLastSpace;

	private boolean pointFlag;
	private boolean scrollable = true;
	private boolean zoomable = true;
	private boolean moveing;
	private boolean scaling;
	private boolean hasFrom = false;

	private double mLastX;
	private double mLastY;

	private int dWidth;
	private int dHeight;
	public int vWidth;
	public int vHeight;

	private Handler handler = new Handler() {
		private Matrix matrix = new Matrix();
		private int delayMillis = 30;
		private float s = 1;
		private float ruleScale;
		private float dx = 0;
		private float dy = 0;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			matrix.set(getImageMatrix());
			switch (msg.what) {
			case 1:
				ruleScale = scale;
				if (scale < minScale)
					ruleScale = minScale;
				if (scale > maxScale)
					ruleScale = maxScale;

				s = ruleScale / scale;

				if ((int) (s * 100) != 100) {
					scaling = true;
					s = (float) Math.sqrt(Math.sqrt(s));
					delayMillis = 30;
					drawablePostScale(matrix, s);
					handler.sendEmptyMessageDelayed(2, delayMillis);
				} else {
					delayMillis = 50;
					handler.sendEmptyMessage(5);
				}
				break;
			case 2:
				drawablePostScale(matrix, s);
				handler.sendEmptyMessageDelayed(3, delayMillis);
				break;
			case 3:
				drawablePostScale(matrix, s);
				handler.sendEmptyMessageDelayed(4, delayMillis);
				break;
			case 4:
				s = ruleScale / scale;
				drawablePostScale(matrix, s);
				handler.sendEmptyMessageDelayed(5, delayMillis);
				scaling = false;
				break;
			case 5:
				move();
				if ((int) dx == 0 && (int) dy == 0) {
					break;
				}
				if (Math.abs(dx) > 1 || Math.abs(dy) > 1) {
					moveing = true;
					drawableTranslate(matrix, dx / 2, dy / 2);
					handler.sendEmptyMessageDelayed(6, delayMillis);
				}

				break;
			case 6:
				drawableTranslate(matrix, dx / 2, dy / 2);
				handler.sendEmptyMessageDelayed(7, delayMillis);
				break;
			case 7:
				drawableTranslate(matrix, dx / 2, dy / 2);
				handler.sendEmptyMessageDelayed(8, delayMillis);
				break;
			case 8:
				move();
				drawableTranslate(matrix, dx, dy);
				dx = 0;
				dy = 0;
				moveing = false;
				break;
			}
		}

		private void move() {
			if (drawableLeft > 0) {
				dx = (float) (0 - drawableLeft);
			}
			if (drawableTop > 0) {
				dy = (float) (0 - drawableTop);
			}
			if (drawableRight < vWidth) {
				dx = (float) (vWidth - drawableRight);
			}
			if (drawableBottom < vHeight) {
				dy = (float) (vHeight - drawableBottom);
			}
		}

	};

	public ScrollZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setScaleType(ScaleType.MATRIX);
	}

	public ScrollZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setScaleType(ScaleType.MATRIX);
	}

	public ScrollZoomImageView(Context context) {
		super(context);
		setScaleType(ScaleType.MATRIX);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		// 多于一屏，不让缩放
		if (BaseApp.SCREEN > BaseApp.SINGLE_SCREEN) {
			return true;
		}

		Matrix matrix = new Matrix();
		matrix.set(getImageMatrix());

		// 单点触控
		float x = event.getX();
		float y = event.getY();

		// 多点触控
		float point1X = 0;
		float point1Y = 0;
		float point2X = 0;
		float point2Y = 0;
		double currentSpace = mLastSpace;
		if (event.getPointerCount() > 1) {
			point1X = event.getX(0);
			point1Y = event.getY(0);
			point2X = event.getX(1);
			point2Y = event.getY(1);
			currentSpace = Math.hypot(point2X - point1X, point2Y - point1Y);
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mLastY = y;
			pointFlag = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (pointFlag) {
				if (zoomable && !scaling && !moveing) {
					float scale = (float) (currentSpace / mLastSpace);
					// 防止缩放比例过大
					if (scale > 1.2) {
						scale = 1.2f;
					}
					if (scale < 0.8) {
						scale = 0.8f;
					}
					// 缩放范围
					if (this.scale * scale < minScale * 1 && scale < 1) {
						scale = 1;
					}
					if (this.scale * scale > maxScale * 1.3 && scale > 1) {
						scale = 1;
					}
					mLastSpace = currentSpace;
					drawablePostScale(matrix, scale);
				}
			} else {
				if (scrollable && !scaling && !moveing) {
					float deltaX = (float) (x - mLastX);
					float deltaY = (float) (y - mLastY);
					mLastX = x;
					mLastY = y;
					double space = Math.hypot(deltaX, deltaY);
					/**
					 * && 防止移动过多
					 */
					if (space > 50) {
						double arg = 50 / space;
						deltaX *= arg;
						deltaY *= arg;
					}
					if (drawableLeft > 150 && deltaX > 0) {
						deltaX = 0;
					}
					if (drawableTop > 150 && deltaY > 0) {
						deltaY = 0;
					}
					if (vWidth - drawableRight > 150 && deltaX < 0) {
						deltaX = 0;
					}
					if (vHeight - drawableBottom > 150 && deltaY < 0) {
						deltaY = 0;
					}

					if (1.0 == scale) {
						space = 1;
						deltaX = 0;
						deltaY = 0;
					}

					drawableTranslate(matrix, deltaX, deltaY);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			pointFlag = false;
			handler.sendEmptyMessageDelayed(1, 30);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			pointFlag = true;
			mLastSpace = currentSpace;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			break;
		}

		return true;
	}

	/**
	 * matrix.postScale(scale, scale,vWidth*0.5f,vHeight*0.5f); 以View 中心为中点进行缩放
	 * 
	 * @param matrix
	 * @param scale
	 */
	private synchronized void drawablePostScale(Matrix matrix, float scale) {

		int scaleX = (int) (vWidth * scale);
		int scaleY = (int) (vHeight * scale);
		matrix.postScale(scale, scale, scaleX, scaleY);
		this.scale *= scale;

		drawableLeft = (drawableLeft - scaleX) * scale + scaleX;
		drawableTop = (drawableTop - scaleY) * scale + scaleY;
		drawableRight = drawableLeft + this.scale * dWidth;
		drawableBottom = drawableTop + this.scale * dHeight;

		setImageMatrix(matrix);
	}

	/**
	 * matrix.postTranslate(deltaX, deltaY);
	 * 
	 * @param matrix
	 * @param deltaX
	 * @param deltaY
	 */
	private synchronized void drawableTranslate(Matrix matrix, float deltaX,
			float deltaY) {

		matrix.postTranslate(deltaX, deltaY);
		drawableLeft += deltaX;
		drawableTop += deltaY;
		drawableRight = drawableLeft + scale * dWidth;
		drawableBottom = drawableTop + scale * dHeight;

		setImageMatrix(matrix);
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);

		if (selected) {
			hasFrom = true;
			this.postInvalidate();
		} else {
			hasFrom = false;
			this.postInvalidate();
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (first) {
			first = false;
			centerCrop();
		}
		if (hasFrom) {
			// 画边框
			Rect rec = canvas.getClipBounds();
			rec.bottom--;
			rec.right--;
			Paint paint = new Paint();
			paint.setColor(Color.GREEN);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(rec, paint);
		} else {
			// 去除边框
			Rect rec = canvas.getClipBounds();
			rec.left++;
			rec.top++;
			Paint paint = new Paint();
			paint.setColor(Color.alpha(1));
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawRect(rec, paint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		first = true;
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void centerCrop() {
		Bitmap bm = getImageBitmap();
		if (bm != null) {
			Matrix matrix = new Matrix();
			matrix.set(getImageMatrix());

			// vWidth = getWidth() - getPaddingLeft() - getPaddingRight();
			// vHeight = getHeight() - getPaddingTop() - getPaddingBottom();

			dWidth = bm.getWidth();
			dHeight = bm.getHeight();

			vWidth = dWidth;
			dHeight = vHeight;

			float dx = 0, dy = 0;

			if (dWidth * vHeight > vWidth * dHeight) {
				scale = (float) vHeight / (float) dHeight;
				dx = (vWidth - dWidth * scale) * 0.5f;
			} else {
				scale = (float) vWidth / (float) dWidth;
				dy = (vHeight - dHeight * scale) * 0.5f;
			}
			setMinScale(scale);
			matrix.setScale(scale, scale);
			matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
			drawableLeft = (int) (dx + 0.5f);
			drawableTop = (int) (dy + 0.5f);
			drawableRight = drawableLeft + scale * dWidth;
			drawableBottom = drawableTop + scale * dHeight;

			setImageMatrix(matrix);
		} else {
			first = true;
		}

	}

	private void setMinScale(float scale) {
		minScale = scale;
		maxScale = scale * 4;
	}

	private Bitmap getImageBitmap() {
		Drawable drawable = getDrawable();
		BitmapDrawable bd = (BitmapDrawable) drawable;
		if (bd != null)
			return bd.getBitmap();
		return null;
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	public void setZoomable(boolean zoomable) {
		this.zoomable = zoomable;
	}

	/**
	 * 获取 imageview 可视部分的图片内容
	 * 
	 * @return
	 */
	public Bitmap getVisualBitmap() {
		double width = drawableRight - drawableLeft;
		double height = drawableBottom - drawableTop;
		int left = (int) ((0 - drawableLeft) / width * dWidth + 0.5);
		int top = (int) ((0 - drawableTop) / height * dHeight + 0.5);
		int right = (int) ((vWidth - drawableLeft) / width * dWidth + 0.5);
		int bottom = (int) ((vHeight - drawableTop) / height * dHeight + 0.5);
		int picw = right - left;// 导出图片的宽度
		int pich = bottom - top;// 导出图片的高度

		Bitmap bitmap = getImageBitmap();
		if (left < 0)
			left = 0;
		if (top < 0)
			top = 0;
		int bw = bitmap.getWidth();
		int bh = bitmap.getHeight();
		if (bw < (left + picw))
			picw = bw - left;
		if (bh < (top + pich))
			pich = bh - top;
		Bitmap result = Bitmap.createBitmap(bitmap, left, top, picw, pich);
		return result;
	}

}