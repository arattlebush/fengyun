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
import net.fengyun.italker.italker.frags.account.AccountTrigger;
import net.fengyun.italker.italker.frags.account.LoginFragment;
import net.fengyun.italker.italker.frags.account.RegisterFragment;
import net.qiujuer.genius.ui.compat.UiCompat;

import butterknife.BindView;

/**
 * @author fengyun
 *         用户的activity
 */
public class AccountActivity extends Activity implements AccountTrigger {

    private Fragment mCurFragment;
    private Fragment mLoginFragment;
    private Fragment mRegisterFragment;

    @BindView(R.id.im_bg)
    ImageView mBg;

    /**
     * 账户activity的显示入口
     *
     * @param context 上下文
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWeight() {
        super.initWeight();


        //初始化fragment
        mCurFragment = mLoginFragment = new LoginFragment();
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

    @Override
    public void triggerView() {
        Fragment fragment;
        if (mCurFragment == mLoginFragment) {
            if (mRegisterFragment == null) {
                //默认情况下为null
                //第一次之后就不为null了
                mRegisterFragment = new RegisterFragment();
            }
            fragment = mRegisterFragment;
        } else {
            //因为默认情况下mLoginFragment已经赋值，无需判断为null
            fragment = mLoginFragment;
        }
        mCurFragment = fragment;
        //切换显示
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lay_container, mCurFragment)
                .commit();
    }
}
