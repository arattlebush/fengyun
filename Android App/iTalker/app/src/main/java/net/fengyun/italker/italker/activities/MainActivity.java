package net.fengyun.italker.italker.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.fengyun.italker.common.app.Activity;
import net.fengyun.italker.common.widget.PortraitView;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.frags.main.ActiveFragment;
import net.fengyun.italker.italker.frags.main.ContactFragment;
import net.fengyun.italker.italker.frags.main.DynamicFragment;
import net.fengyun.italker.italker.frags.main.GroupFragment;
import net.fengyun.italker.italker.help.NavHelper;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import fengyun.android.com.factory.persistence.Account;

import static net.fengyun.italker.common.app.Application.showToast;

/**
 * @author fengyun
 *         主页
 */
public class MainActivity extends Activity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangedListener<Integer> {


    @BindView(R.id.appBar)
    View mLayAppbar;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.txt_title)
    TextView mTitle;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    private NavHelper<Integer> mNavHelper;
    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    @BindView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;

    /**
     * MainActivity跳转的入口
     *
     * @param context 上下文
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if (Account.isComplete()) {
            //判断用户是否完全，完全走正常流程
            return super.initArgs(bundle);
        } else {
            UserActivity.show(this);
            //返回false会自动关闭activity
            return false;
        }

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWeight() {



        super.initWeight();
        //初始化底部辅助工具类
        mNavHelper = new NavHelper<>(this, R.id.lay_container, getSupportFragmentManager(), this);
        mNavHelper
                .add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact))
                .add(R.id.action_dynamic, new NavHelper.Tab<>(DynamicFragment.class, R.string.title_dynamic));
        mNavigation.setOnNavigationItemSelectedListener(this);
        //设置appbar的背景颜色
        Glide.with(this)
                .load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
        initNav();

        BottomNavigationViewHelper.disableShiftMode(mNavigation);

    }

    private void initNav() {
        final NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        PortraitView portrait = (PortraitView) navView.getHeaderView(0).findViewById(R.id.im_portrait);
        TextView username = (TextView) navView.getHeaderView(0).findViewById(R.id.txt_name);
        TextView autograph = (TextView) navView.getHeaderView(0).findViewById(R.id.txt_autograph);
        final TextView card = (TextView) navView.getHeaderView(0).findViewById(R.id.txt_card);
        autograph.setText(Account.getUser().getDesc());
        username.setText(Account.getUser().getName());
        card.setText("签到");
        portrait.setup(Glide.with(this), Account.getUser().getPortrait());
        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalActivity.show(MainActivity.this,Account.getUserId());
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(false);
                switch (item.getItemId()) {

                    case R.id.nav_real_name: {
                        // TODO 判断用户有没有实名认证
                        RealNameActivity.show(MainActivity.this);
                        break;
                    }
                    case R.id.nav_bank: {
                        BankAccountActivity.show(MainActivity.this);
                        break;
                    }

                    case R.id.nav_shares:{
                        SharesActivity.show(MainActivity.this);
                        break;
                    }

                    case R.id.nav_setting: {
                        SettingActivity.show(MainActivity.this);
                        break;
                    }
                }
                mDrawerLayout.closeDrawers();
                //根据item来决定跳转
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        //从底部导航中接管我们的menu，然后进行手动的触发第一次点击
        //默认第一次点击是首页
        Menu menu = mNavigation.getMenu();
        menu.performIdentifierAction(R.id.action_home, 0);

        //初始化头像加载
        mPortrait.setup(Glide.with(this), Account.getUser());
    }

    @OnClick(R.id.im_portrait)
    void onPortrait() {
        mDrawerLayout.openDrawer(Gravity.START);
        //PersonalActivity.show(this,Account.getUserId());
    }


    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        //在群界面的时候，点击顶部的搜索就进入群搜索界面
        //如果是其他，进入人搜索界面
        int type = Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)
                ? SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);
    }

    @OnClick(R.id.btn_action)
    void onActionClick() {
        //浮动按钮点击，判断当前群界面还是联系人界面
        //如果是群，则打开群创建的界面

        if (Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)) {
            //打开群创建界面
            GroupCreateActivity.show(this);
        } else {
            //如果是其他，度打开添加用户的桌面
            SearchActivity.show(this, SearchActivity.TYPE_USER);
        }

    }

    /**
     * 当我们底部被点击的时候触发
     *
     * @param item
     * @return 返回True代表处理成功
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //转接事件流给工具类
        return mNavHelper.performClickMenu(item.getItemId());
    }

    /**
     * NavHelper处理后的回调方法
     *
     * @param newTab 新的
     * @param oldTab 旧的
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        //从额外的字段取出
        mTitle.setText(newTab.extra);
        //对浮动按钮进行隐藏和显示的动画
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home) || Objects.equals(newTab.extra, R.string.title_dynamic) ) {
            //主界面的异常
            transY = Ui.dipToPx(getResources(), 90);
        } else {
            //tranY默认为0 则显示
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                //群
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                //联系人
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }
        //开始动画 旋转 Y轴位移 弹性差值器，时间
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();
    }
}
