package fengyun.android.com.factory.presenter.message;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

import fengyun.android.com.factory.data.BaseDbRepository;
import fengyun.android.com.factory.model.db.Session;
import fengyun.android.com.factory.model.db.Session_Table;

/**
 * session数据监控
 * 最近聊天列表的监控，是对SessionDataSource的实现
 *
 * @author fengyun
 */

public class SessionRepository extends BaseDbRepository<Session>
        implements SessionDataSource {


    @Override
    public void load(SuccessedCallback<List<Session>> callback) {
        super.load(callback);
        //查询session的数据
        SQLite.select()
                .from(Session.class)
                .orderBy(Session_Table.modifyAt, false)//倒序查询
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Session session) {
        //所有的会话我度需要，不需要过滤
        return true;
    }

    @Override
    protected void insert(Session session) {
        //复写方法，让新的数据加到头部,最后一个数据是最后面的
        dataList.addFirst(session);
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Session> tResult) {
        //反转返回的集合
        Collections.reverse(tResult);
        //然后进行调度
        super.onListQueryResult(transaction, tResult);
    }
}
