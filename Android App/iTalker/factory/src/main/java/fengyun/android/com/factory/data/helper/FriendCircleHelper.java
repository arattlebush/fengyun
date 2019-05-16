package fengyun.android.com.factory.data.helper;

import net.fengyun.italker.factory.data.DataSource;

import java.util.List;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.R;
import fengyun.android.com.factory.model.api.RspModel;
import fengyun.android.com.factory.model.api.friend.CommentModel;
import fengyun.android.com.factory.model.api.friend.ReleaseFriendCircleModel;
import fengyun.android.com.factory.model.card.FriendCircleCard;
import fengyun.android.com.factory.net.Network;
import fengyun.android.com.factory.net.RemoteService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 对朋友圈的操作类
 *
 * @author fengyun
 */
public class FriendCircleHelper {


    //获取朋友圈的信息
    public static Call friendCircle(final DataSource.Callback<List<FriendCircleCard>> callback) {

        //搜索的方法
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel<List<FriendCircleCard>>> call = service.friendCircle();
        call.enqueue(new Callback<RspModel<List<FriendCircleCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<FriendCircleCard>>> call, Response<RspModel<List<FriendCircleCard>>> response) {
                RspModel<List<FriendCircleCard>> rspModel = response.body();
                if (rspModel.success()) {
                    //返回数据
                    callback.onDataLoaded(rspModel.getResult());
                } else {
                    //错误的时候返回错误
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<FriendCircleCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        //吧当前的调度者返回回去
        return call;
    }


    /**
     * 发布的调用
     *
     * @param model    传递一个一个发布的model
     * @param callback 成功和失败的回送
     */
    public static void release(final ReleaseFriendCircleModel model, final DataSource.Callback<FriendCircleCard> callback) {
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel<FriendCircleCard>> call = service.release(model);
        //异步请求
        call.enqueue(new Callback<RspModel<FriendCircleCard>>() {
            @Override
            public void onResponse(Call<RspModel<FriendCircleCard>> call, Response<RspModel<FriendCircleCard>> response) {
                RspModel<FriendCircleCard> rspModel = response.body();
                if (rspModel.success()) {
                    FriendCircleCard friendCircleCard = rspModel.getResult();
                    //然后返回
                    if (callback != null) {
                        callback.onDataLoaded(friendCircleCard);
                    }
                } else {
                    //对返回responce中的失败的code进行解析，解析到对应的String资源上面
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel<FriendCircleCard>> call, Throwable t) {
                //网络请求失败
                if (callback != null) {
                    callback.onDataNotAvailable(R.string.data_network_error);
                }

            }
        });
    }

    //点赞
    public static void fabulous(String friendCircleId, final DataSource.Callback callback) {
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel> call = service.fabulous(friendCircleId);
        //异步请求
        call.enqueue(new Callback<RspModel>() {
            @Override
            public void onResponse(Call<RspModel> call, Response<RspModel> response) {
                RspModel rspModel = response.body();
                if (rspModel.success()) {
                    //然后返回
                    if (callback != null) {
                        callback.onDataLoaded(null);
                    }
                } else {
                    //对返回responce中的失败的code进行解析，解析到对应的String资源上面
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel> call, Throwable t) {
                //网络请求失败
                if (callback != null) {
                    callback.onDataNotAvailable(R.string.data_network_error);
                }

            }
        });
    }

    //评论
    public static void comment( CommentModel model, final DataSource.Callback callback) {
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel> call = service.comment(model);
        //异步请求
        call.enqueue(new Callback<RspModel>() {
            @Override
            public void onResponse(Call<RspModel> call, Response<RspModel> response) {
                RspModel rspModel = response.body();
                if (rspModel.success()) {
                    //然后返回
                    if (callback != null) {
                        callback.onDataLoaded(null);
                    }
                } else {
                    //对返回responce中的失败的code进行解析，解析到对应的String资源上面
                    Factory.decodeRspCode(rspModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RspModel> call, Throwable t) {
                //网络请求失败
                if (callback != null) {
                    callback.onDataNotAvailable(R.string.data_network_error);
                }
            }
        });
    }
}
