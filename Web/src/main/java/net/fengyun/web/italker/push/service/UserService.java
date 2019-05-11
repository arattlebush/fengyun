package net.fengyun.web.italker.push.service;

import com.google.common.base.Strings;
import net.fengyun.web.italker.push.bean.api.base.PushModel;
import net.fengyun.web.italker.push.bean.api.base.ResponseModel;
import net.fengyun.web.italker.push.bean.api.user.UpdateInfoModel;
import net.fengyun.web.italker.push.bean.card.UserCard;
import net.fengyun.web.italker.push.bean.db.User;
import net.fengyun.web.italker.push.factory.PushFactory;
import net.fengyun.web.italker.push.factory.UserFactory;
import net.fengyun.web.italker.push.utils.PushDispatcher;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息处理的service
 *
 * @author fengyun 微信 15279149227
 */
//127.0.0.1/api/user......
@Path("/user")//设置一个路径
public class UserService {

    //添加一个上下文注解 该注解会自动赋值，具体的值是我们拦截器中的值
    @Context
    private SecurityContext securityContext;

    private User getSelf() {
        return (User) securityContext.getUserPrincipal();
    }

    //    @Path("")//  /api/user 不需要写路径 就是当前路径
    @PUT //用户信息修改接口 返回自己的个人信息
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(
            UpdateInfoModel model) {
        //校检
        if (!UpdateInfoModel.check(model)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();

        //更新用户信息
        self = model.updateToUser(self);
        //修改数据库
        self = UserFactory.update(self);
        UserCard userCard = new UserCard(self, true);
        //返回一个userCard回去
        return ResponseModel.buildOk(userCard);
    }

    //拉取联系人
    @GET
    @Path("/contact")//127.0.0.1/api/user/contact
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        User self = getSelf();

        PushModel pushModel = new PushModel();
        pushModel.add(new PushModel.Entity(0,"hello!!!!!!"));
        PushDispatcher dispatcher = new PushDispatcher();
        dispatcher.add(self,pushModel);
        dispatcher.submit();
        //拿到我的联系人
        List<User> users = UserFactory.contacts(self);
        //转换为userCard集合
        List<UserCard> userCards = users.stream()
                .map(user -> {//map转换操作 User 转换成UserCard
                    return new UserCard(user, true);
                }).collect(Collectors.toList());
        //返回数据
        return ResponseModel.buildOk(userCards);
    }

    //关注人 关注人的操作其实是双方同时关注
    @PUT //修改使用put
    @Path("/follow/{followId}")//127.0.0.1/api/user/follow/用户的id
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId) {
        User self = getSelf();
        //自己不能关注自己
        if (self.getId().equalsIgnoreCase(followId) || Strings.isNullOrEmpty(followId)) {
            return ResponseModel.buildParameterError();
        }
        //找到我要关注的人
        User followUser = UserFactory.findById(followId);
        if (followUser == null) {
            //没找到人
            return ResponseModel.buildNotFoundUserError(null);
        }


        //TODO 备注可以没有，后面可以扩展
        followUser = UserFactory.follow(self, followUser, null);
        if (followUser == null) {
            //关注失败，返回服务器异常
            return ResponseModel.buildServiceError();
        }
        //给他发送一个我的信息
        PushFactory.pushFollow(followUser,new UserCard(self));

        //返回关注人的信息和
        return ResponseModel.buildOk(new UserCard(followUser, true));

    }

    //获取某人的信息
    @GET //修改使用put
    @Path("{id}")//127.0.0.1/api/user/id......
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("id") String id) {
        if (Strings.isNullOrEmpty(id)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();

        if (self.getId().equalsIgnoreCase(id)) {
            return ResponseModel.buildOk(new UserCard(self, true));
        }
        User user = UserFactory.findById(id);
        if (user == null) {
            //返回没找到用户
            return ResponseModel.buildNotFoundUserError(null);
        }
        boolean isFollow = UserFactory.getUserFollow(self, user) != null;
        //如果我们直接有关注的记录，则我已关注了这个人
        return ResponseModel.buildOk(new UserCard(user, isFollow));

    }

    //搜索人的接口
    //为了简化分页，只返回20条数据
    @GET//搜索人不设置数据更改，则为get
    // 127.0.0.1/api/user/search/
    @Path("/search/{name:(.*)?}")//名字为任意字符，可以为空
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name) {
        User self = getSelf();

        //先查询数据
        List<User> searchUsers = UserFactory.search(name);
        //把查询的人封装为UserCard,判断这些人是否有我关注的人
        //如果有，则返回的关注状态
        //拿出我的联系人
        final List<User> contacts = UserFactory.contacts(self);
        List<UserCard> userCards = searchUsers.stream()
                .map(user -> {
                    //判断这个人是否在我的联系人中,或者是我的联系人中的人
                    //进行联系人的任意匹配，匹配其中的id字段
                    boolean isFollow = user.getId().equalsIgnoreCase(self.getId())
                            // 进行联系人的任意匹配，匹配其中的Id字段
                            || contacts.stream().anyMatch(
                            contactUser -> contactUser.getId()
                                    .equalsIgnoreCase(user.getId())
                    );

                    return new UserCard(user, isFollow);
                }).collect(Collectors.toList());
        //返回
        return ResponseModel.buildOk(userCards);
    }

}
