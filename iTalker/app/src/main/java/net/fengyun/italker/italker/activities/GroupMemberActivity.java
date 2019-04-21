package net.fengyun.italker.italker.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.fengyun.italker.common.app.PresenterToolbarActivity;
import net.fengyun.italker.common.widget.PortraitView;
import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;
import net.fengyun.italker.italker.R;

import butterknife.BindView;
import butterknife.OnClick;
import fengyun.android.com.factory.model.db.view.MemberUserModel;
import fengyun.android.com.factory.presenter.group.GroupMembersContract;
import fengyun.android.com.factory.presenter.group.GroupMembersPresenter;

public class GroupMemberActivity extends PresenterToolbarActivity<GroupMembersContract.Presenter>
        implements GroupMembersContract.View {

    private static final String KEY_GROUP_ID = "KEY_GROUP_ID";
    private static final String KEY_GROUP_Admin = "KEY_GROUP_Admin";
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    private RecyclerAdapter<MemberUserModel> mAdapter;
    private boolean mIsAdmin;
    private String mGroupId;

    public static void show(Context context, String groupId) {
        show(context, groupId, false);
    }

    public static void showAdmin(Context context, String groupId) {
        show(context, groupId, true);
    }

    private static void show(Context context, String groupId, boolean isAdmin) {
        if (TextUtils.isEmpty(groupId))
            return;
        Intent intent = new Intent(context, GroupMemberActivity.class);
        intent.putExtra(KEY_GROUP_ID, groupId);
        intent.putExtra(KEY_GROUP_Admin, isAdmin);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_member;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        String groupId = bundle.getString(KEY_GROUP_ID);
        this.mGroupId = groupId;
        this.mIsAdmin = bundle.getBoolean(KEY_GROUP_Admin);
        return !TextUtils.isEmpty(groupId);

    }

    @Override
    protected void initWeight() {
        super.initWeight();

        setTitle(R.string.title_member_list);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<MemberUserModel>() {
            @Override
            protected ViewHolder<MemberUserModel> onCreateViewHolder(View root, int viewType) {
                return new GroupMemberActivity.ViewHolder(root);
            }

            @Override
            protected int getItemViewType(int position, MemberUserModel memberUserModel) {
                return R.layout.cell_group_create_contact;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        //开始刷新数据
        mPresenter.refresh();
    }

    @Override
    public RecyclerAdapter<MemberUserModel> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //隐藏Loading就好
        hideLoading();
    }

    @Override
    protected GroupMembersContract.Presenter initPresenter() {
        return new GroupMembersPresenter(this);
    }

    @Override
    public String getGroupId() {
        return mGroupId;
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<MemberUserModel> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.findViewById(R.id.cb_select).setVisibility(View.GONE);
        }

        @Override
        protected void onBind(MemberUserModel model) {
            mPortrait.setup(Glide.with(GroupMemberActivity.this), model.getPortrait());
            mName.setText(model.getName());
        }

        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            PersonalActivity.show(GroupMemberActivity.this, mData.userId);
        }
    }
}
