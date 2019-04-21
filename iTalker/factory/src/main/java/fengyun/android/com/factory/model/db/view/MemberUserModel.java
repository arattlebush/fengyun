package fengyun.android.com.factory.model.db.view;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

import fengyun.android.com.factory.model.db.AppDataBase;

/**
 * 群成员对应的用户的简单信息表
 */
@QueryModel(database = AppDataBase.class)
public class MemberUserModel {
    @Column
    public String userId;//User -userId/Member-userId
    @Column
    public String name;//User --name
    @Column
    public String alias;//Member--alias
    @Column
    public String portrait;//User--portrait

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }
}
