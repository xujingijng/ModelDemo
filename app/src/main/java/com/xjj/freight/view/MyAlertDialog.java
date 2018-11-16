package com.xjj.freight.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xjj.freight.R;

/**
 * 通用信息对话框
 */
public class MyAlertDialog {
    private MyAlertDialog.Builder builder;
    private Dialog mDialog;

    /**
     * Instantiates a new My alert dialog.
     *
     * @param context the context
     */
    public MyAlertDialog(Context context) {
        this.builder = new MyAlertDialog.Builder(context);
    }

    /**
     * Instantiates a new My alert dialog.
     *
     * @param dialog the dialog
     */
    public MyAlertDialog(Dialog dialog) {
        this.mDialog = dialog;
    }

    /**
     * Show.
     */
    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    /**
     * Sets cancelable.
     *
     * @param cancelable the cancelable
     * @return the cancelable
     */
    public MyAlertDialog setCancelable(boolean cancelable) {
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        } else if (builder != null) {
            builder.setCancelable(cancelable);
        }
        return this;
    }

    /**
     * Sets canceled on touch outside.
     *
     * @param cancelable the cancelable
     * @return the canceled on touch outside
     */
    public MyAlertDialog setCanceledOnTouchOutside(boolean cancelable) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(cancelable);
        } else if (builder != null) {
            builder.setCanceledOnTouchOutside(cancelable);
        }
        return this;
    }

    /**
     * Dismiss.
     */
    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    /**
     * Is showing boolean.
     *
     * @return the boolean
     */
    public boolean isShowing() {
        return mDialog != null && mDialog.isShowing();
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private Context mContext;
        private Dialog mDialog;

        private CharSequence titleStr;
        private CharSequence messageStr;

        private CharSequence positiveStr;
        private DialogInterface.OnClickListener positiveListener;
        private CharSequence negativeStr;
        private DialogInterface.OnClickListener negativeListener;
        private CharSequence neutralStr;
        private DialogInterface.OnClickListener neutralListener;

        private CharSequence[] arrayStr;
        private DialogInterface.OnClickListener onItemClickListener;

        private DialogInterface.OnCancelListener cancelListener;
        private DialogInterface.OnDismissListener dismissListener;
        private DialogInterface.OnKeyListener onKeyListener;

        private boolean cancelable = true;
        private boolean canceledOnTouchOutside = true;

        /**
         * 是否强制显示标题，true:显示；false:不显示
         */
        private boolean forceShowTitle = false;

        /**
         * Instantiates a new Builder.
         *
         * @param context the context
         */
        public Builder(Context context) {
            this.mContext = context;
        }

        /**
         * Gets context.
         *
         * @return the context
         */
        public Context getContext() {
            return mContext;
        }

        /**
         * Sets title.
         *
         * @param titleId the title id
         * @return the title
         */
        public MyAlertDialog.Builder setTitle(int titleId) {
            this.titleStr = mContext.getResources().getString(titleId);
            return this;
        }

        /**
         * Sets title.
         *
         * @param title the title
         * @return the title
         */
        public MyAlertDialog.Builder setTitle(CharSequence title) {
            this.titleStr = title;
            return this;
        }

        /**
         * 强制显示title
         *
         * @param show 标题
         * @return the my alert dialog . builder
         */
        public MyAlertDialog.Builder forceShowTitle(boolean show) {
            forceShowTitle = show;
            return this;
        }

        /**
         * Sets message.
         *
         * @param messageId the message id
         * @return the message
         */
        public MyAlertDialog.Builder setMessage(int messageId) {
            this.messageStr = mContext.getResources().getString(messageId);
            return this;
        }

        /**
         * Sets message.
         *
         * @param message the message
         * @return the message
         */
        public MyAlertDialog.Builder setMessage(CharSequence message) {
            this.messageStr = message;
            return this;
        }

        /**
         * Sets positive button.
         *
         * @param textId   the text id
         * @param listener the listener
         * @return the positive button
         */
        public MyAlertDialog.Builder setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
            this.positiveStr = mContext.getResources().getString(textId);
            this.positiveListener = listener;
            return this;
        }

        /**
         * Sets positive button.
         *
         * @param text     the text
         * @param listener the listener
         * @return the positive button
         */
        public MyAlertDialog.Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            this.positiveStr = text;
            this.positiveListener = listener;
            return this;
        }

        /**
         * Sets negative button.
         *
         * @param textId   the text id
         * @param listener the listener
         * @return the negative button
         */
        public MyAlertDialog.Builder setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
            this.negativeStr = mContext.getResources().getString(textId);
            this.negativeListener = listener;
            return this;
        }

        /**
         * Sets negative button.
         *
         * @param text     the text
         * @param listener the listener
         * @return the negative button
         */
        public MyAlertDialog.Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
            this.negativeStr = text;
            this.negativeListener = listener;
            return this;
        }

        /**
         * 支持不执行dismiss
         *
         * @param textId   the text id
         * @param listener null 表示直接执行dismiss ; 否则需要listener内部实现dismiss。
         * @return the neutral button
         */
        public MyAlertDialog.Builder setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
            this.neutralStr = mContext.getResources().getString(textId);
            this.neutralListener = listener;
            return this;
        }

        /**
         * Sets neutral button.
         *
         * @param text     the text
         * @param listener the listener
         * @return the neutral button
         */
        public MyAlertDialog.Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
            this.neutralStr = text;
            this.neutralListener = listener;
            return this;
        }

        /**
         * Sets cancelable.
         *
         * @param cancelable the cancelable
         * @return the cancelable
         */
        public MyAlertDialog.Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * Sets on cancel listener.
         *
         * @param onCancelListener the on cancel listener
         * @return the on cancel listener
         */
        public MyAlertDialog.Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            this.cancelListener = onCancelListener;
            return this;
        }

        /**
         * Sets on dismiss listener.
         *
         * @param onDismissListener the on dismiss listener
         * @return the on dismiss listener
         */
        public MyAlertDialog.Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.dismissListener = onDismissListener;
            return this;
        }

        /**
         * Sets on key listener.
         *
         * @param onKeyListener the on key listener
         * @return the on key listener
         */
        public MyAlertDialog.Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            this.onKeyListener = onKeyListener;
            return this;
        }

        /**
         * Sets canceled on touch outside.
         *
         * @param canceledOnTouchOutside the canceled on touch outside
         * @return the canceled on touch outside
         */
        public MyAlertDialog.Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            this.canceledOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        /**
         * Sets items.
         *
         * @param itemsId  the items id
         * @param listener the listener
         * @return the items
         */
        public MyAlertDialog.Builder setItems(int itemsId, DialogInterface.OnClickListener listener) {
            this.arrayStr = mContext.getResources().getStringArray(itemsId);
            this.onItemClickListener = listener;
            return this;
        }

        /**
         * Sets items.
         *
         * @param items    the items
         * @param listener the listener
         * @return the items
         */
        public MyAlertDialog.Builder setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
            this.arrayStr = new String[items.length];
            for (int i = 0; i < items.length; i++) {
                arrayStr[i] = items[i].toString();
            }
            this.onItemClickListener = listener;
            return this;
        }

        /**
         * Sets items.
         *
         * @param items    the items
         * @param listener the listener
         * @return the items
         */
        public MyAlertDialog.Builder setItems(String[] items, DialogInterface.OnClickListener listener) {
            this.arrayStr = items;
            this.onItemClickListener = listener;
            return this;
        }

        /**
         * Create my alert dialog.
         *
         * @return the my alert dialog
         */
        public MyAlertDialog create() {
            mDialog = new Dialog(mContext, R.style.MyAlertDialog);
            View layout = LayoutInflater.from(mContext).inflate(R.layout.my_alert_dialog_layout, null);
            TextView titleTxt = layout.findViewById(R.id.title_txt);
            TextView messageTxt = layout.findViewById(R.id.message_txt);

            LinearLayout llListview = layout.findViewById(R.id.ll_list_view);
            TextView titleTxt2 = layout.findViewById(R.id.title_txt2);
            View vLine = layout.findViewById(R.id.v_line);
            ListView listView = layout.findViewById(R.id.list_view);

            LinearLayout buttonLayout = layout.findViewById(R.id.button_layout);
            Button positiveBtn = layout.findViewById(R.id.positive_btn);
            Button negativeBtn = layout.findViewById(R.id.negative_btn);
            mDialog.setContentView(layout);

            mDialog.setCancelable(cancelable);
            mDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);

            if (onKeyListener != null) {
                mDialog.setOnKeyListener(onKeyListener);
            }
            if (cancelListener != null) {
                mDialog.setOnCancelListener(cancelListener);
            }
            if (dismissListener != null) {
                mDialog.setOnDismissListener(dismissListener);
            }

            if (!TextUtils.isEmpty(titleStr)) {
                titleTxt.setVisibility(View.VISIBLE);
                titleTxt.setText(titleStr);
            }

            if (!TextUtils.isEmpty(messageStr)) {
                messageTxt.setVisibility(View.VISIBLE);
                // 支持换行
                String htmlContent = String.valueOf(messageStr).replaceAll("\n", "<br/>");
                // 支持 <font>
                messageTxt.setText(Html.fromHtml(htmlContent));
            }

            if (!TextUtils.isEmpty(neutralStr)) { // 兼容老代码，按照缺少的按钮自动对应
                if (TextUtils.isEmpty(positiveStr)) {
                    positiveStr = neutralStr;
                    positiveListener = neutralListener;
                } else if (TextUtils.isEmpty(negativeStr)) {
                    negativeStr = neutralStr;
                    negativeListener = neutralListener;
                }
            }

            if (!TextUtils.isEmpty(positiveStr)) {
                buttonLayout.setVisibility(View.VISIBLE);
                positiveBtn.setText(positiveStr);
                positiveBtn.setVisibility(View.VISIBLE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                        if (positiveListener != null) {
                            positiveListener.onClick(mDialog, 0);
                        }
                    }
                });
            }

            if (!TextUtils.isEmpty(negativeStr)) {
                buttonLayout.setVisibility(View.VISIBLE);
                negativeBtn.setText(negativeStr);
                negativeBtn.setVisibility(View.VISIBLE);
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (negativeListener != null) {
                            negativeListener.onClick(mDialog, 1);
                        } else // 支持点击后不关闭对话框
                        {
                            mDialog.dismiss();
                        }
                    }
                });
            }

            if (arrayStr != null && arrayStr.length > 0) {
                //如果列表有数据，则隐藏标题,强制显示除外
                titleTxt.setVisibility(View.GONE);

                llListview.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                if (forceShowTitle && !TextUtils.isEmpty(titleStr)) {
                    titleTxt2.setVisibility(View.VISIBLE);
                    vLine.setVisibility(View.VISIBLE);
                    titleTxt2.setText(titleStr);
                }
                BaseAdapter baseAdapter = new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return arrayStr.length;
                    }

                    @Override
                    public Object getItem(int i) {
                        return arrayStr[i];
                    }

                    @Override
                    public long getItemId(int i) {
                        return i;
                    }

                    @Override
                    public View getView(int i, View view, ViewGroup viewGroup) {
                        TextView textView = (TextView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_alert_dialog_text_item_layout, null);
                        if (forceShowTitle){
                            textView.setGravity(Gravity.CENTER);
                        }
                        textView.setText(arrayStr[i]);
                        return textView;
                    }
                };
                listView.setAdapter(baseAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mDialog.dismiss();
                        if (onItemClickListener != null) {
                            onItemClickListener.onClick(mDialog, i);
                        }
                    }
                });
            }

            Window window = mDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            // 支持横竖屏切换，取最小值
            DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
            params.width = Double.valueOf(Math.min(metrics.widthPixels, metrics.heightPixels) * 0.80).intValue();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);

            return new MyAlertDialog(mDialog);
        }

        /**
         * Show.
         */
        public void show() {
            if (this.mDialog == null) {
                create();
            }
            if (this.mDialog != null) {
                try {
                    mDialog.show();
                } catch (WindowManager.BadTokenException e) { // is your activity running?
                    e.printStackTrace();
                }
            }
        }
    }
}
