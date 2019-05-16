package net.fengyun.italker.italker.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.fengyun.italker.common.app.Activity;
import net.fengyun.italker.common.app.Fragment;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.frags.user.UpdateInfoFragment;
import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;

/**
 * @author fengyun
 * 用户信息界面 可以提供用户信息修改
 */
public class UserActivity extends Activity {

    private Fragment mCurFragment;

    @BindView(R.id.im_bg)
    ImageView mBg;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
    }
    /**
     * 用户activity的显示入口
     * @param context 上下文
     */
    public static void show(Context context){
        context.startActivity(new Intent(context, UserActivity.class));
    }
    @Override
    protected void initWeight() {
        super.initWeight();
        mCurFragment =  new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mCurFragment)
                .commit();

        //初始化背景
        Glide.with(this)
                .load(R.drawable.bg_src_tianjin)
                .centerCrop()//居中剪切
                .into(new ViewTarget<ImageView, GlideDrawable>(mBg) {

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        Drawable drawable = resource.getCurrent();
                        //调用适配类进行包装
                        drawable = DrawableCompat.wrap(drawable);
                        drawable.setColorFilter(UiCompat.getColor(getResources(),R.color.colorAccent)
                                , PorterDuff.Mode.SCREEN);//设置着色的效果和颜色，蒙版模式
                        //设置给Imageview
                        this.view.setImageDrawable(drawable);
                    }
                });
    }

    // Activity中收到剪切图片成功的回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurFragment.onActivityResult(requestCode, resultCode, data);
    }

}
