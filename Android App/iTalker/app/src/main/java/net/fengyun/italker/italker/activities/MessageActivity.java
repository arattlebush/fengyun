package net.fengyun.italker.italker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import net.fengyun.italker.common.app.Activity;
import net.fengyun.italker.common.app.Fragment;
import net.fengyun.italker.factory.model.Author;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.frags.message.ChatGroupFragment;
import net.fengyun.italker.italker.frags.message.ChatUserFragment;

import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.model.db.Session;

public class MessageActivity extends Activity {
    //可以是群，可以是人的id
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    //是不是群
    private static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";
    private String mReceiverId;
    private boolean mIsGroup;

    /**
     * 显示人的聊天界面
     * @param context
     * @param author
     */
    public static void show(Context context, Author author) {
        if (context == null || author == null || TextUtils.isEmpty(author.getId())) {
            return;
        }
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, author.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, false);
        context.startActivity(intent);
    }

    /**
     * 发起群聊天
     * @param context
     * @param group
     */
    public static void show(Context context, Group group) {
        if (context == null || group == null || TextUtils.isEmpty(group.getId())) {
            return;
        }
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, group.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, true);
        context.startActivity(intent);
    }
    /**
     * 会话聊天
     * @param context
     * @param session
     */
    public static void show(Context context, Session session) {
        if (context == null || session == null || TextUtils.isEmpty(session.getId())) {
            return;
        }
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, session.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, session.getReceiverType() == Message.RECEIVER_TYPE_GROUP);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mReceiverId = bundle.getString(KEY_RECEIVER_ID);
        mIsGroup = bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        return !TextUtils.isEmpty(mReceiverId);
    }


    @Override
    protected void initWeight() {
        super.initWeight();
        setTitle("");
        Fragment fragment;
        if (mIsGroup) {
            fragment = new ChatGroupFragment();
        } else {
            fragment = new ChatUserFragment();
        }
        //从activity传递参数到fragment中去
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECEIVER_ID, mReceiverId);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container, fragment)
                .commit();
    }
}
