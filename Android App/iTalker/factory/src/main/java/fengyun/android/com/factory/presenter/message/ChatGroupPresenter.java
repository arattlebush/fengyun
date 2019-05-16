package fengyun.android.com.factory.presenter.message;

import java.util.List;

import fengyun.android.com.factory.data.helper.GroupHelper;
import fengyun.android.com.factory.data.message.MessageGroupRepository;
import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.model.db.view.MemberUserModel;
import fengyun.android.com.factory.persistence.Account;

/**
 * 群聊天的逻辑
 *
 * @author fengyun
 */

public class ChatGroupPresenter extends ChatPresenter<ChatContract.GroupView>
        implements ChatContract.Presenter {
    public ChatGroupPresenter(ChatContract.GroupView view, String receiverId) {
        super(new MessageGroupRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_GROUP);
    }


    @Override
    public void start() {
        super.start();
        //从本地拿到这个群的信息
        Group group = GroupHelper.findFromLocal(mReceiverId);
        if (group != null) {
            //初始化操作
            ChatContract.GroupView view = getView();
            boolean isAdmin = Account.getUserId().equalsIgnoreCase(group.getOwner().getId());
            view.showAdminOption(isAdmin);
            //基础信息初始化
            view.onInit(group);
            //成员初始化
            List<MemberUserModel> models = group.getLatelyGroupMembers();
            final long memberCount = group.getGroupMemberCount();
            //如果得到的数量大于获取的models，因为models最多为4，而memberCount却没有这个限制
            //小于4或者等于4的情况下是相等的，只有大于4的情况下才会大于models
            //没有显示的成员数量
            long moreCount = memberCount - models.size();
            view.onInitGroupMembers(models, moreCount);

        }
    }
}
