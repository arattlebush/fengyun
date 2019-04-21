package net.fengyun.italker.italker.activities;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;

import net.fengyun.italker.common.app.ToolbarActivity;
import net.fengyun.italker.italker.R;

import butterknife.BindView;

/**
 * @author fengyun
 * 设置
 */
public class SettingActivity extends ToolbarActivity {

    @BindView(R.id.webView)
    WebView mWebView;
    String url = "http://chajiaosuo.0791jr.com/app.php?m=app&c=member&a=info&id=12";
    public static void show(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initData() {
        super.initData();
        mWebView.loadUrl(url);
    }
}
