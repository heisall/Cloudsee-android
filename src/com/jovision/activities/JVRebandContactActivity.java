package com.jovision.activities;

import java.io.File;
import java.io.FileOutputStream;

import m.framework.network.StringPart;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint.Join;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.test.JVACCOUNT;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.AccountUtil;
import com.jovision.utils.MobileUtil;
import com.jovision.views.popw;
import com.tencent.stat.StatService;

public class JVRebandContactActivity extends BaseActivity {

	private TextView rebandPhone;
	private TextView rebandEmail;
	private ImageView rebandHeadImg;
	private LinearLayout linear;
	private RelativeLayout rebindphoneLayout;
	private RelativeLayout rebindmaiLayout;
	private RelativeLayout rebindnickname;
	private TextView reband_nickname_text;

	private String showPhone = "";
	private String showEmail = "";
	private String showNickname = "";

	private Dialog resetDialog;// 显示弹出框
	private TextView resetCancel;// 取消按钮
	private TextView resetCompleted;// 确定按钮
	private EditText rebind_nicknametext;

	// 设置头像

	private String more_name;// 用户名
	private popw popupWindow; // 声明PopupWindow对象；
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
	private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	// 存放头像的文件夹
	File file;
	// 旧头像文件
	File tempFile;
	// 新头像文件
	File newFile;

	// popupWindow滑出布局

	@Override
	public void onHandler(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotify(int what, int arg1, int arg2, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		if (tempFile.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(Consts.HEAD_PATH
					+ more_name + ".jpg");
			rebandHeadImg.setImageBitmap(bitmap);
		}
		if (!"".equals(MySharedPreference.getString("REBINDPHONE"))
				&& null != MySharedPreference.getString("REBINDPHONE")) {
			rebandPhone.setText(MySharedPreference.getString("REBINDPHONE"));
		}
		if (!"".equals(MySharedPreference.getString("REBINDEMAIL"))
				&& null != MySharedPreference.getString("REBINDEMAIL")) {
			rebandEmail.setText(MySharedPreference.getString("REBINDEMAIL"));
		}
		super.onResume();
	}

	@Override
	protected void initSettings() {
		// TODO Auto-generated method stub

		Intent intent = getIntent();
		showPhone = intent.getStringExtra("phone");
		showEmail = intent.getStringExtra("email");
		showNickname = intent.getStringExtra("nickname");
		more_name = intent.getStringExtra("username");
	}

	@Override
	protected void initUi() {
		// TODO Auto-generated method stub
		setContentView(R.layout.rebandcontact);

		leftBtn = (Button) findViewById(R.id.btn_left);
		rightBtn = (Button) findViewById(R.id.btn_right);
		rightBtn.setVisibility(View.GONE);
		currentMenu = (TextView) findViewById(R.id.currentmenu);
		currentMenu.setText(getResources().getString(R.string.rebindcontact));

		reband_nickname_text = (TextView)findViewById(R.id.reband_nickname_text);
		rebindnickname = (RelativeLayout)findViewById(R.id.rebind_nickname);
		rebandEmail = (TextView) findViewById(R.id.reband_email_text);
		rebandPhone = (TextView) findViewById(R.id.reband_phone_text);
		rebandHeadImg = (ImageView) findViewById(R.id.reband_hand_img);
		rebindphoneLayout = (RelativeLayout) findViewById(R.id.rebind_phone);
		rebindmaiLayout = (RelativeLayout) findViewById(R.id.rebind_mail);
		linear = (LinearLayout) findViewById(R.id.lin);

		if (!"".equals(showNickname)) {
			reband_nickname_text.setText(showNickname);
		}else {
			reband_nickname_text.setText("未填写");
		}

		if (showPhone.equals("nophone")) {
			rebandPhone
			.setText(getResources().getString(R.string.rebindhasnot));
		} else {
			rebandPhone.setText(showPhone);
		}
		if (showEmail.equals("noemail")) {
			rebandEmail
			.setText(getResources().getString(R.string.rebindhasnot));
		} else {
			rebandEmail.setText(showEmail);
		}
		JVACCOUNT.GetAccountInfo();
		file = new File(Consts.HEAD_PATH);
		MobileUtil.createDirectory(file);
		tempFile = new File(Consts.HEAD_PATH + more_name + ".jpg");
		newFile = new File(Consts.HEAD_PATH + more_name + "1.jpg");

		rebandHeadImg.setOnClickListener(myOnClickListener);
		leftBtn.setOnClickListener(myOnClickListener);
		rebindphoneLayout.setOnClickListener(myOnClickListener);
		rebindmaiLayout.setOnClickListener(myOnClickListener);
		rebindnickname.setOnClickListener(myOnClickListener);
		
		

	}

	private void ResetDialog() {
		resetDialog = new Dialog(JVRebandContactActivity.this, R.style.mydialog);
		View view = LayoutInflater.from(JVRebandContactActivity.this).inflate(
				R.layout.dialog_rebind, null);
		resetDialog.setContentView(view);

		rebind_nicknametext = (EditText)view.findViewById(R.id.rebind_nicknametext);
		resetCancel = (TextView) view.findViewById(R.id.reset_cancel);
		resetCompleted = (TextView) view.findViewById(R.id.reset_completed);

		resetCancel.setOnClickListener(myOnClickListener);
		resetCompleted.setOnClickListener(myOnClickListener);
		resetDialog.show();

	}

	
	OnClickListener myOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_left:
				finish();
				break;
			case R.id.pop_outside:
				popupWindow.dismiss();
				break;
			case R.id.reset_cancel:
				resetDialog.dismiss();
				break;
			case R.id.reset_completed:
				// reqJson:{"user":"111","phone":"18668923911","mail":"","nick":"nicheng"}
				if ("".equals(rebind_nicknametext.getText().toString())) {
					showTextToast(R.string.str_nikename_notnull);
				}else {
				JSONObject resObject = new JSONObject();
				try {
					resObject.put("user", more_name);
					resObject.put("phone", showPhone);
					resObject.put("mail", showEmail);
					resObject.put("nick", rebind_nicknametext.getText().toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SetAccountInfoTask task = new SetAccountInfoTask();
				String params [] = new String [3];
				params [0] = resObject.toString();
				task.execute(params);
				Log.i("TAG", params[0]);
				}
				break;
			case R.id.rebind_nickname:
				if (!"".equals(reband_nickname_text.getText().toString())) {
					showTextToast(R.string.edit_pass_not);
				}else {
					ResetDialog();
				}
				break;
			case R.id.reband_hand_img:
				StatService.trackCustomEvent(
						JVRebandContactActivity.this,
						"census_moreheadimg",
						JVRebandContactActivity.this.getResources().getString(
								R.string.census_moreheadimg));
				popupWindow = new popw(JVRebandContactActivity.this,
						myOnClickListener);
				popupWindow.setBackgroundDrawable(null);
				popupWindow.setOutsideTouchable(true);
				popupWindow.showAtLocation(linear, Gravity.BOTTOM
						| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				break;

			case R.id.btn_pick_photo: {
				popupWindow.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
				break;
			}
			case R.id.btn_take_photo:
				// 调用系统的拍照功能
				popupWindow.dismiss();
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				// 指定调用相机拍照后照片的储存路径
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
				startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
				break;
			case R.id.btn_cancel:
				popupWindow.dismiss();
				break;
			case R.id.rebind_phone:
				Intent intentEmail = new Intent(JVRebandContactActivity.this,
						JVRebandPhoneorEmailActivity.class);
				intentEmail.putExtra("PhoneEmail", "Phone");
				intentEmail.putExtra("isphone", 1);
				startActivity(intentEmail);
				break;

			case R.id.rebind_mail:
				Intent intentPhone = new Intent(JVRebandContactActivity.this,
						JVRebandPhoneorEmailActivity.class);
				intentPhone.putExtra("PhoneEmail", "Email");
				intentPhone.putExtra("isphone", 0);
				startActivity(intentPhone);
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			if (resultCode == -1) {
				startPhotoZoom(Uri.fromFile(newFile), 300);
			}
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData(), 300);
			break;

		case PHOTO_REQUEST_CUT:
			if (data != null)
				setPicToView(data);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	// 将进行剪裁后的图片显示到UI界面上
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			saveBitmap(photo);
			Drawable drawable = new BitmapDrawable(photo);
			rebandHeadImg.setBackgroundDrawable(drawable);
		}
	}

	public void saveBitmap(Bitmap bm) {
		if (null == bm) {
			return;
		}
		File f = new File(Consts.HEAD_PATH + more_name + ".jpg");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void saveSettings() {

	}

	@Override
	protected void freeMe() {

	}
	class SetAccountInfoTask extends AsyncTask<String, Integer, Integer> {

		@Override
		protected Integer doInBackground(String... params) {
			// TODO Auto-generated method stub
			createDialog("", false);
			int ret = -1;
			ret = JVACCOUNT.SetAccountInfo(params[0]);
			return ret;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (result == 0)// ok
			{
				dismissDialog();
				resetDialog.dismiss();
				reband_nickname_text.setText(rebind_nicknametext.getText().toString());
			}
		}

		@Override
		protected void onPreExecute() {
			// 任务启动，可以在这里显示一个对话框，这里简单处理,当任务执行之前开始调用此方法，可以在这里显示进度对话框。
		}
	}
}
