package fengyun.android.com.factory.model.db;

import com.raizlabs.android.dbflow.structure.BaseModel;

import fengyun.android.com.factory.utils.DiffUtilDataCallback;


/**
 * 我们APP中的基础的一个BaseDbModel，
 * 基础了数据库框架DbFlow中的基础类
 * 同时定义类我们需要的方法
 *
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public abstract class BaseDbModel<Model> extends BaseModel
        implements DiffUtilDataCallback.UiDataDiffer<Model> {
}
