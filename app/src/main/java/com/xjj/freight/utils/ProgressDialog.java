package com.xjj.freight.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.xjj.freight.R;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;

/**
 * 公用加载样式loading
 * 支持用户back取消动画时，cancel mRefTask
 */
public class ProgressDialog extends Dialog {
    private final Context context;
    private TextView textView1;
    private final boolean isModel;
    private ImageView imageView;
    private WeakReference<Call> mApiCall;
    /**
     * dismiss 后 变成 -1
     */
    private final AtomicInteger mShowCount = new AtomicInteger(0);

    /**
     * Instantiates a new Xnw progress dialog.
     *
     * @param context the context
     */
    public ProgressDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        isModel = false;
        this.mApiCall = null;
        initUi("");
    }

    /**
     * Instantiates a new Xnw progress dialog.
     *
     * @param context the context
     * @param resId   the res id
     */
    public ProgressDialog(Context context, int resId) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        isModel = false;
        this.mApiCall = null;
        initUi(context.getString(resId));
    }

    /**
     * Instantiates a new Xnw progress dialog.
     *
     * @param context the context
     * @param strTip  the str tip
     */
    public ProgressDialog(Context context, String strTip) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        isModel = false;
        this.mApiCall = null;
        initUi(strTip);
    }

    /**
     * Sets dialog text.
     *
     * @param strTip the str tip
     */
    public void setDialogText(final String strTip) {
        try {
            //如果当前线程是主线程
            if (Looper.myLooper() == Looper.getMainLooper()) {
                textView1.setText(strTip);
                if (!textView1.isShown()) {
                    textView1.setVisibility(View.VISIBLE);
                }
            } else {
                textView1.post(new Runnable() {
                    @Override
                    public void run() {
                        setDialogText(strTip);
                    }
                });
            }
        } catch (NullPointerException ignored) {
        }
    }

    private void initUi(String tip) {
        super.setContentView(R.layout.progress_dialog_layout);
        Window win = getWindow();
        if (!isModel && win != null) {
            win.setGravity(Gravity.CENTER | Gravity.TOP);
            // 设置点击Dialog外部任意区域关闭Dialog
            setCanceledOnTouchOutside(true);
        }
        textView1 = (TextView) this.findViewById(R.id.textView1);
        if (tip != null && !"".equals(tip)) {
            setDialogText(tip);
        } else {
            textView1.setVisibility(View.GONE);
        }
        imageView = (ImageView) this.findViewById(R.id.imageView1);
        try {
            ProgressDialog.this.setCancelable(true);
        } catch (Exception ignored) {

        }
    }

    @Override
    public boolean isShowing() {
        try {
            return super.isShowing();
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public void show() {
        try {
            if (mShowCount.getAndIncrement() > 0) {
                return;
            }
            imageView.setImageResource(R.mipmap.loading_1);
            Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_common);
            imageView.startAnimation(rotateAnimation);
            //当前线程是否是主线程
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            dismiss(); // free resource
        }
    }

    /**
     * 避免出现多个加载动画
     */
    @Override
    public void dismiss() {
        try {
            if (mShowCount.decrementAndGet() > 0) {
                return;
            }
            imageView.clearAnimation();
            imageView.setImageDrawable(null);
            if(isShowing()) {
                super.dismiss();
            }
            Call call = mApiCall == null ? null : mApiCall.get();
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
            mShowCount.set(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
    }

    /**
     * ApiWorkflow 设置 call,供dismiss()时cancel
     *
     * @param call the call
     */
    public void setCall(Call call) {
        this.mApiCall = new WeakReference<>(call);
    }

    /**
     * Is dismissed boolean.
     *
     * @return the boolean
     */
    public boolean isDismissed() {
        return mShowCount.get() < 0;
    }
}
