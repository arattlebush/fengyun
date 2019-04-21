package fengyun.android.com.factory.data;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.fengyun.italker.factory.data.DbDataSource;
import net.fengyun.italker.utils.CollectionUtil;
import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import fengyun.android.com.factory.data.helper.DbHelper;
import fengyun.android.com.factory.model.db.BaseDbModel;

/**
 * 基础的数据库仓库
 *
 * @author fengyun
 */

public abstract class BaseDbRepository<Data extends BaseDbModel<Data>> implements
        DbDataSource<Data>, DbHelper.ChangedListener<Data>, QueryTransaction.QueryResultListCallback<Data> {

    //和Presenter交互的回调
    private SuccessedCallback<List<Data>> callback;

    protected final LinkedList<Data> dataList = new LinkedList<>(); //当前缓冲的数据

    private Class<Data> dataClass;//当前泛型对应的真实Class信息

    public BaseDbRepository() {
        //拿当前类的泛型数组信息,反射
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class, this.getClass());
        dataClass = (Class<Data>) types[0];
    }

    @Override
    public void load(SuccessedCallback<List<Data>> callback) {
        this.callback = callback;
        // 进行数据库监听操作
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        this.callback = null;
        DbHelper.removeChangedListener(dataClass, this);
        dataList.clear();
    }

    // 数据库统一通知的地方 增加/修改
    @Override
    public void onDataSave(Data... list) {
        boolean isChanged = false;
        //数据库数据变更的操作
        for (Data data : list) {
            //是关注的人并且不是我自己
            if (isRequired(data)) {
                insertOrUpdate(data);
                isChanged = true;
            }
        }
        if (isChanged) {
            notifyDataChange();
        }
    }

    //数据库统一通知的地方 删除
    @Override
    public void onDataDelete(Data... list) {
        //再删除情况下不用进行过滤判断
        boolean isChange = false;
        //数据库数据删除的操作
        for (Data data : list) {
            if (dataList.remove(data)) {
                isChange = true;
            }
        }
        //有数据变更，则进行界面刷新
        if (isChange) {
            notifyDataChange();
        }
    }

    //DbFlow框架通知的回调
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        //添加自己当前的缓冲区
        if (callback != null) {
            callback.onDataLoaded(tResult);
        }

        //回到数据集保存更新的操作中
        if (tResult.size() == 0) {
            dataList.clear();
            notifyDataChange();
            return;
        }

        Data[] datas = CollectionUtil.toArray(tResult, dataClass);
        onDataSave(datas);
    }

    //插入或修改
    protected void insertOrUpdate(Data data) {
        //判断是插入还是删除
        int index = indexOf(data);
        if (index >= 0) {
            replace(index, data);
        } else {
            insert(data);
        }
    }

    /**
     * 检查一个data是否是我关注的数据 过滤器的作用
     * @return True 是我关注的数据
     */
    protected abstract boolean isRequired(Data data);

    protected void registerDbChangedListener(){
        //添加数据库的监听操作
        DbHelper.addChangedListener(dataClass,this);
    }

    //更新某个坐标下的数据
    protected void replace(int index, Data data) {
        dataList.remove(index);
        dataList.add(index, data);
    }

    // 插入
    protected void insert(Data data) {
        dataList.add(data);
    }

    //查询一个数据是否在当前的数据中
    protected int indexOf(Data newData) {
        int index = -1;
        for (Data data : dataList) {
            index++;
            if (data.isSame(newData)) {
                return index;
            }
        }
        return -1;
    }

    //通知界面刷新
    private void notifyDataChange() {
        SuccessedCallback<List<Data>> callback = this.callback;
        if (callback != null) {
            callback.onDataLoaded(dataList);
        }
    }

}
