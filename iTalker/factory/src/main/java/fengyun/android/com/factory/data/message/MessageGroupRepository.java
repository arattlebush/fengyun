package fengyun.android.com.factory.data.message;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

import fengyun.android.com.factory.data.BaseDbRepository;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.model.db.Message_Table;

/**
 * 跟群聊天的时候的聊天列表
 * 关注的内容一定是我发给群的，或者是别人发送给群的
 *
 * @author fengyun
 */

public class MessageGroupRepository extends BaseDbRepository<Message>
        implements MessageDataSource {
    //聊天的groupId
    private String receiverId;

    public MessageGroupRepository(String receiverId) {
        super();//调用积累的构造方法
        this.receiverId = receiverId;
    }

    @Override
    public void load(SuccessedCallback<List<Message>> callback) {
        super.load(callback);
        //无论是自己发的还是别人发的，只要是发到这个群的，
        // 那么这个group_id就是receiverId
        SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(receiverId))
                .orderBy(Message_Table.createAt,false)//倒序查询
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();

    }

    @Override
    protected boolean isRequired(Message message) {
        //如果消息的Group不为空，则一定是发送到一个群的
        //如果群Id等于我们需要的，那就是通过
        return message.getGroup()!=null  && receiverId.equalsIgnoreCase(
                message.getGroup().getId());
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        //反转返回的集合
        Collections.reverse(tResult);
        //然后进行调度
        super.onListQueryResult(transaction, tResult);
    }
}
