package fengyun.android.com.factory.data.group;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import fengyun.android.com.factory.data.BaseDbRepository;
import fengyun.android.com.factory.data.helper.GroupHelper;
import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.model.db.Group_Table;
import fengyun.android.com.factory.model.db.view.MemberUserModel;

/**
 * @author fengyun
 * 群列表的仓库
 *
 */

public class GroupsDispatcher extends BaseDbRepository<Group> implements GroupsDataSource{


    @Override
    public void load(SuccessedCallback<List<Group>> callback) {
        super.load(callback);
        SQLite.select()
                .from(Group.class)
                .orderBy(Group_Table.name,true)//按名字顺序排序
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Group group) {
        //一个群的信息，只可能两种情况出现在数据库
        //一个是你被别人加入群，第二是你直接建立群
        //无论什么情况，你拿到的度只是群的信息，没有成员的信息
        //你需要进行成员信息初始化操作
        if(group.getGroupMemberCount()>0){
            //已经初始化了的成员信息
            group.holder=buildGroupHolder(group);
        }else{
            //待初始化的群的信息
            group.holder=null;
            GroupHelper.refreshGroupMember(group);
        }

        //是否关注 所有的群我度关注
        return true;
    }

    //初始化界面显示的成员信息
    private String buildGroupHolder(Group group) {
        List<MemberUserModel> userModels = group.getLatelyGroupMembers();
        if(userModels==null || userModels.size()==0)
            return null;
        StringBuilder builder = new StringBuilder();
        for (MemberUserModel userModel : userModels) {
            builder.append(TextUtils.isEmpty(userModel.alias)?userModel.name:userModel.alias);
            builder.append(", ");
        }
        builder.delete(builder.lastIndexOf(", "),builder.length());
        return builder.toString();
    }
}
