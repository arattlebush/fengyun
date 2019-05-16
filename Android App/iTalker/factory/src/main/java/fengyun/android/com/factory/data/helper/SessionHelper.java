package fengyun.android.com.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import fengyun.android.com.factory.model.db.Session;
import fengyun.android.com.factory.model.db.Session_Table;

/**
 * 会话辅助工具类
 */

public class SessionHelper {
    //从本地找一个session
    public static Session findFromLocal(String id) {
        //TODO 查询会话
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
