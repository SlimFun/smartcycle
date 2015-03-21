package com.example.yh.context;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.example.yh.netword.RequestManager;
import com.example.yh.network.image.ImageCacheManager;

/**
 * Created by yh on 2015/2/13.
 */
public class MainApplication extends Application {

    private static int DISK_IMAGECACHE_SIZE = 1024*1024*30;
    private static Bitmap.CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static int DISK_IMAGECACHE_QUALITY = 100;

    public static int ScreenHeight;
    public static int ScreenWidth;
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void createImageCache() {
        ImageCacheManager.getInstance().init(this,
                this.getPackageCodePath()
                ,DISK_IMAGECACHE_SIZE
                ,DISK_IMAGECACHE_COMPRESS_FORMAT
                ,DISK_IMAGECACHE_QUALITY
                , ImageCacheManager.CacheType.DISK);
    }

    private void init(){
        RequestManager.init(this);
        createImageCache();
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(localDisplayMetrics);
        ScreenHeight = localDisplayMetrics.heightPixels;
        ScreenWidth = localDisplayMetrics.widthPixels;

    }
}
