package com.jovision.views;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jovision.commons.BaseApp;

//import com.jovetech.CloudSee.temp.LeftFragment;

public class RefreshableMainListView extends ListView {

	private View mHeaderContainer = null;
	private View mHeaderView = null;
	private ImageView mArrow = null;
	private ProgressBar mProgress = null;
	private TextView refreshState = null;// 刷新状态
	private TextView refreshTime = null;// 刷新时间

	private float mY = 0;
	private float mHistoricalY = 0;
	private int mHistoricalTop = 0;
	private int mInitialHeight = 0;
	private boolean mFlag = false;
	private boolean mArrowUp = false;
	private boolean mIsRefreshing = false;
	private int mHeaderHeight = 0;
	private OnRefreshListener mListener = null;

	private final int REFRESH = 0;
	private final int NORMAL = 1;
	private final int REPEAT = 2;
	private final int HEADER_HEIGHT_DP = 62;
	private Boolean repeatFlag = false;// 重复刷新标志

	// private static final String TAG =
	// RefreshableListView.class.getSimpleName();

	public RefreshableMainListView(final Context context) {
		super(context);
		initialize();
	}

	public RefreshableMainListView(final Context context,
			final AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public RefreshableMainListView(final Context context,
			final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	// 下拉刷新监听
	public void setOnRefreshListener(final OnRefreshListener l) {
		mListener = l;
	}

	// 完成刷新
	public void completeRefreshing() {
		mProgress.setVisibility(View.INVISIBLE);
		mArrow.setVisibility(View.VISIBLE);
		mHandler.sendMessage(mHandler.obtainMessage(NORMAL, mHeaderHeight, 0));
		mIsRefreshing = false;
		invalidateViews();
		repeatFlag = true;
		CountDown cd = new CountDown(60 * 1000, 1000);
		cd.start();
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent ev) {
		// if (!BaseApp.LOCAL_LOGIN_FLAG) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mHandler.removeMessages(REFRESH);
			mHandler.removeMessages(NORMAL);
			mY = mHistoricalY = ev.getY();
			if (mHeaderContainer.getLayoutParams() != null) {
				mInitialHeight = mHeaderContainer.getLayoutParams().height;
			}
			break;
		}
		// }
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent ev) {
		// if (!BaseApp.LOCAL_LOGIN_FLAG) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_MOVE:
			mHistoricalTop = getChildAt(0).getTop();
			break;
		case MotionEvent.ACTION_UP:
			if (!mIsRefreshing) {
				if (mArrowUp) {
					startRefreshing();
					mHandler.sendMessage(mHandler.obtainMessage(REFRESH,
							(int) (ev.getY() - mY) / 2 + mInitialHeight, 0));
				} else {
					if (getChildAt(0).getTop() == 0) {
						mHandler.sendMessage(mHandler.obtainMessage(NORMAL,
								(int) (ev.getY() - mY) / 2 + mInitialHeight, 0));
					}
				}
			} else {
				mHandler.sendMessage(mHandler.obtainMessage(REFRESH,
						(int) (ev.getY() - mY) / 2 + mInitialHeight, 0));
			}
			mFlag = false;
			break;
		}
		// }
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev) {
		// if (!BaseApp.LOCAL_LOGIN_FLAG) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE
				&& getFirstVisiblePosition() == 0) {
			float direction = ev.getY() - mHistoricalY;
			int height = (int) (ev.getY() - mY) / 2 + mInitialHeight;
			if (height < 0) {
				height = 0;
			}

			float deltaY = Math.abs(mY - ev.getY());
			ViewConfiguration config = ViewConfiguration.get(getContext());

			try {

				if (deltaY > config.getScaledTouchSlop()) {

					// Scrolling downward
					if (direction > 0) {
						// Refresh bar is extended if top pixel of the first
						// item is
						// visible
						if (null != getChildAt(0)
								&& getChildAt(0).getTop() == 0) {
							if (mHistoricalTop < 0) {

								// mY = ev.getY(); // TODO works without
								// this?mHistoricalTop = 0;
							}

							// Extends refresh bar
							setHeaderHeight(height);

							// Stop list scroll to prevent the list from
							// overscrolling
							ev.setAction(MotionEvent.ACTION_CANCEL);
							mFlag = false;
						}
					} else if (direction < 0) {
						// Scrolling upward

						// Refresh bar is shortened if top pixel of the
						// first
						// item
						// is
						// visible
						if (null != getChildAt(0)
								&& getChildAt(0).getTop() == 0) {
							setHeaderHeight(height);

							// If scroll reaches top of the list, list
							// scroll is
							// enabled
							if (getChildAt(1) != null
									&& getChildAt(1).getTop() <= 1 && !mFlag) {
								ev.setAction(MotionEvent.ACTION_DOWN);
								mFlag = true;
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			mHistoricalY = ev.getY();
		}
		// }
		try {
			return super.dispatchTouchEvent(ev);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean performItemClick(final View view, final int position,
			final long id) {
		if (position == 0) {
			// This is the refresh header element
			return true;
		} else {
			return super.performItemClick(view, position - 1, id);
		}
	}

	// 初始化视图
	private void initialize() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHeaderContainer = inflater.inflate(R.layout.messagelist_head, null);
		mHeaderView = mHeaderContainer
				.findViewById(R.id.refreshable_list_header);
		mArrow = (ImageView) mHeaderContainer
				.findViewById(R.id.refreshable_list_arrow);
		mProgress = (ProgressBar) mHeaderContainer
				.findViewById(R.id.refreshable_list_progress);
		refreshState = (TextView) mHeaderContainer
				.findViewById(R.id.refreshable_list_state);
		refreshTime = (TextView) mHeaderContainer
				.findViewById(R.id.refreshable_list_time);
		refreshTime.setText(getResources().getString(R.string.str_update_at)
				+ BaseApp.getCurrentTime());

		addHeaderView(mHeaderContainer);

		mHeaderHeight = (int) (HEADER_HEIGHT_DP * getContext().getResources()
				.getDisplayMetrics().density);
		setHeaderHeight(0);
	}

	private void setHeaderHeight(final int height) {
		if (height <= 1) {
			mHeaderView.setVisibility(View.GONE);
		} else {
			mHeaderView.setVisibility(View.VISIBLE);
		}

		// Extends refresh bar
		LayoutParams lp = (LayoutParams) mHeaderContainer.getLayoutParams();
		if (lp == null) {
			lp = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
		}
		lp.height = height;
		mHeaderContainer.setLayoutParams(lp);

		// Refresh bar shows up from bottom to top
		LinearLayout.LayoutParams headerLp = (LinearLayout.LayoutParams) mHeaderView
				.getLayoutParams();
		if (headerLp == null) {
			headerLp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT);
		}
		headerLp.topMargin = -mHeaderHeight + height;
		mHeaderView.setLayoutParams(headerLp);

		if (!mIsRefreshing) {
			// If scroll reaches the trigger line, start refreshing
			if (height > mHeaderHeight && !mArrowUp) {
				mArrow.startAnimation(AnimationUtils.loadAnimation(
						getContext(), R.anim.rotate));
				refreshState.setText(R.string.str_refresh_data);
				rotateArrow();
				mArrowUp = true;
			} else if (height < mHeaderHeight && mArrowUp) {
				mArrow.startAnimation(AnimationUtils.loadAnimation(
						getContext(), R.anim.rotate));
				refreshState.setText(R.string.pull_to_refresh_pull_label);
				refreshTime.setText(getResources().getString(
						R.string.str_update_at)
						+ BaseApp.getCurrentTime());
				rotateArrow();
				mArrowUp = false;
			}
		}
	}

	private void rotateArrow() {
		Drawable drawable = mArrow.getDrawable();
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.save();
		canvas.rotate(180.0f, canvas.getWidth() / 2.0f,
				canvas.getHeight() / 2.0f);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		canvas.restore();
		mArrow.setImageBitmap(bitmap);
	}

	// 下拉松开后开始刷新数据
	private void startRefreshing() {
		mArrow.setVisibility(View.INVISIBLE);
		mProgress.setVisibility(View.VISIBLE);
		if (!repeatFlag) {
			refreshState.setText(R.string.pull_to_refresh_refreshing_label);
			if (!mIsRefreshing) {
				mIsRefreshing = true;
				if (mListener != null) {
					mListener.onRefresh(this);
				}
			}
		} else {
			refreshState.setText(getResources().getString(
					R.string.pull_to_refresh_repeat_label));
			if (!mIsRefreshing) {
				mIsRefreshing = true;
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
							mHandler.sendEmptyMessage(REPEAT);
						} catch (InterruptedException e) {
						}
					};
				}.start();
			}
		}
	}

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(final Message msg) {
			super.handleMessage(msg);

			int limit = 0;
			switch (msg.what) {
			case REFRESH:
				limit = mHeaderHeight;
				break;
			case NORMAL:
				limit = 0;
				break;
			case REPEAT:
				mProgress.setVisibility(View.INVISIBLE);
				mArrow.setVisibility(View.VISIBLE);
				mIsRefreshing = false;
				mHandler.sendMessage(mHandler.obtainMessage(NORMAL,
						mHeaderHeight, 0));
				break;
			}

			// Elastic scrolling
			if (msg.arg1 >= limit) {
				setHeaderHeight(msg.arg1);
				int displacement = (msg.arg1 - limit) / 4;
				if (displacement == 0) {
					mHandler.sendMessage(mHandler.obtainMessage(msg.what,
							msg.arg1 - 1, 0));
				} else {
					mHandler.sendMessage(mHandler.obtainMessage(msg.what,
							msg.arg1 - displacement, 0));
				}

			} else {
				// 未刷新
				if (!mIsRefreshing) {
					setHeaderHeight(0);
				}

			}
		}

	};

	public interface OnRefreshListener {
		public void onRefresh(RefreshableMainListView listView);
	}

	// 刷新后倒计时60秒后才能再刷新
	class CountDown extends CountDownTimer {

		public CountDown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		public void onFinish() {
			synchronized (this) {
				repeatFlag = false;
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}
}
