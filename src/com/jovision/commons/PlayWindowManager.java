package com.jovision.commons;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.newbean.Channel;

/**
 * 播放窗口管理工具类
 * 
 * @author neo
 * 
 */
public class PlayWindowManager implements View.OnClickListener,
		View.OnLongClickListener {

	private Context mContext;

	/** 全部窗口列表 */
	private ArrayList<PlayWindow> mWindowList;

	/** 全部页面列表，动态生成的 */
	private ArrayList<View> mPageList;
	/** 全部页面窗口组列表，动态生成的 */
	private ArrayList<PlayWindowGroup> mGroupList;

	private static final int SCREEN_1 = 1;
	private static final int SCREEN_2 = 4;
	private static final int SCREEN_3 = 9;
	private static final int SCREEN_4 = 16;
	private static final int SCREEN_5 = 25;
	private static final int SCREEN_6 = 36;

	public static final int BASE_ID = 0x1000;

	/** 单个播放窗口的边框标识 */
	public static final int ID_BORDER = BASE_ID + 0xFF;
	/** 单个播放窗口的中间控制标识 */
	public static final int ID_CONTROL_CENTER = BASE_ID + 0x100;
	/** 单个播放窗口的控制向左标识 */
	public static final int ID_CONTROL_LEFT = BASE_ID + 0x101;
	/** 单个播放窗口的控制向上标识 */
	public static final int ID_CONTROL_UP = BASE_ID + 0x102;
	/** 单个播放窗口的控制向右标识 */
	public static final int ID_CONTROL_RIGHT = BASE_ID + 0x103;
	/** 单个播放窗口的控制向下标识 */
	public static final int ID_CONTROL_BOTTOM = BASE_ID + 0x104;
	/** 单个播放窗口的消息文字标识 */
	public static final int ID_INFO_TEXT = BASE_ID + 0x105;
	/** 单个播放窗口的进度条标识 */
	public static final int ID_INFO_PROGRESS = BASE_ID + 0x106;

	private static final int DEFUALT_PLAYER_PADDING = 3;
	private static final int DEFUALT_TEXT_SIZE = 16;
	private static final int DEFUALT_TEXT_COLOR = Color.GREEN;
	private static final int DEFUALT_PROGRESS_WIDTH = 30;

	private static final int DEFAULT_VISIBILITY = View.GONE;

	public static final int STATUS_CREATED = 0x01;
	public static final int STATUS_CHANGED = 0x02;
	public static final int STATUS_DESTROYED = 0x03;

	private int playerPadding;

	// [Neo] TODO
	private int leftResId = R.drawable.left;
	private int upResId = R.drawable.up;
	private int rightResId = R.drawable.right;
	private int bottomResId = R.drawable.down;

	private int textSize;
	private int textColor;
	private int progressWidth;

	private static final int DEFAULT_UI_RES = R.drawable.ic_launcher;

	private PlayWindowManager() {
		mPageList = new ArrayList<View>();
		mWindowList = new ArrayList<PlayWindow>();
		mGroupList = new ArrayList<PlayWindowGroup>();

		playerPadding = DEFUALT_PLAYER_PADDING;

		// leftResId = DEFAULT_UI_RES;
		// upResId = DEFAULT_UI_RES;
		// rightResId = DEFAULT_UI_RES;
		// bottomResId = DEFAULT_UI_RES;

		progressWidth = DEFUALT_PROGRESS_WIDTH;
		textSize = DEFUALT_TEXT_SIZE;
		textColor = DEFUALT_TEXT_COLOR;
	}

	private static class PlayWindowManagerContainer {
		private static PlayWindowManager MANAGER = new PlayWindowManager();
	}

	/**
	 * 单例获取管理实例
	 * 
	 * @param context
	 *            内容上下文，必须实现 {@link PlayWindowManager.OnUiListener}
	 * @return
	 */
	public static PlayWindowManager getIntance(Context context) {
		if (null != context) {
			if (false == context instanceof OnUiListener) {
				throw new ClassCastException(
						"Context must an OnUiListener impl");
			}

			PlayWindowManagerContainer.MANAGER.mContext = context;
		}

		return PlayWindowManagerContainer.MANAGER;
	}

	/**
	 * 界面交互监听器
	 * 
	 * @author neo
	 * 
	 */
	public interface OnUiListener {

		/**
		 * 单个窗口的点击事件
		 * 
		 * @param channel
		 *            窗口所对应的通道
		 * @param isFromImageView
		 *            判断是否来自图像视图
		 * @param viewId
		 *            事件来自视图标识，参考 {@link PlayWindowManager#ID_CONTROL_CENTER},
		 *            {@link PlayWindowManager#ID_CONTROL_LEFT},
		 *            {@link PlayWindowManager#ID_CONTROL_UP},
		 *            {@link PlayWindowManager#ID_CONTROL_RIGHT},
		 *            {@link PlayWindowManager#ID_CONTROL_BOTTOM}
		 */
		public void onClick(Channel channel, boolean isFromImageView, int viewId);

		/**
		 * 
		 * 单个窗口的长按点击事件
		 * 
		 * @param channel
		 *            窗口所对应的通道
		 */
		public void onLongClick(Channel channel);

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

		/**
		 * 生命周期变化事件
		 * 
		 * @param index
		 *            对应窗口索引
		 * @param status
		 *            当前生命周期状态，参考 {@link PlayWindowManager#STATUS_CREATED},
		 *            {@link PlayWindowManager#STATUS_CHANGED},
		 *            {@link PlayWindowManager#STATUS_DESTROYED}
		 * @param surface
		 *            当前 Surface 对象
		 * @param width
		 *            长
		 * @param height
		 *            宽
		 */
		public void onLifecycle(int index, int status, Surface surface,
				int width, int height);
	}

	/**
	 * 设置窗口之间的边距，需在 {@link PlayWindowManager#genPageList(int)} 之前调用方可生效
	 * 
	 * @param padding
	 *            像素值
	 */
	public void setPadding(int padding) {
		playerPadding = padding;
	}

	/**
	 * 设置单个窗口内部的上下左右控制按钮的图片，需在 {@link PlayWindowManager#genPageList(int)}
	 * 之前调用方可生效
	 * 
	 * @param leftResId
	 *            左按钮图片资源标识
	 * @param upResId
	 *            上按钮图片资源标识
	 * @param rightResId
	 *            右按钮图片资源标识
	 * @param bottomResId
	 *            下按钮图片资源标识
	 */
	public void setArrowId(int leftResId, int upResId, int rightResId,
			int bottomResId) {
		this.leftResId = leftResId;
		this.upResId = upResId;
		this.rightResId = rightResId;
		this.bottomResId = bottomResId;
	}

	/**
	 * 中间控制相关设置，需在 {@link PlayWindowManager#genPageList(int)} 之前调用方可生效
	 * 
	 * @param progressWidth
	 *            进度条宽度
	 * @param textSize
	 *            提示文字大小
	 * @param textColor
	 *            提示文字颜色
	 */
	public void setCenterInfo(int progressWidth, int textSize, int textColor) {
		this.progressWidth = progressWidth;
		this.textSize = textSize;
		this.textColor = textColor;
	}

	/**
	 * 
	 * 修改中间控制图片
	 * 
	 * @param container
	 *            包含中间控制按钮的容器视图
	 * @param centerResId
	 *            待替换的图片资源标识
	 */
	public void setCenterResId(View container, int centerResId) {
		if (null != container) {
			ImageView view = ((ImageView) container
					.findViewById(ID_CONTROL_CENTER));
			if (null != view) {
				view.setImageResource(centerResId);
			}
		}
	}

	/**
	 * 
	 * 修改中间提示文字
	 * 
	 * @param container
	 *            包含中间提示文字的容器视图
	 * @param text
	 *            待修改的文字内容
	 */
	public void setInfo(View container, String text) {
		if (null != container) {
			TextView view = ((TextView) container.findViewById(ID_INFO_TEXT));
			if (null != view) {
				view.setText(text);
			}
		}
	}

	/**
	 * 修改指定控件的显示性
	 * 
	 * @param container
	 *            包含中间提示文字的容器视图
	 * @param viewId
	 *            控件标识，参考 {@link PlayWindowManager#ID_BORDER}，
	 *            {@link PlayWindowManager#ID_CONTROL_BOTTOM}，
	 *            {@link PlayWindowManager#ID_CONTROL_CENTER}，
	 *            {@link PlayWindowManager#ID_CONTROL_LEFT}，
	 *            {@link PlayWindowManager#ID_CONTROL_RIGHT}，
	 *            {@link PlayWindowManager#ID_CONTROL_UP}，
	 *            {@link PlayWindowManager#ID_INFO_PROGRESS}，
	 *            {@link PlayWindowManager#ID_INFO_TEXT}
	 * @param visibility
	 *            显示性，参考 {@link View#VISIBLE}， {@link View#INVISIBLE}，
	 *            {@link View#GONE}，
	 */
	public void setViewVisibility(View container, int viewId, int visibility) {
		if (null != container) {
			View view = container.findViewById(viewId);
			if (null != view) {
				view.setVisibility(visibility);
			}
		}
	}

	/**
	 * 添加新的通道
	 * 
	 * @param channel
	 *            通道对象
	 */
	public void addChannel(Channel channel) {
		if (null != channel && false == channel.isConfigChannel()) {
			int index = mWindowList.size();
			channel.setIndex(index);
			PlayWindow window = new PlayWindow(channel);
			mWindowList.add(window);
		}
	}

	/**
	 * 获取指定页面下指定窗口的通道
	 * 
	 * @param index
	 *            窗口索引
	 * @return 通道对象
	 */
	public Channel getChannel(int index) {
		Channel channel = null;

		if (index >= 0 && index < mWindowList.size()) {
			channel = mWindowList.get(index).getChannel();
		}

		return channel;
	}

	/**
	 * 获取指定页面下指定窗口的通道
	 * 
	 * @param pageId
	 *            页面标识，从 0 开始
	 * @param position
	 *            窗口标识，当前页面从 0 开始
	 * @return 通道对象
	 */
	public Channel getChannel(int pageId, int position) {
		Channel channel = null;
		int size = mGroupList.size();
		if (pageId < size) {
			PlayWindowGroup group = mGroupList.get(pageId);
			size = group.getCount();

			if (position < size) {
				channel = group.getWindow(position).getChannel();
			}
		}

		return channel;
	}

	/**
	 * 通过页面标识获取可用的通道列表
	 * 
	 * @param pageId
	 *            页面标识
	 * @return 可用通道列表
	 */
	public ArrayList<Channel> getValidChannelList(int pageId) {
		ArrayList<Channel> list = null;
		if (mGroupList.size() > pageId) {
			list = new ArrayList<Channel>();
			for (PlayWindow window : mGroupList.get(pageId).getWindowList()) {
				if (null != window.getChannel()
						&& window.getChannel().getIndex() >= 0) {
					list.add(window.getChannel());
				}
			}
		}

		return list;
	}

	/**
	 * 获取所有通道列表
	 * 
	 * @return
	 */
	public ArrayList<Channel> getChannelList() {
		ArrayList<Channel> list = new ArrayList<Channel>();
		for (PlayWindow window : mWindowList) {
			list.add(window.getChannel());
		}

		return list;
	}

	/**
	 * 通过页面标识和窗口标识获取播放视图
	 * 
	 * @param index
	 *            窗口索引
	 * @return 播放视图对象
	 */
	public SurfaceView getView(int index) {
		SurfaceView view = null;
		if (index >= 0 && index < mWindowList.size()) {
			try {
				view = mWindowList.get(index).getChannel().getSurfaceView();
			} catch (Exception e) {
			}
		}

		return view;
	}

	/**
	 * 通过页面标识和窗口标识获取播放视图
	 * 
	 * @param pageId
	 *            页面标识
	 * @param position
	 *            窗口标识
	 * @return 播放视图对象
	 */
	public SurfaceView getView(int pageId, int position) {
		SurfaceView view = null;
		int size = mGroupList.size();
		if (pageId < size) {
			PlayWindowGroup group = mGroupList.get(pageId);
			size = group.getCount();

			if (position < size) {
				view = group.getWindow(position).getChannel().getSurfaceView();
			}
		}

		return view;
	}

	/**
	 * 恢复所有页面播放视图
	 * 
	 */
	public void resumeAll() {
		for (PlayWindowGroup group : mGroupList) {
			group.resume();
		}
	}

	/**
	 * 恢复指定页面播放视图
	 * 
	 * @param pageId
	 *            页面标识
	 */
	public void resumePage(int pageId) {
		int size = mGroupList.size();
		if (pageId < size) {
			mGroupList.get(pageId).resume();
		}
	}

	/**
	 * 恢复指定通道的播放，需要等待 I 帧之后才能显示
	 * 
	 * @param index
	 *            通道索引
	 */
	public void resume(int index) {
		int size = mWindowList.size();
		if (index < size) {
			SurfaceView view = mWindowList.get(index).getChannel()
					.getSurfaceView();
			if (null != view) {
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 暂停所有页面播放视图
	 * 
	 */
	public void pauseAll() {
		for (PlayWindowGroup group : mGroupList) {
			group.pause();
		}
	}

	/**
	 * 暂停指定页面播放视图
	 * 
	 * @param pageId
	 *            页面标识
	 */
	public void pausePage(int pageId) {
		int size = mGroupList.size();
		if (pageId < size) {
			mGroupList.get(pageId).pause();
		}
	}

	/**
	 * 暂停指定通道的播放
	 * 
	 * @param index
	 *            通道索引
	 */
	public void pause(int index) {
		int size = mWindowList.size();
		if (index < size) {
			SurfaceView view = mWindowList.get(index).getChannel()
					.getSurfaceView();
			if (null != view) {
				view.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 清空当前页面列表视图
	 * 
	 */
	private void clearPageLayout() {
		int removeCount = 0;
		int wsize = mWindowList.size();

		// [Neo] 计算待清理的空白播放视图
		for (int i = wsize - 1; i > 0; i--) {
			if (mWindowList.get(i).getChannel().getIndex() < 0) {
				removeCount++;
			} else {
				break;
			}
		}

		// [Neo] 清理空白播放视图
		for (int i = 0; i < removeCount; i++) {
			mWindowList.remove(wsize - 1 - i);
		}

		// [Neo] 清理播放视图与布局关系
		for (View view : mPageList) {
			if (view instanceof ViewGroup) {
				ViewGroup layout = (ViewGroup) view;

				if (layout.getChildAt(0) instanceof ViewGroup) {
					int size = layout.getChildCount();

					for (int i = 0; i < size; i++) {
						ViewGroup liner = (ViewGroup) layout.getChildAt(i);
						int lsize = liner.getChildCount();

						for (int j = 0; j < lsize; j++) {
							View v = liner.getChildAt(j);
							if (v instanceof ViewGroup) {
								((ViewGroup) v).removeAllViews();
							}
						}

						liner.removeAllViews();
					}
				}

				layout.removeAllViews();
			}
		}

		// [Neo] 清空列表
		mPageList.clear();
		mGroupList.clear();
	}

	/**
	 * 生成指定窗口个数的页面视图列表
	 * 
	 * @param windowCount
	 *            窗口个数
	 * @return 页面视图列表
	 */
	public ArrayList<View> genPageList(int windowCount) {
		clearPageLayout();

		int size = mWindowList.size();

		if (windowCount > 1) {
			int left = windowCount - size % windowCount;

			if (windowCount != left) {
				size += left;
				for (; left > 0; left--) {
					mWindowList.add(new PlayWindow(null));
				}
			}
		}

		System.gc();

		int pageId = 0;
		PlayWindowGroup group = new PlayWindowGroup(pageId);

		for (int i = 0; i < size; i++) {
			group.add(mWindowList.get(i));

			if (0 == (i + 1) % windowCount) {
				mGroupList.add(group);

				pageId++;
				group = new PlayWindowGroup(pageId);
			}
		}

		for (PlayWindowGroup g : mGroupList) {
			mPageList.add(g.genPage());
		}

		return mPageList;
	}

	/**
	 * 销毁所有资源
	 * 
	 */
	public void destroy() {
		clearPageLayout();
		mWindowList.clear();
		PlayWindowManagerContainer.MANAGER.mContext = null;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (null != mContext && id >= BASE_ID) {

			boolean isFromImageView = false;

			if (v instanceof ImageView) {
				isFromImageView = true;
				v = ((ViewGroup) v.getParent()).getChildAt(0);
			}

			PlayWindow window = mWindowList.get(id - BASE_ID);

			((OnUiListener) mContext).onClick(window.getChannel(),
					isFromImageView, id);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		boolean result = false;

		if (null != mContext && v.getId() >= BASE_ID) {
			PlayWindow window = mWindowList.get(v.getId() - BASE_ID);
			((OnUiListener) mContext).onLongClick(window.getChannel());
			result = true;
		}

		return result;
	}

	/**
	 * 播放窗口组
	 * 
	 * @author neo
	 * 
	 */
	private class PlayWindowGroup {

		private int mPageId;
		private ArrayList<PlayWindow> mWindowGroupList;

		public PlayWindowGroup(int pageId) {
			this.mPageId = pageId;
			mWindowGroupList = new ArrayList<PlayWindow>();
		}

		public void add(PlayWindow window) {
			mWindowGroupList.add(window);
		}

		public int getCount() {
			return mWindowGroupList.size();
		}

		public ArrayList<PlayWindow> getWindowList() {
			return mWindowGroupList;
		}

		public PlayWindow getWindow(int position) {
			return mWindowGroupList.get(position);
		}

		public void resume() {
			for (PlayWindow window : mWindowGroupList) {
				SurfaceView view = window.getChannel().getSurfaceView();
				if (null != view) {
					view.setVisibility(View.VISIBLE);
				}
			}
		}

		public void pause() {
			for (PlayWindow window : mWindowGroupList) {
				SurfaceView view = window.getChannel().getSurfaceView();
				if (null != view) {
					view.setVisibility(View.GONE);
				}
			}
		}

		public View genPage() {
			int count = getCount();
			ViewGroup layout = null;

			switch (count) {
			case SCREEN_1:
				// [Neo] 窗口容器
				layout = new RelativeLayout(mContext);
				layout.setBackgroundColor(Color.BLACK);
				layout.setPadding(playerPadding, playerPadding, playerPadding,
						playerPadding);

				// [Neo] 播放视图
				View player = mWindowGroupList.get(0).getChannel()
						.getSurfaceView();
				if (null != player) {
					player.setId(BASE_ID
							+ mWindowGroupList.get(0).getChannel().getIndex());
					player.setOnClickListener(PlayWindowManager
							.getIntance(null));
					player.setOnLongClickListener(PlayWindowManager
							.getIntance(null));
					RelativeLayout.LayoutParams playerParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.MATCH_PARENT);
					playerParams.addRule(RelativeLayout.CENTER_IN_PARENT);
					player.setLayoutParams(playerParams);

					layout.addView(player);
					player.setVisibility(View.VISIBLE);
				} else {
					MyLog.e(Consts.TAG_APP, "PWM.genPage, null player");
				}

				addCoverViews(layout);
				break;

			case SCREEN_2:
			case SCREEN_3:
			case SCREEN_4:
			case SCREEN_5:
			case SCREEN_6:
				count = (int) Math.sqrt(count);
				layout = genPlayerGridLayout(count, count);
				break;

			default:
				MyLog.e(Consts.TAG_APP, "bad size: " + count + ", in "
						+ mPageId);
				break;
			}

			return layout;
		}

		/**
		 * 为播放窗口添加额外的控件
		 * 
		 * @param layout
		 *            容器视图
		 */
		private void addCoverViews(ViewGroup layout) {
			// [Neo] 中间控制图
			ImageView center = new ImageView(mContext);
			center.setId(ID_CONTROL_CENTER);
			RelativeLayout.LayoutParams centerParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			centerParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			center.setLayoutParams(centerParams);
			center.setOnClickListener(PlayWindowManager.getIntance(null));
			// [Neo] TODO control drawable res
			center.setImageResource(DEFAULT_UI_RES);
			center.setVisibility(DEFAULT_VISIBILITY);

			// [Neo] 左按钮
			ImageView left = new ImageView(mContext);
			left.setId(ID_CONTROL_LEFT);
			RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			leftParams.addRule(RelativeLayout.CENTER_VERTICAL);
			leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			left.setImageResource(leftResId);
			left.setLayoutParams(leftParams);
			left.setVisibility(DEFAULT_VISIBILITY);

			// [Neo] 上按钮
			ImageView up = new ImageView(mContext);
			up.setId(ID_CONTROL_UP);
			RelativeLayout.LayoutParams upParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			upParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			upParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			up.setImageResource(upResId);
			up.setLayoutParams(upParams);
			up.setVisibility(DEFAULT_VISIBILITY);

			// [Neo] 右按钮
			ImageView right = new ImageView(mContext);
			right.setId(ID_CONTROL_RIGHT);
			RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			rightParams.addRule(RelativeLayout.CENTER_VERTICAL);
			rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			right.setImageResource(rightResId);
			right.setLayoutParams(rightParams);
			right.setVisibility(DEFAULT_VISIBILITY);

			// [Neo] 下按钮
			ImageView bottom = new ImageView(mContext);
			bottom.setId(ID_CONTROL_BOTTOM);
			RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			bottomParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			bottom.setImageResource(bottomResId);
			bottom.setLayoutParams(bottomParams);
			bottom.setVisibility(DEFAULT_VISIBILITY);

			layout.addView(center);
			layout.addView(left);
			layout.addView(up);
			layout.addView(right);
			layout.addView(bottom);

			// [Neo] 中间控制布局
			LinearLayout centerLayout = new LinearLayout(mContext);
			RelativeLayout.LayoutParams centerParamsAgain = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			centerParamsAgain.addRule(RelativeLayout.CENTER_IN_PARENT);
			centerLayout.setLayoutParams(centerParamsAgain);
			centerLayout.setGravity(Gravity.CENTER);
			centerLayout.setOrientation(LinearLayout.VERTICAL);

			{
				// [Neo] 中间进度条
				ProgressBar progressBar = new ProgressBar(mContext);
				progressBar.setId(ID_INFO_PROGRESS);
				LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
						progressWidth, progressWidth);
				progressBar.setLayoutParams(progressParams);
				progressBar.setVisibility(DEFAULT_VISIBILITY);

				centerLayout.addView(progressBar);

				// [Neo] 中间信息文字
				TextView text = new TextView(mContext);
				text.setId(ID_INFO_TEXT);
				LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				// [Neo] TODO
				// textParams.topMargin = 5;
				text.setLayoutParams(textParams);
				text.setTextColor(textColor);
				text.setTextSize(textSize);
				text.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
				text.setSingleLine(true);
				text.setVisibility(DEFAULT_VISIBILITY);

				centerLayout.addView(text);
			}

			layout.addView(centerLayout);
		}

		/**
		 * 生成播放网格布局
		 * 
		 * @param cols
		 *            列数，大于 1
		 * @param lines
		 *            行数，大于 1
		 * @return 网格布局视图
		 */
		private ViewGroup genPlayerGridLayout(int cols, int lines) {
			LinearLayout layout = new LinearLayout(mContext);
			layout.setPadding(playerPadding, playerPadding, playerPadding,
					playerPadding);
			// TODO
			// layout.setBackgroundColor(mContext.getResources().getColor(
			// R.color.videounselect));
			layout.setOrientation(LinearLayout.VERTICAL);

			LinearLayout.LayoutParams verticalLayoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
			LinearLayout.LayoutParams horizontalLayoutParams = new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
			RelativeLayout.LayoutParams playerLayoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			playerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

			for (int i = 0; i < lines; i++) {
				LinearLayout ll = new LinearLayout(mContext);
				ll.setLayoutParams(verticalLayoutParams);
				ll.setOrientation(LinearLayout.HORIZONTAL);

				for (int j = 0; j < cols; j++) {
					RelativeLayout border = new RelativeLayout(mContext);
					border.setId(ID_BORDER);
					// [Neo] TODO background color
					// border.setBackgroundColor(Color.BLACK);
					border.setLayoutParams(horizontalLayoutParams);
					border.setPadding(playerPadding, playerPadding,
							playerPadding, playerPadding);

					int index = i * cols + j;

					View player = mWindowGroupList.get(index).getChannel()
							.getSurfaceView();
					if (null != player) {
						player.setId(BASE_ID
								+ mWindowGroupList.get(index).getChannel()
										.getIndex());
						player.setOnClickListener(PlayWindowManager
								.getIntance(null));
						player.setOnLongClickListener(PlayWindowManager
								.getIntance(null));
						player.setLayoutParams(playerLayoutParams);

						border.addView(player);
						player.setVisibility(View.VISIBLE);
					} else {
						MyLog.e(Consts.TAG_APP,
								"PWM.genPlayerGridLayout, null player");
					}

					addCoverViews(border);

					ll.addView(border);
				}

				layout.addView(ll);
			}

			return layout;
		}
	}

	/**
	 * 播放窗口
	 * 
	 * @author neo
	 * 
	 */
	private class PlayWindow {

		private int index;
		private Channel mChannel;

		public PlayWindow(Channel channel) {
			index = -1;

			if (null == channel) {
				mChannel = new Channel(null, -1, -1, false, false);
			} else {
				mChannel = channel;
			}

			index = mChannel.getIndex();
			mChannel.setSurfaceView(genSurfaceView());
		}

		private SurfaceView genSurfaceView() {
			final MyGestureDispatcher dispatcher = new MyGestureDispatcher(
					new MyGestureDispatcher.OnGestureListener() {

						@Override
						public void onGesture(int direction) {
							((OnUiListener) mContext).onGesture(direction);
						}
					});

			SurfaceView view = new SurfaceView(mContext);
			view.setOnTouchListener(new View.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return dispatcher.motion(event);
				}
			});

			if (index >= 0) {
				SurfaceHolder holder = view.getHolder();

				holder.addCallback(new SurfaceHolder.Callback() {
					@Override
					public void surfaceDestroyed(SurfaceHolder holder) {
						((OnUiListener) mContext).onLifecycle(index,
								STATUS_DESTROYED, null, -1, -1);
					}

					@Override
					public void surfaceCreated(SurfaceHolder holder) {
						((OnUiListener) mContext).onLifecycle(index,
								STATUS_CREATED, holder.getSurface(), -1, -1);
					}

					@Override
					public void surfaceChanged(SurfaceHolder holder,
							int format, int width, int height) {
						((OnUiListener) mContext).onLifecycle(index,
								STATUS_CHANGED, holder.getSurface(), width,
								height);
					}
				});
			}

			return view;
		}

		public Channel getChannel() {
			return mChannel;
		}

	}

}
