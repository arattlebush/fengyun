package fengyun.android.com.factory.presenter.message;

import android.support.v7.util.DiffUtil;
import android.text.TextUtils;

import java.util.List;

import fengyun.android.com.factory.data.helper.MessageHelper;
import fengyun.android.com.factory.data.message.MessageDataSource;
import fengyun.android.com.factory.model.api.message.MsgCreateModel;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.persistence.Account;
import fengyun.android.com.factory.presenter.contact.BaseSourcePresenter;
import fengyun.android.com.factory.utils.DiffUtilDataCallback;

/**
 * 聊天Presenter的基础类
 *
 * @author fengyun
 */

public class ChatPresenter<View extends ChatContract.View> extends
        BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContract.Presenter {

    //接受者的id，可能是群，可能是人
    protected String mReceiverId;
    //接受者的类型 是群还是人
    protected int mReceiverType;

    public ChatPresenter(MessageDataSource source, View view,
                         String receiverId, int receivertype) {
        super(source, view);
        this.mReceiverId = receiverId;
        this.mReceiverType = receivertype;
    }

    @Override
    public void pushText(String content) {
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(content, Message.TYPE_STR)
                .build();
        //进行网络发送
        MessageHelper.push(model);

    }

    @Override
    public void pushAudio(String path,long time) {
        // 发送语音
        if(TextUtils.isEmpty(path))
            return;
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(path, Message.TYPE_AUDIO)
                .attach(String.valueOf(time))//添加时间到附件上面去
                .build();
        //进行网络发送
        MessageHelper.push(model);
    }

    @Override
    public void pushImages(String[] paths) {
        // 发送图片
        if(paths==null || paths.length==0)
        return;
        //此时路径是本地的手机路径,需要变成网上的路径
        for (String path : paths) {

            MsgCreateModel model = new MsgCreateModel.Builder()
                    .receiver(mReceiverId, mReceiverType)
                    .content(path, Message.TYPE_PIC)
                    .build();
            //进行网络发送
            MessageHelper.push(model);
        }
    }

    @Override
    public void pushFile(String path) {
        //TODO 发送文件
    }

    @Override
    public boolean rePush(Message message) {
        //确定消息是可以重复发送的,必须是自己，而且发送者必须是自己
        if (Account.isLogin() && Account.getUserId().equals(message.getSender().getId())
                && message.getStatus() == Message.STATUS_FAILED) {
            //设置状态是创建状态
            message.setStatus(Message.STATUS_CREATED);
            //构建发送model
            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }
        return false;
    }

    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContract.View view = getView();
        if (view == null) {
            return;
        }
        //拿到老数据
        @SuppressWarnings("unchecked")
        List<Message> old = view.getRecyclerAdapter().getItems();
        //计算差异
        DiffUtilDataCallback<Message> callback = new DiffUtilDataCallback<Message>(old, messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //进行界面刷新
        refreshData(result, messages);
    }
}
