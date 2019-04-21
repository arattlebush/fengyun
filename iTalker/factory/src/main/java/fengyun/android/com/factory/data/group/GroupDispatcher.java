package fengyun.android.com.factory.data.group;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fengyun.android.com.factory.data.helper.DbHelper;
import fengyun.android.com.factory.data.helper.GroupHelper;
import fengyun.android.com.factory.data.helper.UserHelper;
import fengyun.android.com.factory.data.user.UserCenter;
import fengyun.android.com.factory.model.card.GroupCard;
import fengyun.android.com.factory.model.card.GroupMemberCard;
import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.model.db.GroupMember;
import fengyun.android.com.factory.model.db.User;

/**
 * 群中心的定义
 *
 * @author fengyun
 */
public class GroupDispatcher implements GroupCenter {

    private static GroupCenter instance;

    //单线程池，处理卡片一个个的消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    //单例
    public static GroupCenter instance() {
        if (instance == null) {
            synchronized (UserCenter.class) {
                if (instance == null) {
                    instance = new GroupDispatcher();
                }
            }
        }
        return instance;
    }

    @Override
    public void dispatch(GroupCard... cards) {
        if (cards == null || cards.length == 0) {
            return;
        }
        //丢到单线程池中
        executor.execute(new GroupDispatcher.GroupHandler(cards));
    }

    @Override
    public void dispatch(GroupMemberCard... cards) {
        if (cards == null || cards.length == 0) {
            return;
        }
        //丢到单线程池中
        executor.execute(new GroupDispatcher.GroupMemberHandler(cards));
    }

    /**
     * 群的卡片线程调度的run方法
     */
    private class GroupHandler implements Runnable {
        private final GroupCard[] cards;

        GroupHandler(GroupCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            //当被线程调度的时候触发
            List<Group> groups = new ArrayList<>();
            for (GroupCard card : cards) {
                //搜索管理员
                User owner = UserHelper.search(card.getOwnerId());
                if (owner != null) {
                    Group group = card.build(owner);
                    groups.add(group);
                }
            }
            //进行数据库存储并分发通知，异步的操作
            if (groups.size() > 0)
                DbHelper.save(Group.class, groups.toArray(new Group[0]));
        }
    }

    /**
     * 群成员的卡片线程调度的run方法
     */
    private class GroupMemberHandler implements Runnable {
        private final GroupMemberCard[] cards;

        GroupMemberHandler(GroupMemberCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            //当被线程调度的时候触发
            List<GroupMember> groupMembers = new ArrayList<>();
            for (GroupMemberCard model : cards) {
                //搜索对应的人信息
                User user = UserHelper.search(model.getUserId());
                //搜索对应的群信息
                Group group = GroupHelper.find(model.getGroupId());
                if (user != null || group != null) {
                    GroupMember member = model.build(group, user);
                    groupMembers.add(member);
                }
            }
            //进行数据库存储并分发通知，异步的操作
            if (groupMembers.size() > 0)
                DbHelper.save(GroupMember.class, groupMembers.toArray(new GroupMember[0]));
        }
    }
}
