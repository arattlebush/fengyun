package net.fengyun.italker.common.app;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import net.fengyun.italker.common.R;

/**
 * @author fengyun
 *         专门显示toolbar的activity
 */

public abstract class ToolbarActivity extends Activity {

    protected Toolbar mToolbar;

    @Override
    protected void initWeight() {
        super.initWeight();
        initToolbar((Toolbar) findViewById(R.id.toolbar));

    }


    public void initToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            initTitleNeedBack();
        }
    }

    protected void initTitleNeedBack(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
