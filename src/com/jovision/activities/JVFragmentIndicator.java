package com.jovision.activities;

import android.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Administrator 功能描述：自定义底部工具栏
 * 
 */
public class JVFragmentIndicator extends LinearLayout implements
		OnClickListener {

	private int mDefaultIndicator = 0;
	private static int mCurIndicator;
	private static View[] mIndicators;
	private OnIndicateListener mOnIndicateListener;
	private Context mContext;
	private LayoutInflater inflater;

	private static final String[] iconTagArray = { "icon_tag_0", "icon_tag_1",
			"icon_tag_2", "icon_tag_3", "icon_tag_4" };

	private static final String[] textTagArray = { "text_tag_0", "text_tag_1",
			"text_tag_2", "text_tag_3", "text_tag_4" };

	private static String[] titleArray;// 4个标题数组
	private static int[] unSelectedArray = {
			R.drawable.mydevice_devicenormal_icon,
			R.drawable.mydevice_messagenormal_icon,
			R.drawable.mydevice_videomanagenormal_icon,
			R.drawable.mydevice_moremessagenormal_icon };
	private static int[] selectedArray = {
			R.drawable.mydevice_devicepress_icon,
			R.drawable.mydevice_messagepress_icon,
			R.drawable.mydevice_videomanagepress_icon,
			R.drawable.mydevice_moremessagepress_icon };

	private static final int COLOR_UNSELECT = R.color.tab_text_color;
	private static final int COLOR_SELECT = R.color.tab_text_color;

	private JVFragmentIndicator(Context context) {
		super(context);
	}

	public JVFragmentIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		titleArray = mContext.getResources().getStringArray(R.array.array_tab);
		mCurIndicator = mDefaultIndicator;
		setOrientation(LinearLayout.HORIZONTAL);
		init();
	}

	private View createIndicator(int iconResID, String title, int stringColor,
			String iconTag, String textTag) {
		RelativeLayout tabLayout = (RelativeLayout) inflater.inflate(
				R.layout.tab_item, null);
		ImageView tabIcon = (ImageView) tabLayout.findViewById(R.id.tab_icon);
		TextView tabTitle = (TextView) tabLayout.findViewById(R.id.tab_title);

		tabIcon.setTag(iconTag);
		tabIcon.setImageResource(iconResID);
		tabTitle.setTag(textTag);
		tabTitle.setTextColor(stringColor);
		// tabTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		tabTitle.setText(title);

		LinearLayout view = new LinearLayout(getContext());
		view.setOrientation(LinearLayout.VERTICAL);
		view.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
		view.setGravity(Gravity.CENTER_HORIZONTAL);
		view.addView(tabLayout);
		return view;

	}

	private void init() {
		if (null != titleArray) {
			titleArray = mContext.getResources().getStringArray(
					R.array.array_tab);
		}
		int length = titleArray.length;
		mIndicators = new View[titleArray.length];

		for (int i = 0; i < length; i++) {
			mIndicators[i] = createIndicator(unSelectedArray[i], titleArray[i],
					COLOR_UNSELECT, iconTagArray[i], textTagArray[i]);
			// mIndicators[i].setBackgroundResource(R.drawable.indic_select);
			mIndicators[i].setTag(Integer.valueOf(i));
			mIndicators[i].setOnClickListener(this);
			addView(mIndicators[i]);
		}
	}

	public static void setIndicator(int which) {
		// clear previous status.
		mIndicators[mCurIndicator].setBackgroundColor(Color.alpha(0));
		ImageView prevIcon;
		TextView prevText;

		prevIcon = (ImageView) mIndicators[mCurIndicator]
				.findViewWithTag(iconTagArray[mCurIndicator]);
		prevIcon.setImageResource(unSelectedArray[mCurIndicator]);
		prevText = (TextView) mIndicators[mCurIndicator]
				.findViewWithTag(textTagArray[mCurIndicator]);
		prevText.setTextColor(COLOR_UNSELECT);

		// update current status.
		mIndicators[which].setBackgroundResource(R.drawable.indic_select);
		ImageView currIcon;
		TextView currText;

		currIcon = (ImageView) mIndicators[which]
				.findViewWithTag(iconTagArray[which]);
		currIcon.setImageResource(selectedArray[which]);
		currText = (TextView) mIndicators[which]
				.findViewWithTag(textTagArray[which]);
		currText.setTextColor(COLOR_SELECT);

		mCurIndicator = which;
	}

	public interface OnIndicateListener {
		public void onIndicate(View v, int which);
	}

	public void setOnIndicateListener(OnIndicateListener listener) {
		mOnIndicateListener = listener;
	}

	@Override
	public void onClick(View v) {
		if (mOnIndicateListener != null) {
			int tag = (Integer) v.getTag();

			if (mCurIndicator != tag) {
				mOnIndicateListener.onIndicate(v, tag);
				setIndicator(tag);
			}

		}
	}
}
