package com.netease.permissions;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.netease.permissions.annotations.NeedsPermission;
import com.netease.permissions.annotations.OnNeverAskAgain;
import com.netease.permissions.annotations.OnPermissionDenied;
import com.netease.permissions.library.PermissionManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //触发授权行为
    public void camera(View view) {
        PermissionManager.request(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    //执行正常的业务逻辑：在需要获取权限的地方注释
    @NeedsPermission()
    void showCamera() {
        Log.e("neteast >>> ", "showCamera()");
    }

//    // 这是下次再次请求权限时，提示用户拒绝后为何要开启权限的回调 （这个注解如果存在，就执行这个需要触发request.proceed，否则，不存在此注解时，直接调用系统授权！）
//    @OnShowRationale()
//    void showRationaleForCamera(final PermissionRequest request) {
//        Log.e("neteast >>> ", "showRationaleForCamera()");
//        new AlertDialog.Builder(this)
//                .setMessage("提示用户为何要开启权限")
//                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int which) {
//                        // 再次执行权限请求
//                        request.proceed();
//                    }
//                })
//                .show();
//    }

    // 授权结果返回：用户选择拒绝时的提示
    @OnPermissionDenied()//这个方法的参数应该返回哪些权限没有被授予！
    void showDeniedForCamera(String deniedListTip) {
        Log.e("neteast >>> ", "showDeniedForCamera() -->[" + deniedListTip +"]");
    }

    // 授权结果返回：用户选择不再询问后的提示  优化的地方就是：需要告诉用户需要开启那些对应的权限！
    @OnNeverAskAgain()
    void showNeverAskForCamera(String deniedListTip) {//这个方法的参数应该返回哪些权限没有被授予！
        Log.e("neteast >>> ", "showNeverAskForCamera() -->[" + deniedListTip +"]");
        new AlertDialog.Builder(this).setMessage("请到设置中开启["+ deniedListTip +"]的权限才能继续使用该应用！").setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 去设置中设置权限
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);

                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + info.packageName));
                    startActivity(intent);

                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("neteast >>> ", "onRequestPermissionsResult()");
        PermissionManager.onRequestPermissionsResult(this, requestCode, grantResults);
    }

}
