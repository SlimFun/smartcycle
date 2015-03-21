package com.example.yh.activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yh.adapter.ColorSelectAdapter;
import com.example.yh.adapter.FontSelectAdapter;
import com.example.yh.adapter.FontSelectPagerAdapter;
import com.example.yh.context.MainApplication;
import com.example.yh.data.Constants;
import com.example.yh.data.DynamicInfo;
import com.example.yh.data.User;
import com.example.yh.http.HttpHelper;
import com.example.yh.smartcycle.R;
import com.example.yh.util.Tools;
import com.example.yh.view.RevealBackgroundView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SendDynamicInfoActivity extends ActionBarActivity implements RevealBackgroundView.OnStateChangeListener {

    private EditText mEtContent;
    private LinearLayout mLlImage;
    private LinearLayout mLlFont;
    private LinearLayout mLlSend;
    private File tempFile;
    private DynamicInfo info;
    private Bitmap mImageContent;
    private User mUser;
    private static final int MSG_SUC = 999;
    private static final int MSG_FL = 888;
    public static final int SUCC = 400;
    public static final int FL = 500;
    public static final int SEND_INFO = 9;
    private ImageView mIvBack;
    private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    public static final String STARTING_LOCATION = "startingLoaction";
    private static final String uploadUrl = Constants.BASE_URL + "UploadDynamicInfoServlet";
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private int fontSize = 16;
    private String typeface;
    private LinearLayout mLlBotton;
    private int mBottonHeight;
    private boolean isBottomVisible = false;
    private int color = Constants.colors.get(0);
    private static final String TAG = "SendDynamicInfoActivity";
    private boolean pendingIntro = false;
    private RelativeLayout mRlActionBar;
    private FrameLayout mFlContent;
    private RevealBackgroundView mBackground;
    private static final int ACTION_BAR_TRANSLATIONY = Tools.dpToPx(48);

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_SUC) {
                JSONObject result = (JSONObject) msg.obj;
                int status = result.optInt("status");
                if(status == 0){
                    Toast.makeText(SendDynamicInfoActivity.this,"发表成功",Toast.LENGTH_SHORT).show();
                    setResult(SUCC);
                }else{
                    Toast.makeText(SendDynamicInfoActivity.this,"发表失败，可能是服务器端出错了",Toast.LENGTH_SHORT).show();
                    setResult(FL);
                }
            }else if(msg.what == MSG_FL){
                Toast.makeText(SendDynamicInfoActivity.this,"发表失败",Toast.LENGTH_SHORT).show();
                setResult(FL);
            }
            finish();
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_dynamic_info);
        info = new DynamicInfo();
        User.init(getApplicationContext());
        mUser = User.getInstance();
        findViews();
        setUpRevealBackground(savedInstanceState);
        setView();
        initView();
        setListeners();
    }

    private void setUpRevealBackground(Bundle savedInstanceState) {
        mBackground.setFillPaintColor(0xFFEAEAEA);
        mBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLoaction = getIntent().getIntArrayExtra(STARTING_LOCATION);
            mBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    mBackground.startFromLocation(startingLoaction);
                    return true;
                }
            });
        }else{
            mBackground.setToFinishedFrame();
        }
    }

    private void initView() {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mLlBotton.measure(w, h);
        mBottonHeight = mLlBotton.getMeasuredHeight();
        mLlBotton.setTranslationY(mBottonHeight);
    }

    private void setListeners() {
        mLlImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog alertDialog = new AlertDialog.Builder(SendDynamicInfoActivity.this)
                        .setTitle("从本地获得头像")
                        .setMessage("选择获取图片方式")
                        .setPositiveButton("相册",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                gallery();
                            }
                        })
                        .setNegativeButton("相机",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                camera();
                            }
                        }).create();
                alertDialog.show();
            }
        });

        mLlFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final Dialog dialog = new Dialog(SendDynamicInfoActivity.this);
//                dialog.setContentView(R.layout.dialog_select_font);
//                dialog.setTitle("选择字体");
//                GridView gridView = (GridView) dialog.findViewById(R.id.dialog_select_font_gridview);
//                final FontSelectAdapter adapter = new FontSelectAdapter(SendDynamicInfoActivity.this,fontSize);
//                gridView.setAdapter(adapter);
//                SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.dialog_select_font_seekbar);
//                final TextView tvSize = (TextView) dialog.findViewById(R.id.dialog_select_font_size_text);
                viewBottom();
                final TextView tvSize = (TextView) findViewById(R.id.acti_sendDynamicInfo_botton_select_font_size_text);
                SeekBar seekBar = (SeekBar) findViewById(R.id.acti_sendDynamicInfo_botton_select_font_seekbar);
                tvSize.setText(fontSize + "");
                seekBar.setProgress(fontSize * 100 / 32);

//                ViewPager viewPager = (ViewPager) dialog.findViewById(R.id.dialog_select_font_viewpager);
                ViewPager viewPager = (ViewPager) findViewById(R.id.acti_sendDynamicInfo_botton_select_font_viewpager);
                List<View> list = new ArrayList<>();
                for (int i = 0; i < 2; i++) {
                    list.add(getView(i));
                }
                Log.e(TAG, list.size()+"");
                FontSelectPagerAdapter pagerAdapter = new FontSelectPagerAdapter(list);
                viewPager.setAdapter(pagerAdapter);
//                final ImageView tip1 = (ImageView) dialog.findViewById(R.id.dialog_select_font_iv_tip1);
//                final ImageView tip2 = (ImageView) dialog.findViewById(R.id.dialog_select_font_iv_tip2);
                final ImageView tip1 = (ImageView) findViewById(R.id.acti_sendDynamicInfo_botton_select_font_iv_tip1);
                final ImageView tip2 = (ImageView) findViewById(R.id.acti_sendDynamicInfo_botton_select_font_iv_tip2);
                viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        if (position == 0) {
                            tip1.setImageResource(R.drawable.page_indicator_unfocused);
                            tip2.setImageResource(R.drawable.page_indicator_focused);
                        } else {
                            tip1.setImageResource(R.drawable.page_indicator_focused);
                            tip2.setImageResource(R.drawable.page_indicator_unfocused);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
//                GridView colorGridView = (GridView) dialog.findViewById(R.id.dialog_select_font_color_gridview);
//                ColorSelectAdapter colorAdapter = new ColorSelectAdapter(SendDynamicInfoActivity.this);
//                colorGridView.setAdapter(colorAdapter);

//                dialog.show();

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        fontSize = 32 * progress / 100;
                        tvSize.setText(fontSize + "");
                        tvSize.setTextSize(fontSize);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
//                TextView tvOk = (TextView) dialog.findViewById(R.id.dialog_select_font_tv_ok);
                TextView tvOk = (TextView) findViewById(R.id.acti_sendDynamicInfo_botton_select_font_tv_ok);
                tvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (typeface != null && !typeface.trim().equals("")) {
                            Typeface tf = Typeface.createFromAsset(getAssets(), typeface);
                            mEtContent.setTypeface(tf);
                        }
                        mEtContent.setTextSize(fontSize);
                        mEtContent.setTextColor(color);
                        mEtContent.setHintTextColor(color);
                        hitBottom();
//                        dialog.dismiss();
                    }
                });
//                TextView tvNo = (TextView) dialog.findViewById(R.id.dialog_select_font_tv_no);
                TextView tvNo = (TextView) findViewById(R.id.acti_sendDynamicInfo_botton_select_font_tv_no);
                tvNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        dialog.dismiss();
                        Toast.makeText(SendDynamicInfoActivity.this,"no",Toast.LENGTH_SHORT).show();
                        hitBottom();
                    }
                });

            }
        });

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(FL);
                finish();
            }
        });

        mLlSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDynamicInfo();
            }
        });
    }

    private void hitBottom() {
        Toast.makeText(SendDynamicInfoActivity.this,isBottomVisible+"",Toast.LENGTH_SHORT).show();
        if (isBottomVisible) {
            mLlBotton.animate().translationY(mBottonHeight).setDuration(1000).start();
            isBottomVisible = false;
        }
    }

    private void viewBottom(){
        if(!isBottomVisible){
            mLlBotton.animate().translationY(0f).setDuration(1000).start();
            isBottomVisible = true;
        }
    }

    private View getView(int i) {
        View view = LayoutInflater.from(SendDynamicInfoActivity.this).inflate(R.layout.include_font_gridview, null);
        GridView gridView = (GridView) view.findViewById(R.id.dialog_select_font_color_gridview);
        final BaseAdapter adapter;
        if (i == 0) {
            adapter = new FontSelectAdapter(SendDynamicInfoActivity.this);
        }else {
            adapter = new ColorSelectAdapter(SendDynamicInfoActivity.this);
        }

        gridView.setAdapter(adapter);
        gridView.setSelector(new ColorDrawable(Color.WHITE));
        if (i == 0) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    typeface = (String) adapter.getItem(position);
                    view.setAlpha(100);
                }
            });
        }else{
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    color = getResources().getColor(Constants.colors.get(position));
                }
            });
        }

        return gridView;
    }

    @Override
    public void onBackPressed() {
        setResult(FL);
        finish();
    }

    private void uploadDynamicInfo() {
        final Dialog dialog = ProgressDialog.show(this,"发表动态","发表动态");
        dialog.show();
        String imageString = "";
        imageString = Tools.bitmap2String(mImageContent);
        String username = mUser.getUsername();
        String sex = mUser.getSex();
        String attrs = mUser.getAttrs();
        String userId = mUser.getId();
        final String param = new StringBuffer()
                .append("username=")
                .append(username)
                .append("&sex=")
                .append(sex)
                .append("&attrs=")
                .append(attrs)
                .append("&userId=")
                .append(userId)
                .append("&imageUrl=")
                .append(imageString)
                .append("&time=")
                .append(System.currentTimeMillis()+"")
                .append("&content=")
                .append(mEtContent.getText().toString())
                .append("&userFace=")
                .append(mUser.getFaceUrl())
                .append("&fontSize=")
                .append(fontSize)
                .append("&fontColor=")
                .append(color)
                .append("&fontType=")
                .append(typeface)
                .toString();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                String json = HttpHelper.sendPost(uploadUrl, param);
                Log.e(TAG, json);
                JSONObject result;
                dialog.dismiss();
                Message msg = new Message();
                try {
                    if (json != null) {
                        result = new JSONObject(json);
                        msg.obj = result;
                        msg.what = MSG_SUC;
                        handler.sendMessage(msg);
                    }else{

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = MSG_FL;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if(hasSdCard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent,PHOTO_REQUEST_CAMERA);
    }

    private boolean hasSdCard() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    private void gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PHOTO_REQUEST_GALLERY){
            Uri uri = data.getData();
            crop(uri);
        }else if(requestCode == PHOTO_REQUEST_CAMERA){
            if(hasSdCard()) {
                tempFile = new File(Environment.getExternalStorageDirectory(),
                        PHOTO_FILE_NAME);
                crop(Uri.fromFile(tempFile));
            }else{
                Toast.makeText(SendDynamicInfoActivity.this,"未找到储存卡，无法储存照片!",Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == PHOTO_REQUEST_CUT) {
            if(data != null){
                mImageContent = data.getParcelableExtra("data");
                mEtContent.setBackground(new BitmapDrawable(mImageContent));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 16);
        intent.putExtra("aspectY", 9);
        intent.putExtra("outputX",MainApplication.ScreenWidth);
        intent.putExtra("outputY", 9.0F * (MainApplication.ScreenWidth / 16.0F));

        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent,PHOTO_REQUEST_CUT);
    }

    private void findViews() {
        mEtContent = (EditText) findViewById(R.id.acti_sendDynamicInfo_textContent);
        mLlImage = (LinearLayout) findViewById(R.id.acti_sendDynamicINfo_ll_image);
        mLlFont = (LinearLayout) findViewById(R.id.acti_sendDynamicINfo_ll_font);
        mLlSend = (LinearLayout) findViewById(R.id.acti_sendDynamicINfo_ll_send);
        mIvBack = (ImageView) findViewById(R.id.acti_send_dynamic_info_back);
        mLlBotton = (LinearLayout) findViewById(R.id.acti_sendDynamicInfo_ll_botton);
        mRlActionBar = (RelativeLayout) findViewById(R.id.acti_sendDynamicInfo_rl_actionbar);
        mFlContent = (FrameLayout) findViewById(R.id.acti_send_dynamic_info_framelayout);
        mBackground = (RevealBackgroundView) findViewById(R.id.vRevealBackground);
    }

    private void setView() {

        RelativeLayout.LayoutParams localObject = new RelativeLayout.LayoutParams(MainApplication.ScreenWidth, (int) (9.0F * (MainApplication.ScreenWidth / 16.0F)));
        mEtContent.setLayoutParams(localObject);

        mRlActionBar.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRlActionBar.getViewTreeObserver().removeOnPreDrawListener(this);
                pendingIntro = true;
                mRlActionBar.setTranslationY(-ACTION_BAR_TRANSLATIONY);
                mFlContent.setAlpha(0);
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_dynamic_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStateChange(int state) {
        if (state == RevealBackgroundView.STATE_FINISHED) {
            if (pendingIntro) {
                 startIntroAnimation();
            }
        }
    }

    private void startIntroAnimation() {
        Log.e(TAG, "startIntroAnimation");
        mRlActionBar.animate()
                .translationY(0)
                .setDuration(400)
                .setInterpolator(new AccelerateInterpolator())
                .start();
        mFlContent.animate()
                .alpha(255)
                .setDuration(400)
                .setInterpolator(new AccelerateInterpolator())
                .start();
    }
}
