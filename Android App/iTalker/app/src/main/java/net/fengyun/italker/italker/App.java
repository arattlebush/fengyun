package net.fengyun.italker.italker;

import android.content.Context;

import com.igexin.sdk.PushManager;

import net.fengyun.italker.common.app.Application;
import net.fengyun.italker.italker.activities.AccountActivity;

import org.litepal.LitePalApplication;
import org.litepal.util.Const;

import fengyun.android.com.factory.Factory;

/**
 * @author fengyun
 * 应用程序
 */
public class App extends Application{


    @Override
    public void onCreate() {
        super.onCreate();
        //调用factory进行初始化
        Factory.setup();
        //个推推送进行初始化
        PushManager.getInstance().initialize(this);
        LitePalApplication.initialize(this);


    }

    @Override
    protected void showAccountActivity(Context context) {
        //登录界面的显示
        AccountActivity.show(context);
    }
}
