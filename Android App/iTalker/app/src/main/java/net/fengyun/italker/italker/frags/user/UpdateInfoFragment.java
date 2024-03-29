package net.fengyun.italker.italker.frags.user;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.fengyun.italker.common.app.Application;
import net.fengyun.italker.common.app.PresenterFragment;
import net.fengyun.italker.common.widget.PortraitView;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.activities.MainActivity;
import net.fengyun.italker.italker.frags.media.GalleryFragment;
import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.EditText;
import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import fengyun.android.com.factory.presenter.user.UpdateInfoContract;
import fengyun.android.com.factory.presenter.user.UpdateInfoPresenter;

/**
 * @author fengyun
 *  修改用户信息的fragment
 */
public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter> implements UpdateInfoContract.View{

    private static final String TAG = "UpdateInfoFragment";
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.im_sex)
    ImageView mSex;
    @BindView(R.id.edit_desc)
    EditText mDesc;
    @BindView(R.id.btn_submit)
    Button mSubmit;
    @BindView(R.id.loading)
    Loading mLoading;
    private String mPortraitPath;
    private boolean isMan = true;

    public UpdateInfoFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }


    @OnClick(R.id.im_portrait)
    void onPortraitClick(){
        new GalleryFragment()
                .setListener(new GalleryFragment.OnSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {
                        UCrop.Options options = new UCrop.Options();
                        //设置图片处理的格式JPEG
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        //设置压缩的精度
                        options.setCompressionQuality(96);
                        //得到头像的缓存地址
                        File dPath = Application.getPortraitTmpFile();
                        //第一个参数原始路径，第二个接收路径
                        //发起剪切
                        UCrop.of(Uri.fromFile(new File(path)),Uri.fromFile(dPath))
                        .withAspectRatio(1,1)//1比1比例
                        .withMaxResultSize(520,520)//返回最大的尺寸
                        .withOptions(options)//相关参数
                        .start(getActivity());
                    }
                })//show的时候建议使用getChildFragmentManager
                //tag使我们的class名字
                .show(getChildFragmentManager()
                ,GalleryFragment.class.getName());
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick(){
        String desc = mDesc.getText().toString();
        mPresenter.update(mPortraitPath,desc,isMan);
    }
    @OnClick(R.id.im_sex)
    void onSexClick(){
        //性别图标点击的时候触发
        isMan = !isMan;//反向性别
        Drawable drawable = getResources().getDrawable(isMan?R.drawable.ic_sex_man:R.drawable.ic_sex_woman);
        mSex.setImageDrawable(drawable);
        //设置背景的层级，切换颜色
        mSex.getBackground().setLevel(isMan?0:1);
    }


    @Override
    public void showError(@StringRes int str) {
        super.showError(str);
        mLoading.stop();
        mDesc.setEnabled(true);
        mPortrait.setEnabled(true);
        mSubmit.setEnabled(true);
    }

    @Override
    public void showLoading() {
        super.showLoading();
        mLoading.start();
        mDesc.setEnabled(false);
        mPortrait.setEnabled(false);
        mSubmit.setEnabled(false);
    }


    @Override
    public void updateSucceed() {
        //更新成功跳转到主页
        MainActivity.show(getContext());
        getActivity().finish();
    }


    /**
     * 加载uri到当前的头像
     * @param uri 返回过来的图片
     */
    private void loadPortrait(Uri uri){
        //得到头像地址
        mPortraitPath = uri.getPath();
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //收到从activity传递的信息，然后取出其中的值进行图片加载
        //如果是我能够处理的类型
        if(resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            //通过UCORP得到对应的URI
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                //加载头像
                loadPortrait(resultUri);
            }
        }else if(resultCode == UCrop.RESULT_ERROR){
            Application.showToast(R.string.data_rsp_error_unknown);
        }
    }


}
