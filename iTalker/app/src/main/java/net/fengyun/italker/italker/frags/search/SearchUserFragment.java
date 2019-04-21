package net.fengyun.italker.italker.frags.search;


import android.support.annotation.StringRes;
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
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import fengyun.android.com.factory.model.card.UserCard;
import fengyun.android.com.factory.presenter.contact.FollowContract;
import fengyun.android.com.factory.presenter.contact.FollowPresenter;
import fengyun.android.com.factory.presenter.search.SearchContract;
import fengyun.android.com.factory.presenter.search.SearchUserPresenter;

/**
 * @author fengyun
 *         搜索人
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchActivity.SearchFragment, SearchContract.UserView {


    private static final String TAG = "SearchUserFragment";

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<UserCard> mAdapter;

    public SearchUserFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchUserPresenter(this);
    }

    @Override
    protected void initWeight(View root) {
        super.initWeight(root);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<UserCard>() {
            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                return new SearchUserFragment.ViewHolder(root);
            }

            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                //其实是返回cell的布局id
                return R.layout.cell_search;
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
    public void onSearchDone(List<UserCard> userCards) {
        //数据加载成功的时候返回数据
        mAdapter.replace(userCards);
        //如果有布局就显示布局，没有布局就显示空布局
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }


    //每一个Cell布局的操作
    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard>
            implements FollowContract.View {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.im_follow)
        ImageView mFollow;

        private FollowContract.Presenter mPresenter;

        public ViewHolder(View itemView) {
            super(itemView);
            //当前view与presenter绑定起来
            new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            mPortraitView.setup(Glide.with(SearchUserFragment.this),userCard);
            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }
        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            //显示信息
            PersonalActivity.show(getContext(),mData.getId());
        }

        @OnClick(R.id.im_follow)
        void onFollowClick() {
            //发起关注
            mPresenter.follow(mData.getId());
        }

        @Override
        public void showError(@StringRes int str) {
            if (mFollow.getDrawable() instanceof LoadingDrawable) {
                //失败则停止动画，并且显示一个圈圈
                LoadingDrawable drawable = ((LoadingDrawable) mFollow.getDrawable());
                drawable.setProgress(1);
                drawable.stop();
            }
        }

        @Override
        public void showLoading() {
            int minSize = (int) Ui.dipToPx(getResources(), 22);
            int maxSize = (int) Ui.dipToPx(getResources(), 30);
            LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
            //初始化一个圆形的动画的Drawable
            drawable.setBackgroundColor(0);
            drawable.setForegroundColor(new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)});
            mFollow.setImageDrawable(drawable);
            //启动动画
            drawable.start();
        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            this.mPresenter = presenter;
        }

        @Override
        public void onFollowSucceed(UserCard userCard) {
            //更改drawable状态
            if (mFollow.getDrawable() instanceof LoadingDrawable) {
                ((LoadingDrawable) mFollow.getDrawable()).stop();
                //设置为默认的Drawable
                mFollow.setImageResource(R.drawable.sel_opt_done_add);
            }
            //发起更新
            updateData(userCard);
        }
    }
}
