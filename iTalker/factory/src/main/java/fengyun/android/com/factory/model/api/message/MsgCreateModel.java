package fengyun.android.com.factory.model.api.message;

import java.util.Date;
import java.util.UUID;

import fengyun.android.com.factory.model.card.MessageCard;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.persistence.Account;

/**
 * Created by Administrator on 2017/6/19 0019.
 */

public class MsgCreateModel {
    //Id从客户端生成，一个UUID
    private String id;
    private String content;
    private String attach;
    private int type = Message.TYPE_STR;
    private String receiverId;

    //接受者的类型,群、人
    private int receiverType = Message.RECEIVER_TYPE_NONE;

    private MsgCreateModel() {
        //随机生成一个uuid
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAttach() {
        return attach;
    }

    public int getType() {
        return type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }


    //当我们需要发送一个文件的时候，Content刷新的问题

    private MessageCard card;

    /**
     * 返回 一个MessageCard的卡片
     *
     * @return
     */
    public MessageCard buildCard() {
        if (card == null) {
            MessageCard card = new MessageCard();
            card.setId(id);
            card.setContent(content);
            card.setAttach(attach);
            card.setType(type);
            card.setSenderId(Account.getUserId());
            //如果是群
            if (receiverType == Message.RECEIVER_TYPE_GROUP) {
                card.setGroupId(receiverId);
            } else {
                card.setReceiverId(receiverId);
            }
            //通过当前的Model建立的Card就是初步的状态
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
            this.card = card;
        }
        return card;
    }

    //同步到卡片的最新状态
    public void refreshByCard() {
        if (card == null)
            return;
        //刷新内容和附近信息
        this.content = card.getContent();
        this.attach = card.getAttach();
    }


    /**
     * 建造者模式，快速的建议一个发送Model
     */
    public static class Builder {

        private MsgCreateModel model;

        public Builder() {
            this.model = new MsgCreateModel();
        }

        /**
         * 设置接受者
         *
         * @param receiverId
         * @param receiverType
         * @return
         */
        public Builder receiver(String receiverId, int receiverType) {
            model.receiverId = receiverId;
            model.receiverType = receiverType;
            return this;
        }

        /**
         * 设置内容
         *
         * @param content
         * @param type
         * @return
         */
        public Builder content(String content, int type) {
            this.model.content = content;
            this.model.type = type;
            return this;
        }

        public Builder attach(String attach) {
            this.model.attach = attach;
            return this;
        }

        public MsgCreateModel build() {
            return this.model;
        }

    }

    /**
     * 把一个message消息转换为一个创建状态的MsgCreateModel
     *
     * @param message
     * @return
     */
    public static MsgCreateModel buildWithMessage(Message message) {
        MsgCreateModel model = new MsgCreateModel();
        model.id = message.getId();
        model.content = message.getContent();
        model.type = message.getType();
        model.attach = message.getAttach();
        //如果接受者不为NULL,那么是给人发送
        if (message.getReceiver() != null) {
            model.receiverId = message.getReceiver().getId();
            model.receiverType = Message.RECEIVER_TYPE_NONE;
        } else {
            model.receiverId = message.getGroup().getId();
            model.receiverType = Message.RECEIVER_TYPE_GROUP;
        }

        return model;
    }


}
