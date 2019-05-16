package fengyun.android.com.factory.data.helper;

import android.text.TextUtils;

import net.fengyun.italker.factory.data.DataSource;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.R;
import fengyun.android.com.factory.model.api.RspModel;
import fengyun.android.com.factory.model.api.account.AccountRspModel;
import fengyun.android.com.factory.model.api.account.LoginModel;
import fengyun.android.com.factory.model.api.account.RegisterModel;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.net.Network;
import fengyun.android.com.factory.net.RemoteService;
import fengyun.android.com.factory.persistence.Account;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author fengyun
 *         账户的接口
 */
public class AccountHelper {

    /**
     * 注册的调用
     *
     * @param model    传递一个一个注册的model
     * @param callback 成功和失败的回送
     */
    public static void register(final RegisterModel model, final DataSource.Callback<User> callback) {
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 登录的调用
     *
     * @param model    传递一个一个登录的model
     * @param callback 成功和失败的回送
     */
    public static void login(final LoginModel model, final DataSource.Callback<User> callback) {
        //调用Retrofit对我们的网络请求接口做代理
        RemoteService service = Network.remote();
        //得到一个Call
        Call<RspModel<AccountRspModel>> call = service.accountLogin(model);
        //异步请求
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 对设备ID进行绑定的操作
     *
     * @param callback Callback
     */
    public static void bindPush(final DataSource.Callback<User> callback) {
        String pushId = Account.getPushId();
        if (!TextUtils.isEmpty(pushId)) {
            return;
        }
        RemoteService service = Network.remote();
        Call<RspModel<AccountRspModel>> call = service.accountBind(pushId);
        call.enqueue(new AccountRspCallback(callback));
    }

    /**
     * 请求的回调封装
     */
    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>> {
        final DataSource.Callback<User> callback;

        public AccountRspCallback(DataSource.Callback<User> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
            //网络请求成功返回
            //从返回中得到我们的全局Model 内部是使用的Gson进行解析
            RspModel<AccountRspModel> rspModel = response.body();
            if (rspModel.success()) {
                AccountRspModel accountRspModel = rspModel.getResult();
                User user = accountRspModel.getUser();
                //保存
                DbHelper.save(User.class, user);
                //同步到XML持久化状态
                Account.login(accountRspModel);
                if (accountRspModel.isBind()) {
                    //设置绑定状态为True
                    Account.setBind(true);
                    //然后返回
                    if (callback != null) {
                        callback.onDataLoaded(user);
                    }
                } else {
                    //进行绑定的唤起
                    bindPush(callback);
                }
            } else {
                //对返回responce中的失败的code进行解析，解析到对应的String资源上面
                Factory.decodeRspCode(rspModel, callback);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            //网络请求失败
            if (callback != null) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }

        }
    }

}
