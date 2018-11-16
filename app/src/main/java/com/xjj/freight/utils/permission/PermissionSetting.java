package com.xjj.freight.utils.permission;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import com.xjj.freight.R;
import com.xjj.freight.view.MyAlertDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.SettingService;

import java.util.List;

/**
 * 默认权限拒绝 且选中以后不提示后的对话框
 */
public final class PermissionSetting {

    private final Context mContext;

    public PermissionSetting(Context context) {
        this.mContext = context;
    }

    public void showSetting(final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(mContext, permissions);
        String message = mContext.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));

        final SettingService settingService = AndPermission.permissionSetting(mContext);
        new MyAlertDialog.Builder(mContext)
                .setCancelable(true)
                .setTitle(R.string.title_dialog)
                .setMessage(message)
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingService.execute();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        settingService.cancel();
                        dialog.dismiss();
                    }
                })
                .show();
    }
}