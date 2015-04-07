
package com.jovision.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.test.JVACCOUNT;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovetech.CloudSee.temp.R;
import com.jovision.Consts;
import com.jovision.commons.MySharedPreference;
import com.jovision.utils.MobileUtil;
import com.jovision.views.popw;
import com.tencent.stat.StatService;

import java.io.File;
import java.io.FileOutputStream;

public class JVRebandContactActivity extends BaseActivity {

    private TextView rebandPhone;
    private TextView rebandEmail;
    private ImageView rebandHeadImg;
    private LinearLayout linear;
    private RelativeLayout rebindphoneLayout;
    private RelativeLayout rebindmaiLayout;
    private RelativeLayout rebindnickname;
    private TextView reband_nickname_text;
    private ImageView nickImageView;

    private String showPhone = "";
    private String showEmail = "";
    private String showNickname = "";

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
        if (!"".equals(MySharedPreference.getString("NICKNAMEBBS"))) {
            reband_nickname_text.setText(MySharedPreference
                    .getString("NICKNAMEBBS"));
            nickImageView.setVisibility(View.GONE);
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

        nickImageView = (ImageView) findViewById(R.id.rebindnicknameimg);
        reband_nickname_text = (TextView) findViewById(R.id.reband_nickname_text);
        rebindnickname = (RelativeLayout) findViewById(R.id.rebind_nickname);
        rebandEmail = (TextView) findViewById(R.id.reband_email_text);
        rebandPhone = (TextView) findViewById(R.id.reband_phone_text);
        rebandHeadImg = (ImageView) findViewById(R.id.reband_hand_img);
        rebindphoneLayout = (RelativeLayout) findViewById(R.id.rebind_phone);
        rebindmaiLayout = (RelativeLayout) findViewById(R.id.rebind_mail);
        linear = (LinearLayout) findViewById(R.id.lin);

        MySharedPreference.putString("NICKNAMEBBS", "");
        if (!"".equals(showNickname)) {
            reband_nickname_text.setText(showNickname);
            nickImageView.setVisibility(View.GONE);
        } else {
            reband_nickname_text.setText(getResources().getString(
                    R.string.rebindnicknamenull));
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
                case R.id.rebind_nickname:
                    if (View.GONE == nickImageView.getVisibility()) {
                        showTextToast(R.string.edit_pass_not);
                    } else {
                        Intent nickIntent = new Intent(
                                JVRebandContactActivity.this,
                                JVRebandNickActivity.class);
                        nickIntent.putExtra("phone", showPhone);
                        nickIntent.putExtra("email", showEmail);
                        nickIntent.putExtra("username", more_name);
                        startActivity(nickIntent);
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
}
