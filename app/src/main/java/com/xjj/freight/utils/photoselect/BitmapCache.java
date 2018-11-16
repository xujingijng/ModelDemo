package com.xjj.freight.utils.photoselect;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.LruCache;

import com.bumptech.glide.Glide;
import com.xjj.freight.utils.NameThreadFactory;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Describe: 图片缓存
 *
 * @author xujingjing
 * @date 2018/6/17 0017
 */
public class BitmapCache {

    private LruCache<String, Bitmap> mMemoryCache;
    private static BitmapCache mInstance;
    private ScheduledThreadPoolExecutor executor;
    public static final int MAX_GRID_SIZE = 270;

    public static synchronized BitmapCache getInstance() {
        if (mInstance == null) {
            mInstance = new BitmapCache();
        }
        return mInstance;
    }

    private BitmapCache(){
        executor = new ScheduledThreadPoolExecutor(4, new NameThreadFactory(getClass().getSimpleName()));
        initMemoryCache();
    }

    /**
     * 初始化内存缓存
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void initMemoryCache() {
        if (mMemoryCache != null) {
            try {
                clearMemoryCache();
            } catch (Throwable e) {
            }
        }
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                if (bitmap == null){
                    return 0;
                }
                return bitmap.getByteCount();
            }
        };
    }


    /**
     * 加载图片
     */
    public Bitmap loadImage(final Context mContext, final String path, final int position, final ImageCallBack callBack){
         Bitmap bm = getBitmapFromMemoryCache(path);
        if(bm != null){
            return bm;
        }

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bitmap bmp = (Bitmap) msg.obj;
                callBack.callBack(path,position, bmp);
                return false;
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = Glide.with(mContext).load(path).asBitmap().into(MAX_GRID_SIZE, MAX_GRID_SIZE).get();
                    if (bitmap != null){
                        addBitmapToMemoryCache(path,bitmap);
                        handler.sendMessage(handler.obtainMessage(0x123, bitmap));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

      return null;
    }

    /**
     * 从内存缓存中获取图片
     *
     * @param key
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (!TextUtils.isEmpty(key) && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 清空内存缓存
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void clearMemoryCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
        }
    }

    public interface ImageCallBack{
        void callBack(String path,int postition, Bitmap bm);
    }
}
