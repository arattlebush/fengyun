package net.fengyun.italker.italker.frags.message;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.fengyun.italker.common.app.Application;
import net.fengyun.italker.common.app.PresenterFragment;
import net.fengyun.italker.common.tools.AudioPlayHelper;
import net.fengyun.italker.common.widget.PortraitView;
import net.fengyun.italker.common.widget.adapter.TextWatcherAdapter;
import net.fengyun.italker.common.widget.recycler.RecyclerAdapter;
import net.fengyun.italker.common.widget.recycler.RecyclerAdapter.AdapterListenerImpl;
import net.fengyun.italker.face.Face;
import net.fengyun.italker.italker.R;
import net.fengyun.italker.italker.activities.MessageActivity;
import net.fengyun.italker.italker.frags.panel.PanelFragment;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;
import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;
import net.qiujuer.widget.airpanel.AirPanel;
import net.qiujuer.widget.airpanel.Util;

import java.io.File;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.persistence.Account;
import fengyun.android.com.factory.presenter.message.ChatContract;
import fengyun.android.com.factory.utils.FileCache;

/**
 * @author fengyun
 *         基本的聊天fragment
 */
//泛型抛给子类
public abstract class ChatFragment<InitModel>
        extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener,
        ChatContract.View<InitModel>, PanelFragment.PanelCallback {


    protected String mReceiverId;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.appBar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.edit_content)
    EditText mContent;

    @BindView(R.id.btn_submit)
    View mSubmit;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    protected Adapter mAdapter;

    //控制顶部面板，与软件盘过度的Boss
    private AirPanel.Boss mPanelBoss;

    private PanelFragment mPanelFragment;

    //下载的工具类
    private FileCache<AudioHolder> mAudioFileCache;


    private AudioPlayHelper<AudioHolder> mAudioPlayer;

    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        //拿到传过来的接受者id
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    //子类不能复写这个方法，在这里终结掉
    @Override
    protected final int getContentLayoutId() {
        return R.layout.fragment_chat_common;
    }

    //得到顶部布局的资源Id
    @LayoutRes
    protected abstract int getHeaderLayoutId();

    @Override
    protected void initWeight(View root) {
        //拿到占位布局
        ViewStub stub = (ViewStub) root.findViewById(R.id.view_stud_header);
        //替换成我们的具体布局,一定要发生在super之前，防止控件绑定异常
        stub.setLayoutResource(getHeaderLayoutId());
        stub.inflate();
        //在这里进行了控件绑定
        super.initWeight(root);

        //初始化面板操作
        mPanelBoss = (AirPanel.Boss) root.findViewById(R.id.lay_content);
        mPanelBoss.setup(new AirPanel.PanelListener() {
            @Override
            public void requestHideSoftKeyboard() {
                //请求隐藏软件盘
                Util.hideKeyboard(mContent);
            }
        });
        mPanelBoss.setOnStateChangedListener(new AirPanel.OnStateChangedListener() {
            @Override
            public void onPanelStateChanged(boolean isOpen) {
                //面板状态改变
                if(isOpen)
                    onBottomPanelOpened();
            }

            @Override
            public void onSoftKeyboardStateChanged(boolean isOpen) {
                //软件盘改变
                if(isOpen)
                    onBottomPanelOpened();
            }
        });
        mPanelFragment = (PanelFragment) getChildFragmentManager().findFragmentById(R.id.frag_panel);
        mPanelFragment.setup(this);
        initToolbar();
        initAppbar();
        initEditContent();
        //mRecyclerView设置
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        //添加监听器,进行点击的实现
        mAdapter.setListener(new AdapterListenerImpl<Message>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Message message) {

                if (message.getType() == Message.TYPE_AUDIO && holder instanceof ChatFragment.AudioHolder) {
                    //权限的判断，当然权限已经全局申请了
                    //文件路径
                    mAudioFileCache.download((AudioHolder) holder,message.getContent());
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        //进入界面后初始化
        mAudioPlayer = new AudioPlayHelper<>(new AudioPlayHelper.RecordPlayListener<AudioHolder>() {

            @Override
            public void onPlayStart(AudioHolder audioHolder) {
                //泛型作用就在于此
                audioHolder.onPlayStart();
            }

            @Override
            public void onPlayStop(AudioHolder audioHolder) {
                //直接停止
                audioHolder.onPlayStop();
            }

            @Override
            public void onPlayError(AudioHolder audioHolder) {
                Application.showToast(R.string.toast_audio_play_error);
            }
        });

        mAudioFileCache = new FileCache("audio/cache",
                "mp3", new FileCache.CacheListener<AudioHolder>() {
            @Override
            public void onDownloadSucceed(final AudioHolder holder,final  File file) {
                Run.onUiAsync(new Action() {
                    @Override
                    public void call() {
                        mAudioPlayer.trigger(holder,file.getAbsolutePath());
                    }
                });

            }

            @Override
            public void onDownloadFailed(AudioHolder audioHolder) {
                Application.showToast(R.string.toast_download_error);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAudioPlayer.destroy();
    }

    private void onBottomPanelOpened(){
        //当底部面板或者软键盘打开时触发
        if (mAppBarLayout != null) {
            mAppBarLayout.setExpanded(false,true);
        }
    }

    @Override
    public boolean onBackPressed() {
        if(mPanelBoss.isOpen()){
            //关闭自己并返回True代表自己拦截已经处理了
            mPanelBoss.closePanel();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.start();
    }

    //初始化toolbar
    protected void initToolbar() {
        Toolbar toolbar = mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }


    //给界面的appbar设置一个监听。得到关闭与打开的时候的进度
    private void initAppbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
    }


    private void initEditContent() {
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                //设置状态，改变对应的Icon
                mSubmit.setActivated(needSendMsg);
            }
        });
    }

    @OnClick(R.id.btn_face)
    void onFaceClick() {
        //仅仅只需要请求打开即可
        mPanelBoss.openPanel();
        mPanelFragment.showFace();
    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {
        //仅仅只需要请求打开即可
        mPanelBoss.openPanel();
        mPanelFragment.showRecord();
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            //发送
            String content = mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(content);
        } else {
            onMoreClick();
        }
    }

    private void onMoreClick() {
        //仅仅只需要请求打开即可
        mPanelBoss.openPanel();
        mPanelFragment.showGallery();
    }

    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        //界面没有占位布局，Recycler布局一直显示的，所以不用做任何操作
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    @Override
    public EditText getInputEditText() {
        return mContent;
    }

    @Override
    public void onSendGallery(String[] paths) {
        //图片回调回来
        mPresenter.pushImages(paths);
    }

    @Override
    public void onRecordDone(File file, long time) {
        //语音回调回来
        mPresenter.pushAudio(file.getAbsolutePath(), time);
    }

    //聊天内容的适配器
    private class Adapter extends RecyclerAdapter<Message> {
        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                case R.layout.cell_chat_text_left:
                case R.layout.cell_chat_text_right:
                    return new TextHolder(root);
                case R.layout.cell_chat_audio_left:
                case R.layout.cell_chat_audio_right:
                    return new AudioHolder(root);
                case R.layout.cell_chat_pic_left:
                case R.layout.cell_chat_pic_right:
                    return new PicHolder(root);
                default:
                    //TODO 默认情况下返回的就是text类型，确实一个文件类型
                    return new TextHolder(root);
            }
        }

        @Override
        protected int getItemViewType(int position, Message message) {
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (message.getType()) {
                //文字内容
                case Message.TYPE_STR:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
                //语音内容
                case Message.TYPE_AUDIO:
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;
                //图片内容
                case Message.TYPE_PIC:
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;
                //TODO 文件内容 自己完善
                case Message.TYPE_FILE:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
            }
            return 0;
        }
    }

    //holder的基类
    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {

        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        //允许为空，左边没有，右边有
        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;


        public BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            //进行数据加载
            sender.load();
            //进行头像加载
            mPortrait.setup(Glide.with(ChatFragment.this), sender);
            int status = message.getStatus();
            if (mLoading != null) {
                //当前布局应该是在右边
                if (status == Message.STATUS_DONE) {
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) {
                    //正在发送中的状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {
                    //发送消息失败状态，允许重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                }
                //当状态是错误状态时才允许点击
                mPortrait.setEnabled(status == Message.STATUS_FAILED);
            }
        }

        @OnClick(R.id.im_portrait)
        void onRePushClick() {
            //重新发送,如果发送成功
            if (mLoading != null && mPresenter.rePush(mData)) {
                //必须是右边，才有可能需要重新发送
                //状态改变需要重新刷新界面
                updateData(mData);
            }
        }
    }

    //文字的holder
    class TextHolder extends BaseHolder {

        @BindView(R.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);

            Spannable spannable = new SpannableString(message.getContent());

            // 解析表情
            Face.decode(mContent, spannable, (int) Ui.dipToPx(getResources(), 20));

            //设置文字内容
            mContent.setText(spannable);
        }
    }

    //语音的holder
    class AudioHolder extends BaseHolder {

        @BindView(R.id.txt_content)
        TextView mContent;
        @BindView(R.id.im_audio_track)
        ImageView mAudioTrack;

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //long  时间
            String attach = TextUtils.isEmpty(message.getAttach()) ? "0" : message.getAttach();
            mContent.setText(formatTime(attach));

        }

        //当播放开始
        void onPlayStart() {
            //显示
            mAudioTrack.setVisibility(View.VISIBLE);
        }

        //当播放停止
        void onPlayStop() {
            //占位并隐藏
            mAudioTrack.setVisibility(View.INVISIBLE);
        }

        private String formatTime(String attach) {
            float time;
            try {
                //毫秒装换成秒
                time = Float.parseFloat(attach) / 1000f;
            } catch (Exception e) {
                time = 0;
            }
//            12000/1000f = 12.000000000;
            //取整一位小数点 1.234 --->1.2
            String shortTime = String.valueOf(Math.round((time * 10) / 10f));
            //1.0-->1  1.20000---1.2
            shortTime = shortTime.replaceAll("[.]0+?$|0+?$", "");
            return String.format("%s", shortTime);
        }

    }

    //图片的holder
    class PicHolder extends BaseHolder {

        @BindView(R.id.im_image)
        ImageView mContent;

        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //当时图片类型的时候，Content就是具体的地址
            String content = message.getContent();
            Glide.with(ChatFragment.this)
                    .load(content)
                    .fitCenter()
                    .into(mContent);
        }
    }

}
