package fengyun.android.com.factory.data.message;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

import fengyun.android.com.factory.data.BaseDbRepository;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.model.db.Message_Table;

/**
 * 跟某人聊天的时候的聊天列表
 * 关注的内容一定是我发给这个人的，或者是他发送给我的
 *
 * @author fengyun
 */

public class MessageRepository extends BaseDbRepository<Message>
        implements MessageDataSource {
    //聊天的对象id
    private String receiverId;

    public MessageRepository(String receiverId) {
        super();//调用积累的构造方法
        this.receiverId = receiverId;
    }

    @Override
    public void load(SuccessedCallback<List<Message>> callback) {
        super.load(callback);
        //(sender_id == receiverId and group_id==null or receiver_id==receiver_id)
        //如果接受者是你，或者发送者是你必须是群是空的的情况下
        SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt,false)//倒序查询
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();

    }

    @Override
    protected boolean isRequired(Message message) {
        //receiverId如果是发送者，group是空的情况下，一定是发送给我的消息
        //如果消息的接受者不为空，那么一定是发送给某个人的，这个人只能是我或者是某个人
        //如果这个某个人就是receiverId 那么就是我需要关注的信息
        return (receiverId.equalsIgnoreCase(message.getSender().getId())
                && message.getGroup() == null)
                || (message.getReceiver() != null
                && receiverId.equalsIgnoreCase(message.getReceiver().getId()));
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        //反转返回的集合
        Collections.reverse(tResult);
        //然后进行调度
        super.onListQueryResult(transaction, tResult);
    }
}
