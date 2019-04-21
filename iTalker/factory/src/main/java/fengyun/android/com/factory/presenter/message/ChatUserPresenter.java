package fengyun.android.com.factory.presenter.message;

import fengyun.android.com.factory.data.helper.UserHelper;
import fengyun.android.com.factory.data.message.MessageRepository;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.model.db.User;

/**
 * 用户聊天的逻辑
 *
 * @author fengyun
 */

public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView>
        implements ChatContract.Presenter {

    public ChatUserPresenter(ChatContract.UserView view, String receiverId) {
        //数据源、View、接受者、接受者的类型
        super(new MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);


    }

    @Override
    public void start() {
        super.start();
        //从本地拿到这个人的信息
        User mReceiver = UserHelper.findFromLocal(mReceiverId);
        //设置用户数据到界面
        getView().onInit(mReceiver);
    }
}
