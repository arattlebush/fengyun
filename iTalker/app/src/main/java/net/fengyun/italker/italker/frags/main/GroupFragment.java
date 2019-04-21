package net.fengyun.italker.italker.frags.main;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.fengyun.italker.common.app.PresenterFragment;
import net.fengyun.italker.common.widget.EmptyView;
import net.fengyun.italker.common.widget.PortraitView;
import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.activities.MessageActivity;

import butterknife.BindView;
import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.presenter.group.GroupsContract;
import fengyun.android.com.factory.presenter.group.GroupsPresenter;


/**
 *@author fengyun
 * 聊天
 */
public class GroupFragment extends PresenterFragment<GroupsContract.Presenter>
implements GroupsContract.View{


    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;


    //适配器 可以直接从数据库拿到数据
    private RecyclerAdapter<Group> mAdapter;

    public GroupFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_group;
    }

    @Override
    protected void initWeight(View root) {
        super.initWeight(root);
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Group>() {
            @Override
            protected ViewHolder<Group> onCreateViewHolder(View root, int viewType) {
                return new GroupFragment.ViewHolder(root);
            }

            @Override
            protected int getItemViewType(int position, Group group) {
                //其实是返回cell的布局id
                return R.layout.cell_group_list;
            }
        });
        //点击事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Group>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder viewHolder, Group group) {
                //跳转到聊天界面
                MessageActivity.show(getContext(),group);
            }
        });
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }


    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        //进行数据的第一次请求
        mPresenter.start();
    }

    @Override
    protected GroupsContract.Presenter initPresenter() {
        return new GroupsPresenter(this);
    }


    @Override
    public RecyclerAdapter<Group> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //进行界面操作
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }


    //每一个Cell布局的操作
    class ViewHolder extends RecyclerAdapter.ViewHolder<Group> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        @BindView(R.id.txt_member)
        TextView mMember;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Group group) {
            mPortraitView.setup(Glide.with(GroupFragment.this),group.getPicture());
            mName.setText(group.getName());
            mDesc.setText(group.getDesc());
            if(group.holder!=null && group.holder instanceof String){
                mMember.setText((String)group.holder);
            }else{
                mMember.setText("");
            }
        }
    }


}
