package net.fengyun.italker.italker.frags.message;


import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;

import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.activities.GroupMemberActivity;
import net.fengyun.italker.italker.activities.PersonalActivity;

import java.util.List;

import butterknife.BindView;
import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.model.db.view.MemberUserModel;
import fengyun.android.com.factory.presenter.message.ChatContract;
import fengyun.android.com.factory.presenter.message.ChatGroupPresenter;

/**
 * 在群里聊天的界面
 *
 * @author fengyun
 */
public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {


    @BindView(R.id.im_header)
    ImageView mHeader;

    @BindView(R.id.lay_members)
    LinearLayout mLayMembers;

    @BindView(R.id.txt_member_more)
    TextView mMemberMore;

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_group;
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return new ChatGroupPresenter(this, mReceiverId);
    }

    @Override
    protected void initWeight(View root) {
        super.initWeight(root);
        //实现头部渐变
        Glide.with(this)
                .load(R.drawable.default_banner_group)
                .centerCrop()
                .into(new ViewTarget<CollapsingToolbarLayout, GlideDrawable>(mCollapsingToolbarLayout) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setContentScrim(resource.getCurrent());
                    }
                });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        super.onOffsetChanged(appBarLayout, verticalOffset);
        View view = mLayMembers;
        if (view == null)
            return;
        if (verticalOffset == 0) {
            //完全展开，头像是可见的
            mLayMembers.setVisibility(View.VISIBLE);
            view.setScaleX(1);
            view.setScaleY(1);
            view.setAlpha(1);
        } else {
            //abs运算
            verticalOffset = Math.abs(verticalOffset);
            final int totalScrollRange = appBarLayout.getTotalScrollRange();
            if (verticalOffset >= totalScrollRange) {
                //关闭状态
                view.setVisibility(View.INVISIBLE);
                view.setScaleX(0);
                view.setScaleY(0);
                view.setAlpha(0);
            } else {
                //中间状态
                float progress = 1 - verticalOffset / (float) totalScrollRange;
                view.setVisibility(View.VISIBLE);
                view.setScaleX(progress);
                view.setScaleY(progress);
                view.setAlpha(progress);
            }
        }
    }

    @Override
    public void onInit(Group group) {
        //初始化群的信息
        mCollapsingToolbarLayout.setTitle(group.getName());
        Glide.with(this)
                .load(group.getPicture())
                .centerCrop()
                .placeholder(R.drawable.default_banner_group)
                .into(mHeader);
    }

    @Override
    public void onInitGroupMembers(List<MemberUserModel> members, long moreCount) {
        if (members == null || members.size() == 0)
            return;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (final MemberUserModel member : members) {
            //添加成员头像
            ImageView p = (ImageView) inflater.inflate(R.layout.lay_chat_group_portrait, mLayMembers, false);
            mLayMembers.addView(p, 0);
            Glide.with(this)
                    .load(member.getPortrait())
                    .placeholder(R.drawable.default_portrait)
                    .centerCrop()
                    .dontAnimate()
                    .into(p);
            //个人桌面点击
            p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonalActivity.show(getContext(), member.getUserId());
                }
            });
        }
        //更多的按钮
        if (moreCount > 0) {
            mMemberMore.setText(String.format("+%s", moreCount));
            mMemberMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 进行群成员添加 mReceiverId就是群的id
                    GroupMemberActivity.show(getContext(), mReceiverId);
                }
            });
        } else {
            mMemberMore.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAdminOption(boolean isAdmin) {
        if (isAdmin) {
            mToolbar.inflateMenu(R.menu.chat_group);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_add) {
                        // 进行群成员添加 mReceiverId就是群的id
                        GroupMemberActivity.showAdmin(getContext(), mReceiverId);
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
