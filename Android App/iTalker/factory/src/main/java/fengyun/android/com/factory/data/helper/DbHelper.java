package fengyun.android.com.factory.data.helper;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fengyun.android.com.factory.model.db.AppDataBase;
import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.model.db.GroupMember;
import fengyun.android.com.factory.model.db.Group_Table;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.model.db.Session;

/**
 * 数据库的辅助工具类
 * 辅助完成：增删改
 *
 * @author fengyun
 */

public class DbHelper {
    private static final DbHelper instace;

    static {
        instace = new DbHelper();
    }

    private DbHelper() {

    }

    /**
     * 观察者集合
     * Class<?> 观察到表
     * Set<ChangedListener> 每一个表对应的观察者有很多
     */
    private final Map<Class<?>, Set<ChangedListener>> changedListeners = new HashMap<>();

    /**
     * 从所有的监听器中获取某一个表的所有
     *
     * @param modelClass 表对应的class信息
     * @param <Model>    泛型
     * @return Set<ChangedListener>
     */
    private <Model extends BaseModel> Set<ChangedListener> getListeners(Class<Model> modelClass) {
        if (changedListeners.containsKey(modelClass)) {
            return changedListeners.get(modelClass);
        }
        return null;
    }

    /**
     * 添加一个监听
     *
     * @param tClass   对某个表的关注
     * @param listener 监听者
     * @param <Model>  表的泛型
     */
    public static <Model extends BaseModel> void addChangedListener(final Class<Model> tClass, ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instace.getListeners(tClass);
        if (changedListeners == null) {
            //初始化某一类型的容器
            changedListeners = new HashSet<>();
            //添加到总的map里面
            instace.changedListeners.put(tClass, changedListeners);
        }
        changedListeners.add(listener);
    }

    /**
     * 删除一个监听
     *
     * @param tClass   对某个表的关注
     * @param listener 监听者
     * @param <Model>  表的泛型
     */
    public static <Model extends BaseModel> void removeChangedListener(final Class<Model> tClass, ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instace.getListeners(tClass);
        if (changedListeners == null) {
            //容器为null 根本就没有
            return;
        }
        //从容器中删除你这个监听器
        changedListeners.remove(changedListeners);
    }

    /**
     * 新增和修改的统一方法
     *
     * @param tClass  传递一个class
     * @param models  这个class的数组
     * @param <Model> 这个实例的泛型,限定条件是BaseModel
     */
    public static <Model extends BaseModel> void save(final Class tClass, final Model... models) {
        if (models == null || models.length == 0) {
            return;
        }
        //当前数据库的管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        //提交一个事务
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                //保存
                adapter.saveAll(Arrays.asList(models));
                instace.notifySave(tClass, models);

            }
        }).build().execute();
    }

    /**
     * 删除数据的统一方法
     *
     * @param tClass  传递一个class
     * @param models  这个class的数组
     * @param <Model> 这个实例的泛型,限定条件是BaseModel
     */
    public static <Model extends BaseModel> void delete(final Class tClass, final Model... models) {
        if (models == null || models.length == 0) {
            return;
        }
        //当前数据库的管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        //提交一个事务
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                //删除
                adapter.deleteAll(Arrays.asList(models));
                instace.notifyDelete(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 进行保存通知声明
     *
     * @param tClass  通知的类型
     * @param models  通知的数组
     * @param <Model> 这个实例的泛型,限定条件是BaseModel
     */
    @SuppressWarnings("unchecked")
    private final <Model extends BaseModel> void notifySave(final Class tClass, final Model... models) {
        //找监听器
        final Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            //通用的通知
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataSave(models);
            }
        }
        //列外情况
        //群成员变更，需要通知对应群信息更新
        //消息变化，应该通知会话列表更新

        if (GroupMember.class.equals(tClass)) {
            updateGroup((GroupMember[]) models);
            //群成员
        } else if (Message.class.equals(tClass)) {
            //消息变化
            updateSession((Message[]) models);
        }
    }

    /**
     * 进行删除通知声明
     *
     * @param tClass  通知的类型
     * @param models  通知的数组
     * @param <Model> 这个实例的泛型,限定条件是BaseModel
     */
    private final <Model extends BaseModel> void notifyDelete(final Class tClass, final Model... models) {
        //找监听器
        final Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            //通用的通知
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataDelete(models);
            }
        }
        //列外情况
        if (GroupMember.class.equals(tClass)) {
            updateGroup((GroupMember[]) models);
            //群成员
        } else if (Message.class.equals(tClass)) {
            //消息变化,应该通知会话列表更新
            updateSession((Message[]) models);
        }
    }

    /**
     * 从成员中找到成员对应的群，并对群进行通知
     *
     * @param groupMembers 群列表
     */
    private void updateGroup(GroupMember... groupMembers) {
        //不重复的集合
        final Set<String> groupIds = new HashSet<>();
        for (GroupMember groupMember : groupMembers) {
            //添加群的id
            groupIds.add(groupMember.getGroup().getId());
        }
        //异步的发起二次通知
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                List<Group> groups = SQLite.select()
                        .from(Group.class)
                        .where(Group_Table.id.in(groupIds))
                        .queryList();
                //调用直接进行一次分发
                instace.notifySave(Group.class,groups.toArray(new Group[0]));
            }
        }).build().execute();


    }

    /**
     * 从消息列表中刷选中对应的会话，并进行更新
     *
     * @param messages 消息列表
     */
    private void updateSession(Message... messages) {
        //标示一个Session的唯一性
        final Set<Session.Identify> identifies = new HashSet<>();
        for (Message message : messages) {
            Session.Identify identify = Session.createSessionIdentify(message);
            identifies.add(identify);
        }
        //异步的发起二次通知
        DatabaseDefinition definition = FlowManager.getDatabase(AppDataBase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {

                ModelAdapter<Session> adapter = FlowManager.getModelAdapter(Session.class);
                Session[] sessions = new Session[identifies.size()];
                int index = 0;
                for (Session.Identify identify : identifies) {
                    Session session = SessionHelper.findFromLocal(identify.id);
                    if (session == null) {
                        //第一次聊天，创建一个会话
                        session = new Session(identify);
                    }
                    //把会话刷新到当前列表
                    session.refreshToNow();
                    adapter.save(session);
                    sessions[index++] = session;
                }
                //调用直接进行一次分发
                instace.notifySave(Session.class,sessions);
            }
        }).build().execute();

    }


    /**
     * 通知监听器
     */
    @SuppressWarnings({"unused", "unchecked"})
    public interface ChangedListener<Data extends BaseModel> {
        void onDataSave(Data... list);

        void onDataDelete(Data... list);
    }
}
