package fengyun.android.com.factory.net;


import java.util.List;

import fengyun.android.com.factory.model.api.RspModel;
import fengyun.android.com.factory.model.api.account.AccountRspModel;
import fengyun.android.com.factory.model.api.account.LoginModel;
import fengyun.android.com.factory.model.api.account.RegisterModel;
import fengyun.android.com.factory.model.api.friend.CommentModel;
import fengyun.android.com.factory.model.api.friend.ReleaseFriendCircleModel;
import fengyun.android.com.factory.model.api.group.GroupCreateModel;
import fengyun.android.com.factory.model.api.group.GroupMemberAddModel;
import fengyun.android.com.factory.model.api.message.MsgCreateModel;
import fengyun.android.com.factory.model.api.user.UserUpdateModel;
import fengyun.android.com.factory.model.card.FriendCircleCard;
import fengyun.android.com.factory.model.card.GroupCard;
import fengyun.android.com.factory.model.card.GroupMemberCard;
import fengyun.android.com.factory.model.card.MessageCard;
import fengyun.android.com.factory.model.card.UserCard;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * @author fengyun
 *         网络请求的所有接口
 */
public interface RemoteService {

    /**
     * 注册接口
     * @param model RegisterModel
     * @return AccountRspModel
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 登录接口
     * @param model RegisterModel
     * @return AccountRspModel
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 绑定ID接口
     * @param  pushId
     * @return AccountRspModel
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(encoded = true,
            value = "pushId") String pushId);


    /**
     * 用户信息上传
     */
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    /**
     * 用户搜索获取数据
     */
    @GET("user/search/{name}")
    Call<RspModel<List<UserCard>>> userSearch(@Path("name") String name);


    @PUT("user/follow/{followId}")
    Call<RspModel<UserCard>> follow(@Path("followId") String followId);

    /**
     * 获取用户联系人
     */
    @GET("user/contact")
    Call<RspModel<List<UserCard>>> userContacts();

    //查询某人的信息
    @GET("user/{userId}")
    Call<RspModel<UserCard>> userFind(@Path("userId")String userId);

    //发送消息的接口
    @POST("msg")
    Call<RspModel<MessageCard>> msgPush(@Body MsgCreateModel model);

    //创建群
    @POST("group")
    Call<RspModel<GroupCard>> createGroup(@Body GroupCreateModel model);

    //创建群
    @GET("group/{groupId}")
    Call<RspModel<GroupCard>> groupFind(@Path("groupId") String groupId);

    //搜索群获取数据
    @GET("group/search/{name}")
    Call<RspModel<List<GroupCard>>> groupSearch(@Path(value = "name",encoded = true) String name);

    //我的群列表
    @GET("group/list/{date}")
    Call<RspModel<List<GroupCard>>> groups(@Path(value = "date",encoded = true) String date);

    //我的群的成员列表
    @GET("group/{groupId}/members")
    Call<RspModel<List<GroupMemberCard>>> groupMembers(@Path(value = "groupId") String groupId);

    //给群添加成员
    @POST("group/{groupId}/member")
    Call<RspModel<List<GroupMemberCard>>> groupMemberAdd(@Path(value = "groupId") String groupId,
                                                         @Body GroupMemberAddModel model);

    //获取朋友圈的列表信息
    @GET("friend/list")
    Call<RspModel<List<FriendCircleCard>>> friendCircle();

    //发布朋友圈
    @POST("friend")
    Call<RspModel<FriendCircleCard>> release(@Body ReleaseFriendCircleModel model);


    //点赞
    @POST("friend/fabulous/{friendCircleId}")
    Call<RspModel> fabulous(@Path(value = "friendCircleId") String friendCircleId);

    //评论
    @POST("friend/comment")
    Call<RspModel> comment(@Body CommentModel model);
}
