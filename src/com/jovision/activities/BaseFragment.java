package com.jovision.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.activities.JVTabActivity.OnTabListener;

/**
 * Fragment 基类
 */
public class BaseFragment extends Fragment implements OnTabListener {
	private final String TAG = "BaseFragment";
	protected View mParent;
	protected FragmentActivity mActivity;

	/** topBar */
	protected Button leftBtn;
	protected TextView currentMenu;
	protected Button rightBtn;

	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */
	public static JVMyDeviceFragment newInstance(int index) {
		JVMyDeviceFragment f = new JVMyDeviceFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);
		return f;
	}

	public int getShownIndex() {
		return getArguments().getInt("index", 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mydevice, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = getActivity();
		mParent = getView();

		leftBtn = (Button) mParent.findViewById(R.id.btn_left);
		currentMenu = (TextView) mParent.findViewById(R.id.currentmenu);
		rightBtn = (Button) mParent.findViewById(R.id.btn_right);
		currentMenu.setText(mActivity.getResources().getStringArray(
				R.array.titles)[0]);
		leftBtn.setOnClickListener(mOnClickListener);

	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.btn_left:
				((BaseActivity) mActivity).openExitDialog();
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onTabAction(int what, int arg1, int arg2, Object obj) {
	}

}
