package net.fengyun.web.italker.push.bean.api.group;

import com.google.gson.annotations.Expose;

import java.util.HashSet;
import java.util.Set;

/**
 * 添加一个成员
 * @author fengyun
 */
public class GroupMemberAddModel {
    @Expose
    private Set<String> users = new HashSet<>();

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
