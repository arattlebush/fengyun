package net.fengyun.italker.italker.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.fengyun.italker.common.app.Application;
import net.fengyun.italker.common.app.ToolbarActivity;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.frags.media.GalleryFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author fengyun
 *         实名认证
 */
public class RealNameActivity extends ToolbarActivity {


    //realName是实名认证前的拍照，到底是拍那张。正、反、手持身份证照片
    private byte realName = 0;

    private String front, opposite, handPhoto;

    @BindView(R.id.im_id_front)
    ImageView mFront;

    @BindView(R.id.im_id_opposite)
    ImageView mOpposite;

    @BindView(R.id.im_id_hand_photo)
    ImageView mHandPhoto;

    public static void show(Context context) {
        context.startActivity(new Intent(context, RealNameActivity.class));
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_real_name;
    }

    @Override
    protected void initData() {
        super.initData();

    }

    // 创建一个以当前时间为名称的文件
    private File tempFile =  new File(Environment.getExternalStorageDirectory(), Application.getPhotoFileName());

    public void realName() {
        final PopupWindow popupWindow = new PopupWindow();
        View view = LayoutInflater.from(RealNameActivity.this).inflate(R.layout.real_name_popup, null);
        view.findViewById(R.id.txt_photograph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null)
                    popupWindow.dismiss();
                tempFile = new File(Environment.getExternalStorageDirectory(),
                        Application.getPhotoFileName());
                // 调用系统的拍照功能
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 指定调用相机拍照后照片的储存路径
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                startActivityForResult(intent, 129);

            }
        });
        view.findViewById(R.id.txt_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null)
                    popupWindow.dismiss();
                selectPhoto();
            }
        });
        view.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null)
                    popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        popupWindow.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        popupWindow.setAnimationStyle(R.style.PopupWindowToTopAnim);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    @OnClick({R.id.im_id_front, R.id.im_id_opposite, R.id.im_id_hand_photo})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_id_front: {
                realName = 0;
                realName();
                break;
            }
            case R.id.im_id_opposite: {
                realName = 1;
                realName();
                break;
            }
            case R.id.im_id_hand_photo: {
                realName = 2;
                realName();
                break;
            }
        }
    }

    /**
     * 选择照片并且截图的逻辑
     */
    private void selectPhoto() {
        new GalleryFragment()
                .setListener(new GalleryFragment.OnSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {
                        UCrop.Options options = new UCrop.Options();
                        //设置图片处理的格式JPEG
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        //设置压缩的精度
                        options.setCompressionQuality(96);
                        //得到图片的缓存地址
                        File dPath = Application.getImageTmpFile();
                        //第一个参数原始路径，第二个接收路径
                        //发起剪切
                        UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                                .withAspectRatio(1, 1)//1比1比例
                                .withMaxResultSize(520, 520)//返回最大的尺寸
                                .withOptions(options)//相关参数
                                .start(RealNameActivity.this);
                    }
                })
                .show(getSupportFragmentManager()
                        , GalleryFragment.class.getName());
    }


    private static final String TAG = "RealNameActivity";
    //照片截图完成的
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //收到从activity传递的信息，然后取出其中的值进行图片加载
        //如果是我能够处理的类型
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                //通过UCORP得到对应的URI
                final Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    //加载头像
                    loadPortrait(resultUri);
                }
            } else if(requestCode== 129){
                UCrop.Options options = new UCrop.Options();
                //设置图片处理的格式JPEG
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                //设置压缩的精度
                options.setCompressionQuality(96);
                //得到头像的缓存地址
                File dPath = Application.getImageTmpFile();
                //第一个参数原始路径，第二个接收路径
                //发起剪切
                Log.e(TAG, "onActivityResult: " +tempFile.getPath() );
                    UCrop.of(Uri.fromFile(new File(tempFile.getPath())), Uri.fromFile(dPath))
                            .withAspectRatio(1, 1)//1比1比例
                            .withMaxResultSize(520, 520)//返回最大的尺寸
                            .withOptions(options)//相关参数
                            .start(RealNameActivity.this);
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Application.showToast(R.string.data_rsp_error_unknown);
        }
    }


    /**
     * 加载uri到当前的头像
     *
     * @param uri 返回过来的图片
     */
    private void loadPortrait(Uri uri) {
        //得到头像地址
        switch (realName) {
            case 0: {
                front = uri.getPath();
                Glide.with(this)
                        .load(uri)
                        .asBitmap()
                        .centerCrop()
                        .into(mFront);
                break;
            }
            case 1: {
                opposite = uri.getPath();
                Glide.with(this)
                        .load(uri)
                        .asBitmap()
                        .centerCrop()
                        .into(mOpposite);
                break;
            }
            case 2: {
                handPhoto = uri.getPath();
                Glide.with(this)
                        .load(uri)
                        .asBitmap()
                        .centerCrop()
                        .into(mHandPhoto);
                break;
            }
        }


    }
}
