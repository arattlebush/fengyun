package fengyun.android.com.factory.model.db.view;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

import net.fengyun.italker.factory.model.Author;

import fengyun.android.com.factory.model.db.AppDataBase;

/**
 * 用户基础信息的Model,可以和数据库进行查询
 * @author fengyun
 */
@QueryModel(database = AppDataBase.class)
public class UserSampleModel implements Author{

    @Column
    public String id;
    @Column
    public String name;
    @Column
    public String portrait;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPortrait() {
        return portrait;
    }

    @Override
    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
