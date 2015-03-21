package com.example.yh.activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.navisdk.BNaviEngineManager;
import com.baidu.navisdk.BaiduNaviManager;
import com.example.yh.context.MainApplication;
import com.example.yh.data.Constants;
import com.example.yh.data.User;
import com.example.yh.factory.FragmentFactory;
import com.example.yh.fragment.MainFragment;
import com.example.yh.smartcycle.R;
import com.example.yh.util.Tools;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;




public class MainActivity extends ActionBarActivity implements ViewAnimator.ViewAnimatorListener {
    private static final String TAG = "MainActivity";

    private static final int FAB_TRANSLATIONY = 80;
    private static final int ACTION_BAR_TRANSLATIONY = 56;

    private static final int ANIM_DURING_ACTION_BAR = 300;
    private static final int ANIM_DURING_FAB_BUTTON = 400;
    private static final int ANIM_DURING_CONTENT = 400;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> list = new ArrayList<>();
    private ScreenShotable contentFragment;
    private ViewAnimator viewAnimator;
    private int res = R.drawable.content_music;
    private LinearLayout linearLayout;
    private List<ScreenShotable> fragments = new ArrayList<>();
    private User user;
    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private long firstTime;
    private PushAgent mPushAgent;
    private String mDeviceToken;
    private Toolbar mToolBar;
    private boolean pendingIntroAnimation;
    private RelativeLayout mRlEditInfo;
    private ImageView mIvEditInfo;
    private RelativeLayout mRlHeadLayout;
    private ImageView mIvBackground;
    private int headLayoutTranslationY = 607;
    private int FABButtonTranslationY = 360;
    public static boolean pendingLoading = true;

    private FragmentFactory.FragmentType mType = FragmentFactory.FragmentType.MAP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }else{
            pendingIntroAnimation = false;
        }

        mDeviceToken = mPushAgent.getRegistrationId();
//        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//        if (!Tools.isEmpty(mDeviceToken)) {
//            clipboardManager.setText(mDeviceToken);
//            Toast.makeText(MainActivity.this,"DeviceToken复制成功",Toast.LENGTH_SHORT).show();
//        }

        User.init(getApplicationContext());
        user = User.getInstance();

        initLocationClient();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            contentFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, (Fragment) contentFragment)
                    .commit();
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        linearLayout = (LinearLayout) findViewById(R.id.left_drawer);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });


        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, list, contentFragment, drawerLayout, this);
        Constants.ACTION_BAR_HEIGHT = getSupportActionBar().getHeight();
        TypedValue tv = new TypedValue();
        if (getApplicationContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            Constants.ACTION_BAR_HEIGHT = TypedValue.complexToDimensionPixelSize(tv.data, getApplicationContext().getResources().getDisplayMetrics()) +11;
        }

        Toast.makeText(MainActivity.this,mLocationClient.isStarted()+"",Toast.LENGTH_SHORT).show();



    }

    private void findViews() {
        mRlEditInfo = (RelativeLayout) findViewById(R.id.fragment_main_rl_edti_info);
        mIvEditInfo = (ImageView) findViewById(R.id.fragment_main_iv_edit_info);
        mIvBackground = (ImageView) findViewById(R.id.imageView);
        mRlHeadLayout = (RelativeLayout) findViewById(R.id.acti_main_head_layout);
    }

    public void toBaseMap(View view){
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void initLocationClient() {
        mLocationClient = new LocationClient(getApplicationContext());
        mMyLocationListener = new MyLocationListener();

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("gcj02");
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(mMyLocationListener);
        mLocationClient.start();
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        }else{
            Toast.makeText(MainActivity.this,mLocationClient.toString()+"---"+mLocationClient.isStarted(),Toast.LENGTH_SHORT).show();
        }

    }

    private class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Constants.ATTRS = bdLocation.getAddrStr();
            Toast.makeText(MainActivity.this,Constants.ATTRS,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (firstTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        }else{
            Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
        }
        firstTime = System.currentTimeMillis();
    }

    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(FragmentFactory.CLOSE, R.drawable.icn_close);
        list.add(menuItem0);
        SlideMenuItem menuItem = new SlideMenuItem(FragmentFactory.BUILDING, R.drawable.icn_1);
        list.add(menuItem);
        SlideMenuItem menuItem2 = new SlideMenuItem(FragmentFactory.BOOK, R.drawable.icn_2);
        list.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(FragmentFactory.PAINT, R.drawable.icn_3);
        list.add(menuItem3);
        SlideMenuItem menuItem4 = new SlideMenuItem(FragmentFactory.CASE, R.drawable.icn_4);
        list.add(menuItem4);
        SlideMenuItem menuItem5 = new SlideMenuItem(FragmentFactory.SHOP, R.drawable.icn_5);
        list.add(menuItem5);
        SlideMenuItem menuItem6 = new SlideMenuItem(FragmentFactory.PARTY, R.drawable.icn_6);
        list.add(menuItem6);
        SlideMenuItem menuItem7 = new SlideMenuItem(FragmentFactory.MOVIE, R.drawable.icn_7);
        list.add(menuItem7);
    }


    private void setActionBar() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                mToolBar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                linearLayout.removeAllViews();
                linearLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        findViews();

        mRlHeadLayout.setTranslationY(headLayoutTranslationY);
        mIvBackground.setTranslationY(headLayoutTranslationY);
        mRlEditInfo.setTranslationY(Tools.dpToPx(80));

        Log.e(TAG, "headLayoutTranslationY" + headLayoutTranslationY);
        Log.e(TAG, "FABButtonTranslationY" + FABButtonTranslationY);

        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {

        mToolBar.setTranslationY(-Tools.dpToPx(ACTION_BAR_TRANSLATIONY));
        mToolBar.animate()
                .translationY(0)
                .setStartDelay(300)
                .setDuration(ANIM_DURING_ACTION_BAR)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

    }

    private void startContentAnimation() {
        mRlHeadLayout.animate()
                .translationY(0)
                .setDuration(ANIM_DURING_CONTENT)
                .start();
        mIvBackground.animate()
                .translationY(0)
                .setDuration(ANIM_DURING_CONTENT)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        pendingLoading = true;
                        Log.e(TAG, "pendingLoading set true");
                        startFABAnimation();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    private void startFABAnimation() {
        mRlEditInfo.animate()
                .translationY(0)
                .setDuration(ANIM_DURING_FAB_BUTTON)
                .setInterpolator(new OvershootInterpolator(1.f))
                .start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.mainActivity_action_logout:
                if(user.isLogin()) {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("确定要退出登录吗？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    user.setLogin(false);
                                    Fragment contentFragment = FragmentFactory.getInstance(FragmentFactory.FragmentType.MAIN);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, contentFragment).commit();
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).show();
                }else{
                    Toast.makeText(MainActivity.this,"您还没有登录",Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition) {
Log.i(TAG, topPosition+"");
//        this.res = this.res == R.drawable.content_music ? R.drawable.content_films : R.drawable.content_music;
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();
//        ContentFragment contentFragment = ContentFragment.newInstance(this.res);
        Fragment contentFragment = FragmentFactory.getInstance(mType);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, contentFragment).commit();
        return (ScreenShotable) contentFragment;
//        return fragments.get(topPosition);
    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        switch (slideMenuItem.getName()) {
            case FragmentFactory.CLOSE:
                return screenShotable;
            case FragmentFactory.BOOK:
                if (user.isLogin()) {
                    mType = FragmentFactory.FragmentType.MAP;
                    break;
                }else{
                    Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                    return screenShotable;
                }
            case FragmentFactory.BUILDING:
                mType = FragmentFactory.FragmentType.MAIN;
                break;
            case FragmentFactory.PAINT:
                if (user.isLogin()) {
                    mType = FragmentFactory.FragmentType.FRIENDS;
                    break;
                }else{
                    Toast.makeText(MainActivity.this,"请先登录",Toast.LENGTH_SHORT).show();
                    return screenShotable;
                }
            case FragmentFactory.CASE:
                mType = FragmentFactory.FragmentType.RECORD;
                break;
            case FragmentFactory.SHOP:

            default:
                return replaceFragment(screenShotable, position);
        }
        return replaceFragment(screenShotable,position);
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();

    }

    @Override
    public void addViewToContainer(View view) {
        linearLayout.addView(view);
    }
}
