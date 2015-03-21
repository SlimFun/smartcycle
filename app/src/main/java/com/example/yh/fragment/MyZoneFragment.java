package com.example.yh.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.example.yh.activity.ChangeDescribeActivity;
import com.example.yh.activity.ChangeTelphoneActivity;
import com.example.yh.activity.ChangeUserNameActivity;
import com.example.yh.context.MainApplication;
import com.example.yh.data.Constants;
import com.example.yh.data.User;
import com.example.yh.http.HttpHelper;
import com.example.yh.netword.RequestManager;
import com.example.yh.network.image.ImageCacheManager;
import com.example.yh.smartcycle.R;
import com.example.yh.util.Tools;
import com.example.yh.view.NetworkCircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Created by yh on 2015/2/10.
 */
public class MyZoneFragment extends Fragment implements ScreenShotable, View.OnClickListener {

    private View mRootView;
    private ImageView mIvSetUserName;
    private ImageView mIvSetSex;
    private ImageView mIvDescribe;
    private ImageView mIvTelphone;
    private TextView mTvUserName;
    private TextView mTvSex;
    private TextView mTvDescribe;
    private TextView mTvTelphone;
    private NetworkCircleImageView mIvFace;
    private TextView mTvBirthday;
    private ImageView mIvBirthday;
    private TextView mTvUpdateMessage;
    private TextView mTvLogout;
    private User user;
    private String tempSex;
    private String tempName;
    private String tempBirthday;
    private String tempTelphone;
    private String tempDiscribe;
    private String tempFace;
    private String tempBackGround;
    private RequestQueue mQueue = RequestManager.getRequestQueue();
    private static String upsetUserUrl = Constants.BASE_URL + "UpsetUserServlet";
    private static final int CHANGE_USERNAME_ACTI = 1;
    private static final int CHANGE_TELPHONE_ACTI = 2;
    private static final int CHANGE_DESCRIBE_ACTI = 3;
    private static final int PHOTO_REQUEST_CAMERA_FACE = 41;// 拍照
    private static final int PHOTO_REQUEST_GALLERY_FACE = 51;// 从相册中选择
    private static final int MSG_SUCC = 40;
    private static final int MSG_FL = 50;
    private static final int PHOTO_REQUEST_CUT_FACE = 6;// 结果
    private static final int CHANGE_FACE = 8;
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
//    private NetworkImageView mIvBackground;
    private ImageView mIvBackground;
    private Bitmap mFaceBitmap;
    private Bitmap mBackgroundBitmap;
    private File tempFile;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SUCC) {
                JSONObject result = (JSONObject) msg.obj;
                int status = result.optInt("status");
                if (status == 0) {
                    user.setUsername(tempName);
                    user.setSex(tempSex);
                    user.setTelphone(tempTelphone);
                    user.setBirthday(tempBirthday);
                    user.setDescription(tempDiscribe);
                    JSONObject jsonObject = result.optJSONObject("user");
                    user.setFaceUrl(jsonObject.optString("face"));
                    Toast.makeText(getActivity(),"上传成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(),"上传失败,可能是服务器端出问题了",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getActivity(),"上传失败",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_my_zone,container,false);
        init();
        findViews();
        initViews();
        setListener();
        return mRootView;
    }

    private void initViews() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(MainApplication.ScreenWidth, 9 * (MainApplication.ScreenWidth / 16));
        mIvBackground.setLayoutParams(lp);
        mTvDescribe.setText(cropText(tempDiscribe));
        if ("male".equals(tempSex)) {
            mTvSex.setText("男");
        }else {
            mTvSex.setText("女");
        }
        mTvTelphone.setText(tempTelphone);
        mTvBirthday.setText(tempBirthday);
        mTvUserName.setText(tempName);
        mIvFace.setDefaultImageResId(R.drawable.a1);
        if (tempFace != null) {
            mIvFace.setImageUrl(tempFace, ImageCacheManager.getInstance().getImageLoader());
        }
    }

    private void init() {
        mQueue = RequestManager.getRequestQueue();
        User.init(getActivity());
        user = User.getInstance();
        tempBirthday = user.getBirthday();
        tempDiscribe = user.getDescription();
        tempName = user.getUsername();
        tempSex = user.getSex();
        tempTelphone = user.getTelphone();
        tempFace = user.getFaceUrl();
    }

    private void setListener() {
        mIvTelphone.setOnClickListener(this);
        mIvFace.setOnClickListener(this);
        mIvSetSex.setOnClickListener(this);
        mIvDescribe.setOnClickListener(this);
        mIvSetUserName.setOnClickListener(this);
        mIvBirthday.setOnClickListener(this);
        mTvUpdateMessage.setOnClickListener(this);
        mIvFace.setOnClickListener(this);
        mTvLogout.setOnClickListener(this);
    }

    private void findViews() {
        mIvSetUserName = (ImageView) mRootView.findViewById(R.id.fragment_my_zone_iv_username);
        mIvDescribe = (ImageView) mRootView.findViewById(R.id.fragment_my_zone_iv_describe);
        mIvSetSex = (ImageView) mRootView.findViewById(R.id.fragment_my_zone_iv_sex);
        mIvTelphone = (ImageView) mRootView.findViewById(R.id.fragment_my_zone_iv_telphone);
        mIvFace = (NetworkCircleImageView) mRootView.findViewById(R.id.fragment_my_zone_iv_face);
        mTvSex = (TextView) mRootView.findViewById(R.id.fragment_my_zone_tv_sex);
        mTvDescribe = (TextView) mRootView.findViewById(R.id.fragment_my_zone_tv_describe);
        mTvTelphone = (TextView) mRootView.findViewById(R.id.fragment_my_zone_tv_telphone);
        mTvUserName = (TextView) mRootView.findViewById(R.id.fragment_my_zone_tv_username);
        mTvLogout = (TextView) mRootView.findViewById(R.id.fragment_my_zone_tv_logout);
        mTvUpdateMessage = (TextView) mRootView.findViewById(R.id.fragment_my_zone_tv_updateMessage);
        mTvBirthday = (TextView) mRootView.findViewById(R.id.fragment_my_zone_tv_birthday);
        mIvBirthday = (ImageView) mRootView.findViewById(R.id.fragment_my_zone_iv_birthday);
        mIvBackground = (ImageView) mRootView.findViewById(R.id.fragment_my_zone_iv_background);
    }

    @Override
    public void takeScreenShot() {
        getFragmentManager().beginTransaction().replace(R.id.content_frame, this).commit();
    }

    @Override
    public Bitmap getBitmap() {
        return null;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.fragment_my_zone_iv_describe:
                setDescibe();
                break;
            case R.id.fragment_my_zone_iv_sex:
                showSetSexDialog();
                break;
            case R.id.fragment_my_zone_iv_telphone:
                setTelphone();
                break;
            case R.id.fragment_my_zone_iv_birthday:
                showSetBirthdayDialog();
                break;
            case R.id.fragment_my_zone_iv_username:
                intent.setClass(getActivity(), ChangeUserNameActivity.class);
                startActivityForResult(intent,CHANGE_USERNAME_ACTI);
                break;
            case R.id.fragment_my_zone_tv_updateMessage:
                upsetUser();
                break;
            case R.id.fragment_my_zone_iv_face:
                setFace();
                break;
            case R.id.fragment_my_zone_tv_logout:
                logout();
                break;
        }
    }

    private void logout() {
        user.setLogin(false);
        user.logout();
    }

    private void setFace() {
        Dialog alertDialog =  new AlertDialog.Builder(getActivity()).
                setTitle("从本地获取头像").
                setMessage("选择获取图片方式").
                setPositiveButton("相册",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gallery(CHANGE_FACE);
                    }
                }).
                setNegativeButton("相机",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        camera(CHANGE_FACE);
                    }
                }).create();
        alertDialog.show();
    }

    private void camera(int type) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (Tools.hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        if(type == CHANGE_FACE){
            startActivityForResult(intent, PHOTO_REQUEST_CAMERA_FACE);
        }
    }

    private void gallery(int type) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if(type == CHANGE_FACE){
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY_FACE);
        }
    }

    private void upsetUser() {
//        UpsetUserServlet?userId=%s&username=%s&sex=%s&birthday=%s&descibe=%s
//        String url = String.format(upsetUserUrl, user.getId(), tempName, tempSex, tempBirthday, tempDiscribe, tempTelphone,tempFace,tempBackGround);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject jsonObject) {
//                        int status = jsonObject.optInt("status");
//                        if (status == 0) {
//                            user.setUsername(tempName);
//                            user.setSex(tempSex);
//                            user.setBackgroundUrl(tempBackGround);
//                            user.setFaceUrl(tempFace);
//                            user.setDescription(tempDiscribe);
//                            user.setBirthday(tempBirthday);
//                            user.setTelphone(tempTelphone);
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//
//                    }
//                });
//        mQueue.add(jsonObjectRequest);

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "更改信息", "上传数据...");
        String faceString = "";

        if (mFaceBitmap != null) {
            faceString = Tools.bitmap2String(mFaceBitmap);
        }
        final String param = new StringBuffer()
                .append("id=")
                .append(user.getId())
                .append("&username=")
                .append(tempName)
                .append("&sex=")
                .append(tempSex)
                .append("&birthday=")
                .append(tempBirthday)
                .append("&telphone=")
                .append(tempTelphone)
                .append("&description=")
                .append(tempDiscribe)
                .append("&faceString=")
                .append(faceString)
                .toString();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String json = HttpHelper.sendPost(upsetUserUrl, param);
                JSONObject result;
                dialog.dismiss();
                Message msg = new Message();
                try {
                    result = new JSONObject(json);
                    msg.obj = result;
                    msg.what = MSG_SUCC;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    msg.what = MSG_FL;
                    e.printStackTrace();
                    handler.sendMessage(msg);
                }

            }
        });
    }

    private void setDescibe() {
        Intent intent = new Intent();
        intent.putExtra("descibe", tempDiscribe);
        intent.setClass(getActivity(), ChangeDescribeActivity.class);
        startActivityForResult(intent, CHANGE_DESCRIBE_ACTI);
    }

    private void showSetBirthdayDialog() {
        int year,month,day;
        if (TextUtils.isEmpty(tempBirthday)) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH)+1;
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }else{
            String[] time = tempBirthday.split("-");
            year = Integer.parseInt(time[0]);
            month = Integer.parseInt(time[1]);
            day = Integer.parseInt(time[2]);
        }
        final DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tempBirthday = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                mTvBirthday.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
            }
        },year , (month-1), day);
        dialog.show();
    }

    private void showSetSexDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_select_sex);
        dialog.setTitle("性别");
        final RelativeLayout llMale = (RelativeLayout) dialog.findViewById(R.id.dialog_select_sex_layout_male);
        RelativeLayout llFemale = (RelativeLayout) dialog.findViewById(R.id.dialog_select_sex_layout_female);
        final ImageView ivMale = (ImageView) dialog.findViewById(R.id.dialog_select_sex_iv_male);
        final ImageView ivFemale = (ImageView) dialog.findViewById(R.id.dialog_select_sex_iv_female);
        if ("male".equals(tempSex)) {
            ivMale.setVisibility(View.VISIBLE);
        }else if("female".equals(tempSex)){
            ivFemale.setVisibility(View.VISIBLE);
        }
        llMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivMale.setVisibility(View.VISIBLE);
                ivFemale.setVisibility(View.GONE);
                dialog.dismiss();
                tempSex = "male";
                mTvSex.setText("男");
            }
        });
        llFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivFemale.setVisibility(View.VISIBLE);
                ivMale.setVisibility(View.GONE);
                dialog.dismiss();
                tempSex = "female";
                mTvSex.setText("女");
            }
        });
        dialog.show();
    }

    private void setTelphone() {
        Intent intent = new Intent();
        intent.putExtra("telphone", tempTelphone);
        intent.setClass(getActivity(), ChangeTelphoneActivity.class);
        startActivityForResult(intent, CHANGE_TELPHONE_ACTI);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case CHANGE_USERNAME_ACTI:
                if (resultCode == ChangeUserNameActivity.SUCC) {
                    tempName = data.getStringExtra("username");
                    mTvUserName.setText(tempName);
                }
                break;
            case CHANGE_TELPHONE_ACTI:
                if (resultCode == ChangeTelphoneActivity.SUCC) {
                    tempTelphone = data.getStringExtra("telphone");
                    mTvTelphone.setText(tempTelphone);
                }
                break;
            case CHANGE_DESCRIBE_ACTI:
                if (resultCode == ChangeDescribeActivity.SUCC) {
                    tempDiscribe = data.getStringExtra("describe");
                    mTvDescribe.setText(cropText(tempDiscribe));
                }
                break;
            case PHOTO_REQUEST_GALLERY_FACE:
                if (data != null) {
                    Uri uri = data.getData();
                    crop(uri,CHANGE_FACE);
                }
                break;

            case PHOTO_REQUEST_CAMERA_FACE:
                if (Tools.hasSdcard()) {
                    tempFile = new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME);
                    crop(Uri.fromFile(tempFile),CHANGE_FACE);
                }else{
                    Toast.makeText(getActivity(), "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
                }
                break;
            case PHOTO_REQUEST_CUT_FACE:
                mFaceBitmap = data.getParcelableExtra("data");
                mIvFace.setImageBitmap(mFaceBitmap);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void crop(Uri uri,int type) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        if(type == CHANGE_FACE){
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);

            intent.putExtra("outputX", 250);
            intent.putExtra("outputY", 250);
        }else{
            intent.putExtra("aspectX", 16);
            intent.putExtra("aspectY", 9);
            // 裁剪后输出图片的尺寸大小
            intent.putExtra("outputX", MainApplication.ScreenWidth);
            intent.putExtra("outputY", 9.0F * (MainApplication.ScreenWidth / 16.0F));
        }
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        if (type == CHANGE_FACE) {
            startActivityForResult(intent, PHOTO_REQUEST_CUT_FACE);
        }
    }

    private String cropText(String tempDiscribe) {
        if (TextUtils.isEmpty(tempDiscribe)) {
            return "";
        }else if(tempDiscribe.length() <= 10) {
            return tempDiscribe;
        }else {
            return tempDiscribe.substring(0, 10) + "..." ;
        }
    }
}
