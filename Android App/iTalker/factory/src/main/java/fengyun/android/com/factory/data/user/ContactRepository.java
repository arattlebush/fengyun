package fengyun.android.com.factory.data.user;


import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.fengyun.italker.factory.data.DataSource;

import java.util.List;

import fengyun.android.com.factory.data.BaseDbRepository;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.model.db.User_Table;
import fengyun.android.com.factory.persistence.Account;

/**
 * 联系人仓库
 *
 * @author fengyun
 */

public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource{

    private DataSource.SuccessedCallback<List<User>> callback;

    @Override
    public void load(DataSource.SuccessedCallback<List<User>> callback) {
        super.load(callback);
        // 加载本地数据库数据
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))//已经关注的人
                .and(User_Table.id.notEq(Account.getUserId()))//id不等于自己
                .orderBy(User_Table.name, true)//排序
                .limit(100)//最多一百条
                .async()//异步的
                .queryListResultCallback(this).execute();
    }

    @Override
    protected boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }


}
