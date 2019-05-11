package net.fengyun.web.italker.push.factory;

import com.google.common.base.Strings;
import net.fengyun.web.italker.push.bean.api.group.GroupCreateModel;
import net.fengyun.web.italker.push.bean.db.Group;
import net.fengyun.web.italker.push.bean.db.GroupMember;
import net.fengyun.web.italker.push.bean.db.User;
import net.fengyun.web.italker.push.utils.Hib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 群数据库处理
 *
 * @author fengyun
 */
public class GroupFactory {


    //通过群id找到这个群
    public static Group findById(String groupId) {
        return Hib.query(session -> session.get(Group.class, groupId));
    }

    //查询一个群，通过名字
    public static Group findByName(String name) {
        return Hib.query(session -> (Group) session.createQuery("from Group where lower(name) =:name ")
                .setParameter("name", name.toLowerCase())
                .uniqueResult());
    }

    //通过群找到群成员
    public static Set<GroupMember> getMember(Group group) {
        return Hib.query(session -> {
            @SuppressWarnings("unchecked")
            List<GroupMember> members = session.createQuery("from GroupMember where group=:group")
                    .setParameter("group", group)
                    .list();
            return new HashSet<>(members);
        });
    }

    //获取一个人加入的所有群
    public static Set<GroupMember> getMember(User user) {
        return Hib.query(session -> {
            @SuppressWarnings("unchecked")
            List<GroupMember> members = session.createQuery("from GroupMember where userId=:userId")
                    .setParameter("userId", user.getId())
                    .list();
            return new HashSet<>(members);
        });
    }

    //查询一个群，同事这个人是群的成员
    public static Group findById(User sender, String groupId) {
        GroupMember member = getMember(sender.getId(), groupId);
        if (member != null)
            return member.getGroup();
        return null;
    }

    //创建一个群，
    public static Group create(User creator, GroupCreateModel model, List<User> users) {
        return Hib.query(session -> {
            Group group = new Group(creator, model);
            session.save(group);
            GroupMember ownerMember = new GroupMember(creator, group);
            //设置超级权限，创建者
            ownerMember.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN_SU);
            //保存，并没有提交数据库
            session.save(ownerMember);
            for (User user : users) {
                GroupMember member = new GroupMember(user, group);
                //保存，并没有提交数据库
                session.save(member);
            }
//            session.flush();
//            session.load(group,group.getId());
            return group;
        });
    }

    //获取一个群的群成员
    public static GroupMember getMember(String userId, String groupId) {
        return Hib.query(session -> (GroupMember) session
                .createQuery("from GroupMember where userId=:userId and groupId=:groupId")
                .setParameter("userId", userId)
                .setParameter("groupId", groupId)
                .setMaxResults(1)
                .uniqueResult()
        );
    }

    /**
     * 搜索联系人的实现
     *
     * @param name 查询的名字，允许为空
     * @return 查询到的用户集合，如果name为空，则返回最近的用户
     */
    @SuppressWarnings("unchecked")
    public static List<Group> search(String name) {
        if (Strings.isNullOrEmpty(name))
            name = "";//保证不能为null的情况，减少后面的一下判断和额外的错误
        final String searchName = "%" + name + "%";
        return Hib.query(session ->
                //查询的条件 name忽略大小写，并且使用like（模糊查询）
                //头像和描述必须完善才能查询的到
                (List<Group>) session.createQuery("from Group where lower(name) like :name ")
                        .setParameter("name", "" + searchName + "")
                        .setMaxResults(20)//最多20条数据
                        .list()

        );
    }

    /**
     * 给群添加用户
     *
     * @param group       群
     * @param insertUsers 群成员列表
     * @return
     */
    public static Set<GroupMember> addMember(Group group, List<User> insertUsers) {

        return Hib.query(session -> {
            Set<GroupMember> groupMembers = new HashSet<>();
            for (User user : insertUsers) {
                GroupMember member = new GroupMember(user, group);
                //保存，并没有提交数据库
                session.save(member);
                groupMembers.add(member);
            }

            //进行数据刷新,消耗高，耗性能
//            for (GroupMember groupMember : groupMembers) {
//                session.refresh(groupMember);
//            }
            return groupMembers;
        });

    }
}
