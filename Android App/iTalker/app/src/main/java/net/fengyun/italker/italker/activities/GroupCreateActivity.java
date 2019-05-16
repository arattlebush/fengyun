package net.fengyun.italker.italker.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.fengyun.italker.common.app.Application;
import net.fengyun.italker.common.app.PresenterToolbarActivity;
import net.fengyun.italker.common.widget.PortraitView;
import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.frags.media.GalleryFragment;
import net.qiujuer.genius.ui.widget.EditText;

import java.io.File;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import fengyun.android.com.factory.presenter.group.GroupCreateContract;
import fengyun.android.com.factory.presenter.group.GroupCreatePresenter;

public class GroupCreateActivity extends PresenterToolbarActivity<GroupCreateContract.Presenter>
        implements GroupCreateContract.View {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;


    @BindView(R.id.edit_name)
    EditText mName;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    private Adapter mAdapter;

    private String mPortraitPath;


    public static void show(Context context) {
        context.startActivity(new Intent(context, GroupCreateActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_create;
    }

    @Override
    protected void initWeight() {
        super.initWeight();
        setTitle("");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new Adapter());


    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        hideSoftKeyboard();
        new GalleryFragment()
                .setListener(new GalleryFragment.OnSelectedListener() {
                    @Override
                    public void onSelectedImage(String path) {
                        UCrop.Options options = new UCrop.Options();
                        //设置图片处理的格式JPEG
                        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                        //设置压缩的精度
                        options.setCompressionQuality(96);
                        //得到头像的缓存地址
                        File dPath = Application.getPortraitTmpFile();
                        //第一个参数原始路径，第二个接收路径
                        //发起剪切
                        UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                                .withAspectRatio(1, 1)//1比1比例
                                .withMaxResultSize(520, 520)//返回最大的尺寸
                                .withOptions(options)//相关参数
                                .start(GroupCreateActivity.this);
                    }
                })
                .show(getSupportFragmentManager()
                        , GalleryFragment.class.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_create, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create) {
            //进行创建
            onCreateClick();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onCreateClick() {
        hideSoftKeyboard();
        String name = mName.getText().toString().trim();
        String desc = mDesc.getText().toString().trim();
        mPresenter.create(name, desc, mPortraitPath);
    }

    /**
     * 隐藏软键盘
     */
    private void hideSoftKeyboard() {
        //当前焦点的View，可能为NULL
        View view = getCurrentFocus();
        if (view == null)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //设置隐藏
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //创建成功的时候
    @Override
    public void onCreateSucceed() {
        hideLoading();
        Application.showToast(R.string.label_group_create_succeed);
        finish();
    }

    @Override
    protected GroupCreateContract.Presenter initPresenter() {
        return new GroupCreatePresenter(this);
    }

    @Override
    public RecyclerAdapter<GroupCreateContract.ViewModel> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //数据加载成功的时候
        hideLoading();
    }


    private class Adapter extends RecyclerAdapter<GroupCreateContract.ViewModel> {

        @Override
        protected ViewHolder<GroupCreateContract.ViewModel> onCreateViewHolder(View root, int viewType) {
            return new GroupCreateActivity.ViewHolder(root);
        }

        @Override
        protected int getItemViewType(int position, GroupCreateContract.ViewModel viewHolder) {
            return R.layout.cell_group_create_contact;
        }
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCreateContract.ViewModel> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.cb_select)
        android.widget.CheckBox mSelect;


        public ViewHolder(View itemView) {
            super(itemView);
        }


        @OnCheckedChanged(R.id.cb_select)
        void onCheckChanged(boolean checked){
            //进行状态更改
            mPresenter.changeSelect(mData,checked);
        }

        @Override
        protected void onBind(GroupCreateContract.ViewModel viewHolder) {
            mPortrait.setup(Glide.with(GroupCreateActivity.this),viewHolder.author.getPortrait());
            mName.setText(viewHolder.author.getName());
            mSelect.setChecked(viewHolder.isSelected);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //收到从activity传递的信息，然后取出其中的值进行图片加载
        //如果是我能够处理的类型
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //通过UCORP得到对应的URI
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                //加载头像
                loadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Application.showToast(R.string.data_rsp_error_unknown);
        }
    }

    /**
     * 加载uri到当前的头像
     * @param uri 返回过来的图片
     */
    private void loadPortrait(Uri uri) {
        //得到头像地址
        mPortraitPath = uri.getPath();
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
    }
}
