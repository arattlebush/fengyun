package fengyun.android.com.factory.data.message;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fengyun.android.com.factory.data.helper.DbHelper;
import fengyun.android.com.factory.data.helper.GroupHelper;
import fengyun.android.com.factory.data.helper.MessageHelper;
import fengyun.android.com.factory.data.helper.UserHelper;
import fengyun.android.com.factory.data.user.UserCenter;
import fengyun.android.com.factory.model.card.MessageCard;
import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.model.db.User;

/**
 * 消息中心的定义
 *
 * @author fengyun
 */

public class MessageDispatcher implements MessageCenter {
    private static MessageCenter instance;

    //单线程池，处理卡片一个个的消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    //单例
    public static MessageCenter instance() {
        if (instance == null) {
            synchronized (UserCenter.class) {
                if (instance == null) {
                    instance = new MessageDispatcher();
                }
            }
        }
        return instance;
    }

    @Override
    public void dispatch(MessageCard... cards) {
        if (cards == null || cards.length == 0) {
            return;
        }
        //丢到单线程池中
        executor.execute(new MessageDispatcher.MessageHandler(cards));
    }


    /**
     * 消息的卡片线程调度的run方法
     */
    private class MessageHandler implements Runnable {
        private final MessageCard[] cards;

        MessageHandler(MessageCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            //当被线程调度的时候触发
            List<Message> messages = new ArrayList<>();
            for (MessageCard card : cards) {
                //卡片基础信息过滤，错误卡片直接过滤
                if (card == null || TextUtils.isEmpty(card.getSenderId())
                        || TextUtils.isEmpty(card.getId())
                        || (TextUtils.isEmpty(card.getReceiverId())
                        && TextUtils.isEmpty(card.getGroupId()))) {
                    continue;
                }
                //消息卡片有可能是推送过来的，也有可能是直接造的
                //推送过开的代表服务器一定有，我们可以查询到（本地可能有，可能没有）
                //如果是直接造的，那么
                //发送消息流程，写消息 --》存储本地 --》发送网路 --》网络返回 ---》刷新本地状态的
                Message message = MessageHelper.findFromLocal(card.getId());
                if (message != null) {
                    //消息本身字段从发送后就不变化，如果收到了消息，本地有，同事本地显示消息状态为完成状态，则不必处理
                    //如果本地消息显示完成已经完成则不做处理
                    if (message.getStatus() == Message.STATUS_DONE) {
                        continue;
                    }
                    //新状态为完成才更新服务器时间，不然不做更新
                    if (card.getStatus() == Message.STATUS_DONE) {
                        //代表网络发送成功，此时需要修改时间为服务器时间
                        message.setCreateAt(card.getCreateAt());
                        //如果没有进入判断，则代表这个消息是发送失败了
                        //重新进行数据库更新而已
                    }
                    //更新一些会变化的内容
                    message.setContent(card.getContent());
                    message.setAttach(card.getAttach());
                    message.setStatus(card.getStatus());
                } else {
                    User sender = UserHelper.search(card.getSenderId());
                    User receiver = null;
                    Group group = null;
                    if (!TextUtils.isEmpty(card.getReceiverId())) {
                        receiver = UserHelper.search(card.getReceiverId());
                    } else if (!TextUtils.isEmpty(card.getGroupId())) {
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }
                    if (receiver == null && group == null && sender!=null) {
                        continue;
                    }
                    message = card.build(sender, receiver, group);
                }
                messages.add(message);
            }
            if (messages.size() > 0)
                //进行数据库存储并分发通知，异步的操作
                DbHelper.save(Message.class, messages.toArray(new Message[0]));
        }
    }
}
