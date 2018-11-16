package com.xjj.freight.utils.permission;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;

import java.util.List;

/**
 * Describe: 6.0以上权限处理
 *
 * @author xujingjing
 * @date 2018/6/5 0005
 */
public class Permissions {

    private Rationale mRationale;
    private PermissionSetting mSetting;
    private Context mContext;
    private static Permissions permission;

    public Permissions(Context context) {
        mContext = context;
        mRationale = new DefaultRationale();
        mSetting = new PermissionSetting(context);
    }

    public static Permissions getPermission(Context context) {
        if (permission == null) {
            permission = new Permissions(context);
        }
        return permission;
    }

    /**
     * 权限处理
     */
    public void requestPermission(final PermissionCallBack callBack, String... permissions) {
        AndPermission.with(mContext)
                .permission(permissions)
                .rationale(mRationale)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //获取权限成功
                        callBack.onGranted();
                    }
                }).onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
                            mSetting.showSetting(permissions);
                        }
                    }
                }) .start();
    }
}
