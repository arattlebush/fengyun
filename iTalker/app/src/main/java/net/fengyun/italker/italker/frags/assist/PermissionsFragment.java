package net.fengyun.italker.italker.frags.assist;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.fengyun.italker.common.app.Application;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.frags.media.GalleryFragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author fengyun
 *         权限弹出框
 */
public class PermissionsFragment extends BottomSheetDialogFragment
    implements EasyPermissions.PermissionCallbacks{

    //权限回调的标示
    private static final int RC = 0x100;

    public PermissionsFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //复用即可
        return new GalleryFragment.TransStatusBottomSheetDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取布局中的控件
        View root = inflater.inflate(R.layout.fragment_permissions, container, false);
        root.findViewById(R.id.btn_submit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击时进行申请权限
                        requestPermission();
                    }
                });
        return root;
    }

    /**
     * 刷新我们的布局中的图片状态
     *
     * @param root
     */
    private void refreshState(View root) {
        if (root == null) {
            return;
        }
        Context context = getContext();
        //网络权限
        root.findViewById(R.id.im_state_permission_network)
                .setVisibility(haveNetwork(context) ? View.VISIBLE : View.GONE);
        //读取权限
        root.findViewById(R.id.im_state_permission_read)
                .setVisibility(haveReadPerm(context) ? View.VISIBLE : View.GONE);
        //写入权限
        root.findViewById(R.id.im_state_permission_write)
                .setVisibility(haveWhitePerm(context) ? View.VISIBLE : View.GONE);
        //录音权限
        root.findViewById(R.id.im_state_permission_record_audio)
                .setVisibility(haveRecordAduioPerm(context) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        //界面显示的时候刷新一下
        refreshState(getView());
    }

    /**
     * 获取是否有网络权限
     *
     * @param context 上下文
     * @return true代表有这个权限
     */
    private static boolean haveNetwork(Context context) {
        //准备检查我们的网络权限
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 获取是否有外部存储权限
     *
     * @param context 上下文
     * @return true代表有这个权限
     */
    private static boolean haveReadPerm(Context context) {
        //准备检查我们的读取权限
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 获取是否有写入权限
     *
     * @param context 上下文
     * @return true代表有这个权限
     */
    private static boolean haveWhitePerm(Context context) {
        //准备检查我们的写入权限
        String[] perms = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 获取是否有录音权限
     *
     * @param context 上下文
     * @return true代表有这个权限
     */
    private static boolean haveRecordAduioPerm(Context context) {
        //准备检查我们的录音权限
        String[] perms = new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 私有的show方法
     * @param manager fragment管理器
     */
    private static void show(FragmentManager manager) {
        //调用BottomSheetDialogFragment以及准备好的显示方法
        new PermissionsFragment()
                .show(manager, PermissionsFragment.class.getName());
    }

    /**
     * 检查是否有所有权限
     * @param context 上下文
     * @param manager fragment管理器
     * @return True是有所有权限，false代表没有所有权限
     */
    public static boolean haveAll(Context context, FragmentManager manager) {
        boolean haveAll = haveNetwork(context) && haveReadPerm(context) && haveWhitePerm(context) && haveRecordAduioPerm(context);
        if (!haveAll) {
            show(manager);
        }
        return haveAll;
    }
    /**
     * 申请权限的方法
     */
    @AfterPermissionGranted(RC)
    private void requestPermission(){
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.RECORD_AUDIO
        };
        if(EasyPermissions.hasPermissions(getContext(), perms)){
            Application.showToast(R.string.label_permission_ok);
            //Fragment中调用getView得到跟布局 前提是在onCreate之后
            refreshState(getView());
        }else{
            //请求数据
            EasyPermissions.requestPermissions(this,getString(
                    R.string.title_assist_permissions),RC,perms);
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //如果权限有没有申请成功的权限存在，则弹出弹出框，用户点击后去到设置界面打开权限
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog
                    .Builder(this)
                    .build()
                    .show();
        }
    }

    /**
     * 权限申请格式后回调的方法，把这个方法吧对应的权限申请状态交给EasyPermissons框架
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //传递对应的参数，并且告知接受权限的处理者是我自己
        EasyPermissions.onRequestPermissionsResult
                (requestCode,permissions,grantResults,this);
    }
}
