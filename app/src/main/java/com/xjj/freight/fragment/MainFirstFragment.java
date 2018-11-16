package com.xjj.freight.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.xjj.freight.BaseFragment;
import com.xjj.freight.R;
import com.xjj.freight.activity.ChangeActivity;
import com.xjj.freight.utils.permission.PermissionCallBack;
import com.xjj.freight.utils.permission.Permissions;
import com.xjj.freight.utils.photoselect.OtherUtils;
import com.xjj.freight.utils.photoselect.PhotoPickerActivity;
import com.yanzhenjie.permission.Permission;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Describe: 动态fragment
 *
 * @author xujingjing
 * @date 2018/6/13 0013
 */
public class MainFirstFragment extends BaseFragment implements View.OnClickListener {

    public final static int REQUEST_DATA = 101;

    private Button takePhone, takePhoneMultiple, changeWindow;

    @Override
    protected int getLayoutId() {
        return R.layout.main_first_fragment;
    }

    @Override
    protected void createView(LayoutInflater inflater, ViewGroup container, View parentView, Bundle savedInstanceState) {
        takePhone = parentView.findViewById(R.id.take_photo);
        takePhone.setOnClickListener(this);
        takePhoneMultiple = parentView.findViewById(R.id.take_photo_multiple);
        takePhoneMultiple.setOnClickListener(this);
        changeWindow = parentView.findViewById(R.id.change_window);
        changeWindow.setOnClickListener(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_photo:
                Intent intent1 = new Intent(getActivity(), PhotoPickerActivity.class);
                intent1.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);
                startActivityForResult(intent1, REQUEST_DATA);
                break;
            case R.id.take_photo_multiple:
                Intent intent2 = new Intent(getActivity(), PhotoPickerActivity.class);
                intent2.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, PhotoPickerActivity.MODE_MULTI);
                intent2.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 6);
                startActivityForResult(intent2, REQUEST_DATA);
                break;
            case R.id.change_window:
                Intent intent3 = new Intent(getActivity(), ChangeActivity.class);
                startActivity(intent3);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            //选取的图片数据
            case REQUEST_DATA:
                ArrayList<String> result = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);
                if (result == null || result.size() <= 0) {
                    return;
                }
                Toast.makeText(getActivity(), "选择了图片数量" + result.size(), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
