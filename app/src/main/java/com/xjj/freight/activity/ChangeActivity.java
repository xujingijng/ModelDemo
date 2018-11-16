package com.xjj.freight.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xjj.freight.BaseActivity;
import com.xjj.freight.R;

public class ChangeActivity extends BaseActivity implements View.OnClickListener {

    private FrameLayout mLayoutBoard;
    private FrameLayout mLayoutVideo;
    private FrameLayout father;
    /**
     * 切换
     */
    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        initView();
    }

    private void initView() {
        father = findViewById(R.id.father);
        mLayoutBoard = findViewById(R.id.layout_board);
        mLayoutVideo = findViewById(R.id.layout_video);
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn:
                setTopPosition(mLayoutVideo);
                father.bringChildToFront(mLayoutVideo);
                father.updateViewLayout(mLayoutVideo,mLayoutVideo.getLayoutParams());
                break;
        }
    }

    private void setTopPosition(View view) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT;
        layoutParams.height = 200;
        layoutParams.setMargins(0, 0, 0, 0);
        view.setLayoutParams(layoutParams);
    }
}
