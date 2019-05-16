package net.fengyun.italker.italker.frags.search;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.fengyun.italker.common.app.PresenterFragment;
import net.fengyun.italker.common.widget.EmptyView;
import net.fengyun.italker.common.widget.PortraitView;
import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.activities.PersonalActivity;
import net.fengyun.italker.italker.activities.SearchActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fengyun.android.com.factory.model.card.GroupCard;
import fengyun.android.com.factory.presenter.search.SearchContract;
import fengyun.android.com.factory.presenter.search.SearchGroupPresenter;

/**
 * @author fengyun
 * 搜索群
 */
public class SearchGroupFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment,SearchContract.GroupView{
    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<GroupCard> mAdapter;

    public SearchGroupFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }


    @Override
    protected void initWeight(View root) {
        super.initWeight(root);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<GroupCard>() {
            @Override
            protected ViewHolder<GroupCard> onCreateViewHolder(View root, int viewType) {
                return new SearchGroupFragment.ViewHolder(root);
            }

            @Override
            protected int getItemViewType(int position, GroupCard groupCard) {
                //其实是返回cell的布局id
                return R.layout.cell_search_group;
            }
        });
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        //发起首次搜索
        search("");
    }

    @Override
    public void search(String content) {
        //Activity ---Fragment ---Presenter ---Net ----Activity
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchGroupPresenter(this);
    }

    @Override
    public void onSearchDone(List<GroupCard> groupCards) {
        //数据加载成功的时候返回
        mAdapter.replace(groupCards);
        //如果有布局就显示布局，没有布局就显示空布局
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }


    //每一个Cell布局的操作
    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCard>
    {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.im_join)
        ImageView mJoin;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(GroupCard groupCard) {
            mPortraitView.setup(Glide.with(SearchGroupFragment.this),groupCard.getPicture());
            mName.setText(groupCard.getName());
            //加入时间为null的话说明没有加入
            mJoin.setEnabled(groupCard.getJoinAt()==null);
        }

        @OnClick(R.id.im_join)
        void onJoinClick(){
            //跳转到个人界面
            PersonalActivity.show(getContext(),mData.getOwnerId());
        }
    }
}
