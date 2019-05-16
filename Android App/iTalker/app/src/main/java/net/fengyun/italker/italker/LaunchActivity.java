package net.fengyun.italker.italker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;

import net.fengyun.italker.common.app.Activity;
import net.fengyun.italker.italker.activities.AccountActivity;
import net.fengyun.italker.italker.activities.MainActivity;
import net.fengyun.italker.italker.frags.assist.PermissionsFragment;
import net.qiujuer.genius.res.Resource;
import net.qiujuer.genius.ui.compat.UiCompat;

import fengyun.android.com.factory.persistence.Account;

/**
 * 第一个界面 如果有所有权限就跳转
 */
public class LaunchActivity extends Activity {

    //Drawable
    private ColorDrawable mBgDrawable;


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWeight() {
        super.initWeight();
        //拿到跟布局
        View root = findViewById(R.id.activity_launch);
        //得到主颜色
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        //创建一个drawable
        ColorDrawable drawable = new ColorDrawable(color);

        root.setBackground(drawable);
        mBgDrawable = drawable;
    }

    @Override
    protected void initData() {
        super.initData();
        //动画进入到50%等待PushID获取到
        startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                waitPushReceiverId();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 等待个推框架对我们的pushId设置好值
     */
    private void waitPushReceiverId(){
        if(Account.isLogin()){
            //已经登录的情况下，判断是否绑定，如果没有绑定则等待广播接收器进行绑定
            if(Account.isBind()){
                skip();
                return;
            }
        }else{
            //没有登录
            //如果拿到了pushId 没有登录的情况下不能绑定pushid
            if(!TextUtils.isEmpty(Account.getPushId())){
                //跳转
                skip();
                return;
            }
        }
        //循环等待
        getWindow().getDecorView()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitPushReceiverId();
                    }
                },500);
    }

    /**
     * 在跳转之前需要把剩下的50%完成
     */
    private void skip(){
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                reallySkip();
            }
        });
    }

    /**
     * 真实跳转
     */
    private void reallySkip(){
        //权限检测 那么进行跳转
        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            //检测跳转到主页还是登录
            if(Account.isLogin()){
                MainActivity.show(this);
            }else{
                AccountActivity.show(this);
            }
            finish();
        }
    }



    /**
     * 给背景设置一个动画
     * @param endProgress 动画的结束进度
     * @param endCallback 动画结束时触发
     */
    private void startAnim(float endProgress,final Runnable endCallback){
        //获取最终的颜色值
        int finalColor = Resource.Color.WHITE;//UiCompat.getColor(getResources(),R.color.white);
        //运算当前进度的颜色
        ArgbEvaluator evaluator =  new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress,mBgDrawable.getColor(),finalColor);
        //构建一个属性动画
        ValueAnimator valueAnimator =
                ObjectAnimator.ofObject(this,property,evaluator,endColor);
        valueAnimator.setDuration(1500);
        valueAnimator.setIntValues(mBgDrawable.getColor(),endColor);//开始结束
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //结束时触发
                endCallback.run();
            }
        });
        valueAnimator.start();
    }

    private final Property<LaunchActivity,Object> property =
            new Property<LaunchActivity, Object>(Object.class,"color") {
        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }

        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }
    };
}
