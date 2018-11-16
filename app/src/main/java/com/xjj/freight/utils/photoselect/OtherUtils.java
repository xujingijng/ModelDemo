package com.xjj.freight.utils.photoselect;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Environment;
import android.text.TextUtils;
import android.view.TouchDelegate;
import android.view.View;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description: 工具类
 * @author xujingjing
 * @date 2018/6/16 0016
 */
public class OtherUtils {

    /**
     * 判断外部存储卡是否可用
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public static final int getHeightInPx(Context context) {
        final int height = context.getResources().getDisplayMetrics().heightPixels;
        return height;
    }

    public static final int getWidthInPx(Context context) {
        final int width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }

    public static final int getHeightInDp(Context context) {
        final float height = context.getResources().getDisplayMetrics().heightPixels;
        int heightInDp = px2dip(context, height);
        return heightInDp;
    }

    public static final int getWidthInDp(Context context) {
        final float width = context.getResources().getDisplayMetrics().widthPixels;
        int widthInDp = px2dip(context, width);
        return widthInDp;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取拍照相片存储文件
     * @param context
     * @return
     */
    public static File createFile(Context context){
        File file;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String timeStamp = String.valueOf(System.currentTimeMillis());
            file = new File(Environment.getExternalStorageDirectory() +
                    File.separator + timeStamp+".jpg");
        }else{
            File cacheDir = context.getCacheDir();
            String timeStamp = String.valueOf(System.currentTimeMillis());
            file = new File(cacheDir, timeStamp+".jpg");
        }
        return file;
    }


    /**
     * 扩大View点击区域
     * @param view
     * @param expandTouchWidth
     */
    public static void setTouchDelegate(final View view, final int expandTouchWidth) {
        final View parentView = (View) view.getParent();
        parentView.post(new Runnable() {
            @Override
            public void run() {
                // view构建完成后才能获取，所以放在post中执行
                final Rect rect = new Rect();
                view.getHitRect(rect);
                // 4个方向增加矩形区域
                rect.top -= expandTouchWidth;
                rect.bottom += expandTouchWidth;
                rect.left -= expandTouchWidth;
                rect.right += expandTouchWidth;

                parentView.setTouchDelegate(new TouchDelegate(rect, view));
            }
        });
    }
}
