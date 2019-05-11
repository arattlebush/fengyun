package net.fengyun.web.italker.push.service;

import com.google.common.base.Strings;
import net.fengyun.web.italker.push.bean.api.account.AccountRepModel;
import net.fengyun.web.italker.push.bean.api.account.LoginModel;
import net.fengyun.web.italker.push.bean.api.account.RegisterModel;
import net.fengyun.web.italker.push.bean.api.base.ResponseModel;
import net.fengyun.web.italker.push.bean.db.User;
import net.fengyun.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author fengyun 微信 15279149227
 */
//127.0.0.1/api/account......
@Path("/account")//设置一个路径
public class AccountService extends BaseService{

    @GET// 请求方式
    @Path("/login")//127.0.0.1/api/account/login
    public String get() {
        return "You get the login";
    }

    @POST //登录
    @Path("/login")//  /api/account/login
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRepModel> login(LoginModel model) {
        //校检一下客户端输入的参数
        if (!LoginModel.check(model)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }

        User user = UserFactory.login(model.getAccount(), model.getPassword());
        if (user != null) {
            //如果有携带pushId
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());
            }

            //登录成功 返回ok
            AccountRepModel accountRepModel = new AccountRepModel(user);
            return ResponseModel.buildOk(accountRepModel);
        } else {
            //登录异常  返回失败
            return ResponseModel.buildLoginError();
        }
    }

    @POST //注册
    @Path("/register")//  /api/account/register
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<AccountRepModel> register(RegisterModel model) {
        if (!RegisterModel.check(model)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        User user = UserFactory.findByPhone(model.getAccount().trim());
        if (user != null) {
            //已有账户
            return ResponseModel.buildHaveAccountError();
        }

        user = UserFactory.findByName(model.getName().trim());
        if (user != null) {
            //已有名字
            return ResponseModel.buildHaveNameError();
        }
        //开始注册逻辑
        user = UserFactory.register(model.getAccount(), model.getPassword(), model.getName());
        if (user != null) {
            //如果有携带pushId
            if (!Strings.isNullOrEmpty(model.getPushId())) {
                return bind(user, model.getPushId());
            }
            //注册成功 返回ok
            AccountRepModel accountRepModel = new AccountRepModel(user);
            return ResponseModel.buildOk(accountRepModel);
        } else {
            //注册异常  返回失败
            return ResponseModel.buildRegisterError();
        }
    }

    @POST //
    @Path("/bind/{pushId}")//  /api/account/bind
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //从请求头中获取token字段
    //pushId从url地址获取
    public ResponseModel<AccountRepModel> bind(@PathParam("pushId") String pushId) {
        if (Strings.isNullOrEmpty(pushId)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        //拿到自己的个人信息
//        User user = UserFactory.findByToken(token);
        return bind(self, pushId);
    }

    /**
     * 绑定的操作
     *
     * @param self   自己
     * @param pushId 设备id
     * @return 自己
     */
    private ResponseModel<AccountRepModel> bind(User self, String pushId) {
        User user = UserFactory.bindPushId(self, pushId);
        if (user != null) {
            //返回当前账户 设置绑定为true
            AccountRepModel accountRepModel = new AccountRepModel(user, true);
            return ResponseModel.buildOk(accountRepModel);
        } else {
            //绑定失败是服务器异常
            return ResponseModel.buildServiceError();
        }
    }


}
