package com.xjj.freight;

import android.app.Activity;
import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.xjj.freight.utils.ProgressDialog;
import com.xjj.freight.utils.SystemBarTintManager;
import com.xjj.freight.utils.TouchUtil;
import com.xjj.freight.utils.NameThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Describe: Activity的基类
 *
 * @author xujingjing
 * @date 2018/6/6 0006
 */
public  class BaseActivity extends FragmentActivity{
    /**
     * 返回键
     */
    private View ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(null);
        super.onCreate(savedInstanceState);
        setStatusColor();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initBackButton();
    }

    /**
     * 设置状态栏颜色
     */
    private void setStatusColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            //通知栏所需颜色
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.c_ffaa33));
            getWindow().getDecorView().setFitsSystemWindows(true);
        }
    }

    /**
     * 初始化左上角返回按钮
     */
    public final void initBackButton() {
        if (ivBack == null) {
            // 自动匹配返回按钮
              ivBack = findViewById(R.id.iv_back);
            if (ivBack != null) {
                TouchUtil.setDelegate(this, ivBack);
                setBackButton(ivBack);
            }
        }
    }

    /**
     * 将该view的点击按照物理返回键响应
     *
     * @param view the view
     */
    protected void setBackButton(View view) {

        view.setOnClickListener(mBackKeyListener);
    }

    /**
     * 模拟按物理返回键
     */
    private final View.OnClickListener mBackKeyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 直接关闭输入法，退出
            hideSoftInput(BaseActivity.this, v);
            ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new NameThreadFactory(getClass().getSimpleName()));
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (BaseActivity.this.isFinishing()) {
                            // 本界面退出后，下面的命令要求注入其他进程执行物理 back 键
                            return;
                        }
                        new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    /**
     * 强制隐藏键盘
     *
     * @param activity the activity
     * @param view     the view
     */
    public static void hideSoftInput(final Activity activity, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
