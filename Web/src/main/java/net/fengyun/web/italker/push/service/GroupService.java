package net.fengyun.web.italker.push.service;

import com.google.common.base.Strings;
import net.fengyun.web.italker.push.bean.api.base.ResponseModel;
import net.fengyun.web.italker.push.bean.api.group.GroupCreateModel;
import net.fengyun.web.italker.push.bean.api.group.GroupMemberAddModel;
import net.fengyun.web.italker.push.bean.api.group.GroupMemberUpdateModel;
import net.fengyun.web.italker.push.bean.card.ApplyCard;
import net.fengyun.web.italker.push.bean.card.GroupCard;
import net.fengyun.web.italker.push.bean.card.GroupMemberCard;
import net.fengyun.web.italker.push.bean.db.Group;
import net.fengyun.web.italker.push.bean.db.GroupMember;
import net.fengyun.web.italker.push.bean.db.User;
import net.fengyun.web.italker.push.factory.GroupFactory;
import net.fengyun.web.italker.push.factory.PushFactory;
import net.fengyun.web.italker.push.factory.UserFactory;
import net.fengyun.web.italker.push.provider.LocalDateTimeConverter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 群组接口的入口
 *
 * @author fengyun
 */
@Path("/group")
public class GroupService extends BaseService {


    /**
     * 创建一个群
     * @param model 基本参数
     * @return 群信息
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> create(GroupCreateModel model) {
        if (!GroupCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }
        //创建者
        User creator = getSelf();
        //创建者并不在列表中
        model.getUsers().remove(creator.getId());
        if (model.getUsers().size() == 0)
            return ResponseModel.buildParameterError();


        if (GroupFactory.findByName(model.getName()) != null) {
            return ResponseModel.buildHaveNameError();
        }
        List<User> users = new ArrayList<>();
        for (String s : model.getUsers()) {
            User user = UserFactory.findById(s);
            if (user == null) {
                continue;
            }
            users.add(user);
        }
        //没有一个成员
        if (users.size() == 0) {
            return ResponseModel.buildParameterError();
        }
        Group group = GroupFactory.create(creator, model, users);
        if (group == null) {
            return ResponseModel.buildServiceError();
        }
        //拿管理员的信息
        GroupMember creatorMember = GroupFactory.getMember(creator.getId(), group.getId());
        if (creatorMember == null)
            return ResponseModel.buildServiceError();
        //拿到群的成员，给所有的群成员发送信息，已经被添加到群的信息

        Set<GroupMember> members = GroupFactory.getMember(group);
        if (members == null)
            return ResponseModel.buildServiceError();
        members = members.stream().filter(groupMember ->
                //过滤后自己
                groupMember.getId().equalsIgnoreCase(creatorMember.getId()))
                .collect(Collectors.toSet());

        //开始发起推送
        PushFactory.pushJoinGroup(members);
        //返回到客户端创建的群信息
        return ResponseModel.buildOk(new GroupCard(creatorMember));
    }

    /**
     * 查询群 模糊查询 没有传递参数就是搜索最近所有的群
     * @param name 模糊查询的字段
     * @return 群信息列表
     */
    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> search(@PathParam("name") @DefaultValue("") String name) {
        User self = getSelf();
        List<Group> groups = GroupFactory.search(name);
        if (groups != null && groups.size() > 0) {
            List<GroupCard> groupCards = groups.stream().map(group -> {
                GroupMember member = GroupFactory.getMember(self.getId(), group.getId());
                return new GroupCard(group, member);
            }).collect(Collectors.toList());
            return ResponseModel.buildOk(groupCards);
        }
        return ResponseModel.buildOk();
    }

    /**
     * 获取自己的群列表
     *
     * @param date 时间字段，不传递，则返回全部当前的群列表，有时间则返回这个时间之后的加入的群
     * @return 群信息列表
     */
    @GET
    @Path("/list/{date:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> list(@DefaultValue("") @PathParam("date") String date) {
        User user = getSelf();
        LocalDateTime dateTime = null;
        if (!Strings.isNullOrEmpty(date)) {
            try {
                dateTime = LocalDateTime.parse(date, LocalDateTimeConverter.FORMATTER);
            } catch (Exception e) {
                dateTime = null;
                e.printStackTrace();
            }
        }

        Set<GroupMember> members = GroupFactory.getMember(user);
        if (members == null && members.size() == 0)
            return ResponseModel.buildOk();

        final LocalDateTime finalDateTime = dateTime;
        List<GroupCard> groupCards = members.stream()
                .filter(groupMember -> finalDateTime == null //时间如果为null，则不做限制
                        || groupMember.getUpdateAt().isAfter(finalDateTime))//时间不为null，你需要在我这个时间之后
                .map(GroupCard::new)//转换操作
                .collect(Collectors.toList());
        return ResponseModel.buildOk(groupCards);
    }


    /**
     * 获取一个群的信息
     * 你必须是这个群的成员
     *
     * @param id 群的id
     * @return
     */
    @GET
    @Path("/{groupId}")//api/group/groupId
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> getGroup(@PathParam("groupId") String id) {
        if (Strings.isNullOrEmpty(id))
            return ResponseModel.buildParameterError();
        User self = getSelf();
        GroupMember groupMember = GroupFactory.getMember(self.getId(), id);
        if (groupMember == null) {
            return ResponseModel.buildNotFoundGroupError(null);
        }
        return ResponseModel.buildOk(new GroupCard(groupMember));
    }


    /**
     * 获取群里的所有成员,你必须是群成员
     *
     * @param groupId 群的id
     * @return 群信息列表
     */
    @GET
    @Path("/{groupId}/members")//api/group/groupId/member
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> members(@PathParam("groupId") String groupId) {

        User self = getSelf();

        Group group = GroupFactory.findById(groupId);
        if (group == null) {
            return ResponseModel.buildNotFoundGroupError(null);
        }
        GroupMember selfMember = GroupFactory.getMember(self.getId(), groupId);
        if (selfMember == null)
            return ResponseModel.buildParameterError();
        //所有的成员
        Set<GroupMember> members = GroupFactory.getMember(group);
        if (members == null)
            return ResponseModel.buildServiceError();
        //返回
        List<GroupMemberCard> memberCards = members
                .stream()
                .map(GroupMemberCard::new)//转换操作
                .collect(Collectors.toList());

        return ResponseModel.buildOk(memberCards);
    }

    /**
     * 添加一个群成员
     *
     * @param groupId 群id，你必须是这个群的管理员之一
     * @param model   添加成员的model
     * @return 添加的成员列表
     */
    @POST
    @Path("/{groupId}/member")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> memberAdd(
            @PathParam("groupId") String groupId, GroupMemberAddModel model) {
        if(Strings.isNullOrEmpty(groupId) || !GroupMemberAddModel.check(model))
            return ResponseModel.buildParameterError();

        User self = getSelf();

        //移除我之后再进行判断
        model.getUsers().remove(self);
        if (model.getUsers().size()==0) {
            return ResponseModel.buildParameterError();
        }

        //没有这个群
        Group group = GroupFactory.findById(groupId);
        if (group == null)
            return ResponseModel.buildNotFoundGroupError(null);

        //自己必须是成员，同时是管理员级别
        GroupMember selfMember = GroupFactory.getMember(self.getId(),groupId);
        if (selfMember == null || selfMember.getPermissionType() == GroupMember.PERMISSION_TYPE_NONE)
            return ResponseModel.buildNoPermissionError();

        //已有的成员
        Set<GroupMember> oldMembers = GroupFactory.getMember(group);

        Set<String> oldMemberUserIds = oldMembers.stream()
                .map(GroupMember::getUserId).collect(Collectors.toSet());

        List<User> insertUsers = new ArrayList<>();
        for (String s : model.getUsers()) {
            //找人
            User user = UserFactory.findById(s);
            if (user == null)
                continue;
            //如果群里面有这个群成员
            if(oldMemberUserIds.contains(user.getId()))
                continue;

            insertUsers.add(user);
        }
        //没有一个新增的成员
        if(insertUsers.size()==0)
            return ResponseModel.buildParameterError();

        //进行添加操作
        Set<GroupMember> insertMembers =  GroupFactory.addMember(group,insertUsers);
        if (insertMembers == null)
            return ResponseModel.buildServiceError();

        //转换成GroupMemberCard
        List<GroupMemberCard> insertCards = insertMembers
                .stream().map(GroupMemberCard::new)
                .collect(Collectors.toList());

        //通知，两部曲

        //1.通知新增的成员，你被加入了XXX群  //开始发起推送
        PushFactory.pushJoinGroup(insertMembers);

        //2.通知群中老的成员，有XXX,XXX加入群
        PushFactory.pushGroupMemberAdd(oldMembers,insertCards);

        return ResponseModel.buildOk(insertCards);
    }


    /**
     * 修改一个成员的信息，请求的人要么是管理员，要么是本人
     *
     * @param memberId 成员id，可以查询对应的群，和人
     * @param model    修改的model
     * @return 当前成员的信息
     */
    @POST
    @Path("/member/{memberId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupMemberCard> modifyMember(@PathParam("memberId") String memberId, GroupMemberUpdateModel model) {
        return null;
    }

    /**
     * 申请加入某一个群，此时会创建一个加入的申请，并写入表，然后会给管理员发送消息
     * 管理员同意，其实就是调用添加成员的接口把对应的用户添加进去
     *
     * @param groupId 群id
     * @return 申请的信息
     */
    @POST
    @Path("/applyJoin/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<ApplyCard> join(@PathParam("groupId") String groupId) {
        return null;
    }


}
