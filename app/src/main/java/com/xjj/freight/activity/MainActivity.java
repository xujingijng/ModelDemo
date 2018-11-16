package com.xjj.freight.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.xjj.freight.BaseActivity;
import com.xjj.freight.R;
import com.xjj.freight.fragment.MainFirstFragment;
import com.xjj.freight.fragment.MainLastFragment;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {
    private MainFirstFragment firstFragment;
    private MainLastFragment lastFragment;
    private Fragment[] fragments;
    private int currentTabIndex = 0;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFragment();
    }

    private void showFragment() {
        firstFragment = new MainFirstFragment();
        lastFragment = new MainLastFragment();
        fragments=new Fragment[]{firstFragment,lastFragment};
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.content, firstFragment).add(R.id.content, lastFragment)
                .hide(fragments[currentTabIndex]).hide(lastFragment)
                .show(firstFragment).commitAllowingStateLoss();
        currentTabIndex = index;
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.radio1:
                index = 0;
                break;
            case R.id.radio2:
                 index = 1;
                break;
            default:
                 index = 0;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.content, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        currentTabIndex = index;
    }

}
