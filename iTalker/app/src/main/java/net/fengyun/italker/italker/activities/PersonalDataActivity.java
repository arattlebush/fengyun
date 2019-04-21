package net.fengyun.italker.italker.activities;

import android.content.Context;
import android.content.Intent;

import net.fengyun.italker.common.app.ToolbarActivity;
import net.fengyun.italker.italker.R;

/**
 * 编辑资料
 */
public class PersonalDataActivity extends ToolbarActivity {

    public static void show(Context context) {
        context.startActivity(new Intent(context, PersonalDataActivity.class));
    }



    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_personal_data;
    }
}
