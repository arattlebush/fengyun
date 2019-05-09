package fengyun.android.com.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.fengyun.italker.factory.data.DataSource;

import java.io.IOException;
import java.util.List;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.R;
import fengyun.android.com.factory.model.api.RspModel;
import fengyun.android.com.factory.model.api.group.GroupCreateModel;
import fengyun.android.com.factory.model.api.group.GroupMemberAddModel;
import fengyun.android.com.factory.model.card.GroupCard;
import fengyun.android.com.factory.model.card.GroupMemberCard;
import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.model.db.GroupMember;
import fengyun.android.com.factory.model.db.GroupMember_Table;
import fengyun.android.com.factory.model.db.Group_Table;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.model.db.User_Table;
import fengyun.android.com.factory.model.db.view.MemberUserModel;
import fengyun.android.com.factory.net.Network;
import fengyun.android.com.factory.net.RemoteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 对群的操作
 *
 * @author fengyun
 */

public class GroupHelper {
    public static Group find(String groupId) {
        Group group = findFromLocal(groupId);
        if (group == null) {
            group = findFromNet(groupId);
        }
        return group;
    }

    //从本地找群
    public static Group findFromLocal(String groupId) {
        return SQLite.select()
                .from(Group.class)
                .where(Group_Table.id.eq(groupId))
                .querySingle();
    }

    //从网络去找群
    public static Group findFromNet(String groupId) {
        RemoteService service = Network.remote();
        try {
            Response<RspModel<GroupCard>> response = service.groupFind(groupId).execute();
            GroupCard card = response.body().getResult();
            if (card != null) {
                //唤起进行保存的操作
                Factory.getGroupCenter().dispatch(card);
                User user = UserHelper.search(card.getOwnerId());
                if (user != null) {
                    return card.build(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //进行群的创建
    public static void create(GroupCreateModel model, final DataSource.Callback<GroupCard> callback) {
        RemoteService service = Network.remote();
        service.createGroup(model).enqueue(new Callback<RspModel<GroupCard>>() {
            @Override
            public void onResponse(Call<RspModel<GroupCard>> call, Response<RspModel<GroupCard>> response) {
                RspModel<GroupCard> rspModel = response.body();
                if (rspModel.success()) {
                    GroupCard groupCard = rspModel.getResult();
                    //唤起进行保存的操作
                    Factory.getGroupCenter().dispatch(groupCard);
                    callback.onDataLoaded(groupCard);
                } else {
                    //错误的时候返回错误
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<GroupCard>> call, Throwable t) {
                //网络请求失败
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    public static Call search(String content, final DataSource.Callback<List<GroupCard>> callback) {

        //搜索的方法
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel<List<GroupCard>>> call = service.groupSearch(content);
        call.enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    //返回数据
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    //错误的时候返回错误
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        //吧当前的调度者返回回去
        return call;
    }

    //刷新群列表数据
    public static void refreshGroups() {
        RemoteService service = Network.remote();
        service.groups("").enqueue(new Callback<RspModel<List<GroupCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupCard>>> call, Response<RspModel<List<GroupCard>>> response) {
                RspModel<List<GroupCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupCard> groupCards = rspModel.getResult();
                    if (groupCards != null && groupCards.size() > 0) {
                        //进行调度
                        Factory.getGroupCenter().dispatch(groupCards.toArray(new GroupCard[0]));
                    }
                } else {
                    //错误的时候返回错误
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupCard>>> call, Throwable t) {
                //失败不做任何事情
            }
        });
    }

    //获取一个群的成员数量
    public static long getMemberCount(String id) {
        //查询数量
        return SQLite.selectCountOf()
                .from(GroupMember.class)
                .where(GroupMember_Table.group_id.eq(id))
                .count();
    }

    //从网络去刷新一个群的信息
    public static void refreshGroupMember(Group group) {
        RemoteService service = Network.remote();
        service.groupMembers(group.getId()).enqueue(new Callback<RspModel<List<GroupMemberCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<GroupMemberCard>>> call, Response<RspModel<List<GroupMemberCard>>> response) {
                RspModel<List<GroupMemberCard>> rspModel = response.body();
                if (rspModel.success()) {
                    List<GroupMemberCard> memberCards = rspModel.getResult();
                    if (memberCards != null && memberCards.size() > 0) {
                        //进行调度
                        Factory.getGroupCenter().dispatch(memberCards.toArray(new GroupMemberCard[0]));
                    }
                } else {
                    //错误的时候返回错误
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<GroupMemberCard>>> call, Throwable t) {
                //失败不做任何事情
            }
        });

    }

    //关联查询一个用户和群成员的表，返回一个MemberUserHolder表的集合
    public static List<MemberUserModel> getMemberUsers(String groupId, int size) {
        return SQLite.select(GroupMember_Table.alias.withTable().as("alias")
                , User_Table.id.withTable().as("userId")
                , User_Table.name.withTable().as("name")
                , User_Table.portrait.withTable().as("portrait"))
                .from(GroupMember.class)
                .join(User.class, Join.JoinType.INNER)
                .on(GroupMember_Table.user_id.withTable().eq(User_Table.id.withTable()))
                .where(GroupMember_Table.group_id.withTable().eq(groupId))
                .orderBy(GroupMember_Table.user_id, true)
                .limit(size)
                .queryCustomList(MemberUserModel.class);
    }

    // 网络请求进行成员添加
    public static void addMembers(String groupId, GroupMemberAddModel model, final DataSource.Callback<List<GroupMemberCard>> callback) {
        RemoteService service = Network.remote();
        service.groupMemberAdd(groupId, model)
                .enqueue(new Callback<RspModel<List<GroupMemberCard>>>() {
                    @Override
                    public void onResponse(Call<RspModel<List<GroupMemberCard>>> call, Response<RspModel<List<GroupMemberCard>>> response) {
                        RspModel<List<GroupMemberCard>> rspModel = response.body();
                        if (rspModel.success()) {
                            List<GroupMemberCard> memberCards = rspModel.getResult();
                            if (memberCards != null && memberCards.size() > 0) {
                                // 进行调度显示
                                Factory.getGroupCenter().dispatch(memberCards.toArray(new GroupMemberCard[0]));
                                callback.onDataLoaded(memberCards);
                            }
                        } else {
                            Factory.decodeRspCode(rspModel, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<List<GroupMemberCard>>> call, Throwable t) {
                        callback.onDataNotAvailable(R.string.data_network_error);
                    }
                });
    }
}
