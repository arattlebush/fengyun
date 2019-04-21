package net.fengyun.italker.italker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.fengyun.italker.common.app.Activity;
import net.fengyun.italker.common.app.ToolbarActivity;
import net.fengyun.italker.italker.R;

import butterknife.BindView;


public class BigPictureActivity extends ToolbarActivity {


    private final static String BIGPICTURT_PIC = "BIGPICTURT_PIC";
    private final static String PIC_NAME_KEY = "查看大图片";
    private String url = "";
    @BindView(R.id.im_header)
    ImageView mHeader;

    public static void show(Activity activity, View view, String url) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        view, PIC_NAME_KEY);
        Intent intent = new Intent(activity, BigPictureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BIGPICTURT_PIC, url);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        url = bundle.getString("BIGPICTURT_PIC");
        return super.initArgs(bundle);

    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_big_picture;
    }

    @Override
    protected void initWeight() {
        super.initWeight();
        Glide.with(this)
                .load(url)
                .asBitmap()
                .placeholder(R.drawable.default_portrait)
                .into(mHeader);
    }
}
