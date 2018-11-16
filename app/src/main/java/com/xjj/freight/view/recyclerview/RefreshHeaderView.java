package com.xjj.freight.view.recyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xjj.freight.R;

/**
 * The type Refresh header view.
 */
public class RefreshHeaderView extends LinearLayout {
    /**
     * The constant STATE_NORMAL.
     */
    public static final int STATE_NORMAL = 0;
    /**
     * The constant STATE_RELEASE_TO_REFRESH.
     */
    public static final int STATE_RELEASE_TO_REFRESH = 1;
    /**
     * The constant STATE_REFRESHING.
     */
    public static final int STATE_REFRESHING = 2;
    /**
     * The constant STATE_DONE.
     */
    public static final int STATE_DONE = 3;

    private LinearLayout mContainer;
    private ImageView mArrowImageView;
    private ImageView mProgressBar;

    private int mState = STATE_NORMAL;

    /**
     * The M measured height.
     */
    public int mMeasuredHeight;

    /**
     * Instantiates a new Refresh header view.
     *
     * @param context the context
     */
    public RefreshHeaderView(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Refresh header view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public RefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        // 初始情况，设置下拉刷新view高度为0
        mContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.pull_recyclerview_header_view, null);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        setLayoutParams(layoutParams);

        setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.header_arrow);
        mProgressBar = (ImageView) findViewById(R.id.header_progressbar);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
        System.out.println("mMeasuredHeight:" + mMeasuredHeight);
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(int state) {
        if (state == mState) {
            return;
        }
        switch (state) {
            case STATE_NORMAL:
                mArrowImageView.setVisibility(View.VISIBLE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setImageResource(R.mipmap.top_loading1);

                hideLoading();
                break;
            case STATE_RELEASE_TO_REFRESH:
                mArrowImageView.setVisibility(View.VISIBLE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setImageResource(R.mipmap.top_loading2);

                hideLoading();
                break;
            case STATE_REFRESHING:
                // 显示进度
                showRefreshAnimation();

                mProgressBar.setVisibility(View.VISIBLE);
                smoothScrollTo(mMeasuredHeight);
                break;
            case STATE_DONE:
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(View.INVISIBLE);

                hideLoading();
                break;
            default:
                break;
        }
        mState = state;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public int getState() {
        return mState;
    }

    /**
     * Refresh complete.
     */
    public void refreshComplete() {
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reset();
            }
        }, 200);
    }

    /**
     * Sets visible height.
     *
     * @param height the height
     */
    public void setVisibleHeight(int height) {
        if (height < 0) {
            height = 0;
        }
        LayoutParams layoutParams = (LayoutParams) mContainer.getLayoutParams();
        layoutParams.height = height;
        mContainer.setLayoutParams(layoutParams);
    }

    /**
     * Gets visible height.
     *
     * @return the visible height
     */
    public int getVisibleHeight() {
        LayoutParams layoutParams = (LayoutParams) mContainer.getLayoutParams();
        return layoutParams.height;
    }

    /**
     * On move.
     *
     * @param delta the delta
     */
    public void onMove(float delta) {
        if (getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) {
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    /**
     * Release action boolean.
     *
     * @return the boolean
     */
    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        // not visible.
        if (height == 0) {
            isOnRefresh = false;
        }
        if (getVisibleHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMeasuredHeight) {
            //return;
        }
        if (mState != STATE_REFRESHING) {
            smoothScrollTo(0);
        }

        if (mState == STATE_REFRESHING) {
            int destHeight = mMeasuredHeight;
            smoothScrollTo(destHeight);
        }

        return isOnRefresh;
    }

    /**
     * Reset.
     */
    public void reset() {
        smoothScrollTo(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setState(STATE_NORMAL);
            }
        }, 500);
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    private void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setImageResource(R.mipmap.pull_recyclerview_header_loading_icon);
        Animation second = AnimationUtils.loadAnimation(getContext(), R.anim.pull_recyclerview_anim_rotate);
        mProgressBar.startAnimation(second);
    }

    private void hideLoading() {
        mProgressBar.clearAnimation();
        mProgressBar.setImageDrawable(null);
        mProgressBar.setVisibility(View.GONE);
    }

    private void showRefreshAnimation() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mArrowImageView.clearAnimation();
                // for view height
                mArrowImageView.setVisibility(View.INVISIBLE);
                // 此处调用第二个动画播放方法
                showLoading();
            }
        };
        AnimationDrawable animFirst = (AnimationDrawable) getResources().getDrawable(R.drawable.pull_recyclerview_header_loading_anim);
        mArrowImageView.setImageDrawable(animFirst);
        if (animFirst != null) {
            // 已知帧间隔时间相同
            int duration = animFirst.getDuration(0) * animFirst.getNumberOfFrames();
            Handler handler = new Handler();
            handler.postDelayed(runnable, duration);

            animFirst.start();
        }
        mArrowImageView.setVisibility(View.VISIBLE);
    }

}