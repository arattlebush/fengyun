package fengyun.android.com.factory.data.message;

import fengyun.android.com.factory.model.card.MessageCard;

/**
 * 消息卡片的消费
 * @author fengyun
 */

public interface MessageCenter {
    //分发处理一堆消息卡片的信息，并更新到数据库
    void  dispatch(MessageCard... cards);
}
