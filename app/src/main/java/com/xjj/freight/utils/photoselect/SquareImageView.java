package com.xjj.freight.utils.photoselect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Describe: 正方形图片(根据宽设置高)
 *
 * @author xujingjing
 * @date 2018/6/16 0016
 */
@SuppressLint("AppCompatCustomView")
public class SquareImageView extends ImageView {

   private int mWidth;

    public SquareImageView(Context context) {
        this(context, null);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int screenWidth = OtherUtils.getWidthInPx(context);
        mWidth = (screenWidth - OtherUtils.dip2px(context, 4))/3;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mWidth);
    }
}
