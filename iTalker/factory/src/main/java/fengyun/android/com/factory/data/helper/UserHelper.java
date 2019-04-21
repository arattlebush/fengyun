package fengyun.android.com.factory.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.fengyun.italker.factory.data.DataSource;
import net.fengyun.italker.utils.CollectionUtil;

import java.io.IOException;
import java.util.List;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.R;
import fengyun.android.com.factory.model.api.RspModel;
import fengyun.android.com.factory.model.api.user.UserUpdateModel;
import fengyun.android.com.factory.model.card.UserCard;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.model.db.User_Table;
import fengyun.android.com.factory.model.db.view.UserSampleModel;
import fengyun.android.com.factory.net.Network;
import fengyun.android.com.factory.net.RemoteService;
import fengyun.android.com.factory.persistence.Account;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.raizlabs.android.dbflow.sql.language.SQLite.select;

/**
 * @author fengyun
 */

public class UserHelper {

    //用户更新的操作，异步的
    public static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel<UserCard>> call = service.userUpdate(model);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    //唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);
                } else {
                    //错误的时候返回错误
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                //网络请求失败
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }


    //搜索的方法
    public static Call search(String name, final DataSource.Callback<List<UserCard>> callback) {
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel<List<UserCard>>> call = service.userSearch(name);
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    //返回数据
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    //错误的时候返回错误
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        //吧当前的调度者返回回去
        return call;
    }

    //关注的方法
    public static void follow(String id, final DataSource.Callback<UserCard> callback) {
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel<UserCard>> call = service.follow(id);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    //唤起进行保存的操作
                    Factory.getUserCenter().dispatch(userCard);
                    //返回数据
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    //错误的时候返回错误
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        //吧当前的调度者返回回去
    }


    //刷新联系人，不需要callback，直接存储到数据库，并通过数据库观察这进行界面更新
    //界面更新的时候进行对比，然后差异更新
    public static void refreshContacts() {
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        service.userContacts()
                .enqueue(new Callback<RspModel<List<UserCard>>>() {
                    @Override
                    public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                        RspModel<List<UserCard>> rspModel = response.body();
                        if (rspModel.success()) {
                            //拿到集合
                            List<UserCard> cards = rspModel.getResult();
                            if (cards == null || cards.size() == 0) {
                                return;
                            }
                            Factory.getUserCenter().dispatch(CollectionUtil.toArray(cards, UserCard.class));
                        } else {
                            //错误的时候返回错误
                            Factory.decodeRspCode(rspModel, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                    }
                });
    }

    //从本地查询一个用户
    public static User findFromLocal(String id) {
        return select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    //从数据库查询一个用户
    public static User findFromNet(String id) {
        RemoteService service = Network.remote();
        try {
            Response<RspModel<UserCard>> response = service.userFind(id).execute();
            UserCard card = response.body().getResult();
            if (card != null) {
                User user = card.build();
                //唤起进行保存的操作
                Factory.getUserCenter().dispatch(card);
                return user;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 搜索一个用户，优先本地缓存
     * 没有然后再从网络拉取
     *
     * @param id
     * @return
     */
    public static User search(String id) {
        User user = findFromLocal(id);
        if (user == null) {
            return findFromNet(id);
        }
        return user;
    }

    /**
     * 搜索一个用户，优先网络查询
     * 没有然后再从网络拉取
     *
     * @param id
     * @return
     */
    public static User searchFirstOfNet(String id) {
        User user = findFromNet(id);
        if (user == null) {
            return findFromLocal(id);
        }
        return user;
    }

    /**
     * 获取联系人
     */
    public static List<User> getContact() {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))//已经关注的人
                .and(User_Table.id.notEq(Account.getUserId()))//id不等于自己
                .orderBy(User_Table.name, true)//排序
                .limit(100)//最多一百条
                .queryList();//同步操作
    }


    /**
     * 获取联系人列表
     * 但是是一个简单的数据的
     */
    public static List<UserSampleModel> getSampleContact() {
        //select id="??";
        //select User_id = "??";

        return SQLite.select(User_Table.id.withTable().as("id"),
                User_Table.name.withTable().as("name"),
                User_Table.portrait.withTable().as("portrait"))
                .from(User.class)
                .where(User_Table.isFollow.eq(true))//已经关注的人
                .and(User_Table.id.notEq(Account.getUserId()))//id不等于自己
                .orderBy(User_Table.name, true)//排序
                .limit(100)//最多一百条
                .queryCustomList(UserSampleModel.class);//同步操作
    }

}
