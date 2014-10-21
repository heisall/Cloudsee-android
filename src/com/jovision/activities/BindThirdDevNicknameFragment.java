package com.jovision.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jovetech.CloudSee.temp.R;
import com.jovision.utils.RegularUtil;

public class BindThirdDevNicknameFragment extends Fragment implements
		OnClickListener {

	private View rootView;// 缓存Fragment view
	private Button completeBtn;
	private EditText nickNameEdt;
	private String nickName;

	public interface OnSetNickNameListener {
		public void OnSetNickName(String strNickName);
	}

	private OnSetNickNameListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnSetNickNameListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnSetNickNameListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.bind_thirddev_nick_fragment,
					container, false);
		}
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		completeBtn = (Button) rootView.findViewById(R.id.complete_btn);
		completeBtn.setOnClickListener(this);
		nickNameEdt = (EditText) rootView.findViewById(R.id.third_dev_nick_edt);
		nickNameEdt.setFocusable(true);

		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.complete_btn:
			nickName = nickNameEdt.getText().toString().trim();
			if (nickName.equals("")) {
				Toast.makeText(getActivity(), "请输入昵称", Toast.LENGTH_SHORT)
						.show();
			} else {
				if (!RegularUtil.checkNickName(nickName)) {
					showToast("输入的昵称不合法，请重新输入", Toast.LENGTH_SHORT);
					return;
				}
				mListener.OnSetNickName(nickName);
			}
			break;

		default:
			break;
		}
	}

	private void showToast(String text, int duration) {
		Toast.makeText(getActivity(), text, duration).show();
	}
}
