package com.xjj.freight;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Describe: Fragment的基类
 *
 * @author xujingjing
 * @date 2018/6/6 0006
 */
public  abstract class BaseFragment extends Fragment{

    private View parentView;
    protected abstract int getLayoutId();
    protected abstract void createView(LayoutInflater inflater, ViewGroup container, View parentView, Bundle savedInstanceState);
    protected abstract void init(Bundle savedInstanceState);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        parentView = inflater.inflate(getLayoutId(), container, false);
        createView(inflater, container, parentView, savedInstanceState);
        return parentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(savedInstanceState);
    }
}
