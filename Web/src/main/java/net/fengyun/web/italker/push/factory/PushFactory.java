package net.fengyun.web.italker.push.factory;

import com.google.common.base.Strings;
import net.fengyun.web.italker.push.bean.api.base.PushModel;
import net.fengyun.web.italker.push.bean.card.GroupMemberCard;
import net.fengyun.web.italker.push.bean.card.MessageCard;
import net.fengyun.web.italker.push.bean.card.UserCard;
import net.fengyun.web.italker.push.bean.db.*;
import net.fengyun.web.italker.push.utils.Hib;
import net.fengyun.web.italker.push.utils.PushDispatcher;
import net.fengyun.web.italker.push.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消息存储并发送消息
 *
 * @author fengyun
 */
public class PushFactory {

    //发送一条消息，并在当前的发送历史记录中存储数据
    public static void pushNewMessage(User sender, Message message) {
        if (sender == null || message == null)
            return;

        //消息卡片
        MessageCard messageCard = new MessageCard(message);
        String entity = TextUtil.toJson(messageCard);
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();

        if (message.getGroup() == null && Strings.isNullOrEmpty(message.getGroupId())) {
            //给朋友发送信息
            User receiver = UserFactory.findById(message.getReceiverId());
            if (receiver == null)
                return;
            //历史记录表字段建立
            PushHistory history = new PushHistory();
            history.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setSender(message.getSender());
            history.setReceiverId(receiver.getId());
            //当前接受者的设备id
            history.setReceiverPushId(receiver.getPushId());

            PushModel model = new PushModel();
            //每一条；历史记录都市独立的，可以单独的发送
            model.add(history.getEntityType(), history.getEntity());
            //把需要发送的数据，丢给发送者发送
            dispatcher.add(receiver, model);
            //保存到数据库
            Hib.queryOnly(session -> session.save(history));
        } else {
            //查询一个群，因为延迟加载情况，可能为null，要通过id查询
            Group group = message.getGroup();
            if (group == null) {
                group = GroupFactory.findById(message.getGroupId());
            }
            //如果真的没有这个群，就返回
            if (group == null) {
                return;
            }
            Set<GroupMember> members = GroupFactory.getMember(group);
            if (members == null || members.size() == 0)
                return;
            //过滤我自己,意思就是把这个消息发给群里面除了我的其他人
            members = members.stream()
                    .filter(groupMember -> !groupMember.getUserId().equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());
            //一个历史记录列表
            List<PushHistory> histories = new ArrayList<>();
            addGroupMembersPushModel(
                    dispatcher,//推送的发送者
                    histories,//数据库要存储的列表
                    members,//所有的成员
                    entity,//发送的数据
                    PushModel.ENTITY_TYPE_MESSAGE//发送的数据类型
//                    ,message.getSender() //消息的发送者
            );
            //保存到数据库
            Hib.queryOnly(session -> {
                for (PushHistory history : histories) {
                    session.saveOrUpdate(history);
                }
            });
        }
        //发送者进行真实的提交
        dispatcher.submit();
    }

    /**
     * 给群成员构建一个消息
     * 把消息存储到历史记录中，每条消息都市一个记录
     */
    private static void addGroupMembersPushModel(PushDispatcher dispatcher,
                                                 List<PushHistory> histories,
                                                 Set<GroupMember> members,
                                                 String entity,
                                                 int entityTypeMessage
//            , User sender
    ) {
        for (GroupMember member : members) {
            //无需通过id找用户
            User receiver = member.getUser();
            if (receiver == null)
                return;
            //历史记录表字段建立
            PushHistory history = new PushHistory();
            history.setEntityType(entityTypeMessage);
            history.setEntity(entity);
            history.setReceiver(receiver);
//            history.setSender(sender);
            //当前接受者的设备id
            history.setReceiverPushId(receiver.getPushId());

            histories.add(history);

            //构建到一个消息Model
            PushModel model = new PushModel()
                    .add(history.getEntityType(), history.getEntity());
            //添加到发送者的数据集中
            dispatcher.add(receiver, model);
        }
    }

    /**
     * 通知一些成员，有成员加入
     *
     * @param members 被加入的成员
     */
    public static void pushJoinGroup(Set<GroupMember> members) {
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();

        //一个历史记录列表
        List<PushHistory> histories = new ArrayList<>();

        for (GroupMember member : members) {
            //无需通过id找用户
            User receiver = member.getUser();
            if (receiver == null)
                return;
            //成员的信息卡片
            GroupMemberCard memberCard = new GroupMemberCard(member);
            String entity = TextUtil.toJson(memberCard);
            //历史记录表字段建立
            PushHistory history = new PushHistory();
            //你被加入到群的类型
            history.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP);
            history.setEntity(entity);
            history.setReceiver(receiver);
//            history.setSender(sender);
            //当前接受者的设备id
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);

            //构建到一个消息Model
            PushModel model = new PushModel()
                    .add(history.getEntityType(), history.getEntity());
            //添加到发送者的数据集中
            dispatcher.add(receiver, model);
        }
        //保存到数据库
        Hib.queryOnly(session -> {
            for (PushHistory history : histories) {
                session.saveOrUpdate(history);
            }
        });
        //提交发送
        dispatcher.submit();

    }

    /**
     * 通知老的成员，有一系列成员加入到某个群
     *
     * @param oldMembers  老的成员
     * @param insertCards 新的成员
     */
    public static void pushGroupMemberAdd(Set<GroupMember> oldMembers, List<GroupMemberCard> insertCards) {
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();

        //一个历史记录列表
        List<PushHistory> histories = new ArrayList<>();
        //当前新增的用户的集合的Json字符串
        String entity = TextUtil.toJson(insertCards);
        //进行循环添加,给每一个老的用户构建一个消息，消息的内容为新增的用户的集合
        //通知的类型是群成员添加了的类型
        addGroupMembersPushModel(dispatcher, histories, oldMembers, entity, PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);
        //保存到数据库
        Hib.queryOnly(session -> {
            for (PushHistory history : histories) {
                session.saveOrUpdate(history);
            }
        });
        //提交发送
        dispatcher.submit();
    }

    /**
     * 推送账户退出消息
     * @param receiver 接受者
     * @param pushId 这个时刻的接受者的设备id
     */
    public static void pushLogout(User receiver) {

        PushHistory history = new PushHistory();
        history.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        history.setEntity("Account logout!!!");
        history.setReceiver(receiver);
        //当前接受者的设备id
        history.setReceiverPushId(receiver.getPushId());
        Hib.queryOnly(session -> session.save(history));
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();
        //具体推送的内容
        PushModel model = new PushModel()
                .add(history.getEntityType(), history.getEntity());
        dispatcher.add(receiver,model);
        //发送者
        dispatcher.submit();
    }

    /**
     * 推送朋友圈消息
     * @param receiver 接受者
     */
    public static void pushFriendCircle(User receiver) {

        PushHistory history = new PushHistory();
        history.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        history.setEntity("你有一条新消息");
        history.setReceiver(receiver);
        //当前接受者的设备id
        history.setReceiverPushId(receiver.getPushId());
        Hib.queryOnly(session -> session.save(history));
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();
        //具体推送的内容
        PushModel model = new PushModel()
                .add(history.getEntityType(), history.getEntity());
        dispatcher.add(receiver,model);
        //发送者
        dispatcher.submit();
    }
    /**
     * 给一个朋友推送我的信息过去
     * 类型是我关注了他
     * @param receiver
     * @param userCard
     */
    public static void pushFollow(User receiver, UserCard userCard) {

        userCard.setFollow(true);

        PushHistory history = new PushHistory();
        String entity = TextUtil.toJson(userCard);
        history.setEntityType(PushModel.ENTITY_TYPE_ADD_FRIEND);
        history.setEntity(entity);
        history.setReceiver(receiver);
        //当前接受者的设备id
        history.setReceiverPushId(receiver.getPushId());
        Hib.queryOnly(session -> session.save(history));
        PushDispatcher dispatcher = new PushDispatcher();
        //具体推送的内容
        PushModel model = new PushModel()
                .add(history.getEntityType(), history.getEntity());

        dispatcher.add(receiver,model);
        dispatcher.submit();
    }
}
