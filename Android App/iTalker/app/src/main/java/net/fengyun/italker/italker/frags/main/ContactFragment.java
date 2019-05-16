package net.fengyun.italker.italker.frags.main;

import android.support.v7.widget.LinearLayoutManager;
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
import net.fengyun.italker.italker.activities.PersonalActivity;

import butterknife.BindView;
import butterknife.OnClick;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.presenter.contact.ContactContract;
import fengyun.android.com.factory.presenter.contact.ContactPresenter;

/**
 * @author fengyun
 *         联系人
 */
public class ContactFragment extends PresenterFragment<ContactContract.Presenter> implements ContactContract.View{


    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    //适配器 可以直接从数据库拿到数据
    private RecyclerAdapter<User> mAdapter;

    public ContactFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initWeight(View root) {
        super.initWeight(root);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<User>() {
            @Override
            protected ViewHolder<User> onCreateViewHolder(View root, int viewType) {
                return new ContactFragment.ViewHolder(root);
            }

            @Override
            protected int getItemViewType(int position, User userCard) {
                //其实是返回cell的布局id
                return R.layout.cell_contact_list;
            }
        });
        //点击事件监听
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<User>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder viewHolder, User user) {
                //跳转到聊天界面
                MessageActivity.show(getContext(),user);
            }
        });
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }


    @Override
    protected ContactContract.Presenter initPresenter() {
        return new ContactPresenter(this);
    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        //进行数据的第一次请求
        mPresenter.start();
    }

    @Override
    public RecyclerAdapter<User> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //进行界面操作
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount()>0);
    }


    //每一个Cell布局的操作
    class ViewHolder extends RecyclerAdapter.ViewHolder<User> {

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(User user) {
            mPortraitView.setup(Glide.with(ContactFragment.this),user);
            mName.setText(user.getName());
            mDesc.setText(user.getDesc());
        }
        @OnClick(R.id.im_portrait)
        void onPortraitClick() {
            //显示信息
            PersonalActivity.show(getContext(),mData.getId());
        }
    }
}
