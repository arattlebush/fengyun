package fengyun.android.com.factory.model.api.group;

import java.util.HashSet;
import java.util.Set;

/**
 * 群成员添加的Model
 */

public class GroupMemberAddModel {


    private Set<String> users = new HashSet<>();

    public GroupMemberAddModel(Set<String> users) {
        this.users = users;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
    /**
     * 检测
     * @param model
     * @return
     */
    public static boolean check(GroupMemberAddModel model) {
        return  model!=null && !(model.users==null || model.users.size()==0) ;
    }
}
