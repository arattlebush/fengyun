package fengyun.android.com.factory.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.model.api.account.AccountRspModel;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.model.db.User_Table;

/**
 * @author fengyun
 *
 */
public class Account {

    private static final String KEY_PUSH_ID = "KEY_PUSH_ID";
    private static final String KEY_IS_BIND = "KEY_IS_BIND";
    private static final String KEY_TOKEN = "KEY_TOKEN";
    private static final String KEY_USER_ID = "KEY_USER_ID";
    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    //设备推送ID
    private static String pushId;
    //设备id是否绑定到了服务器
    private static boolean isBind;
    //登录的token,用来接口请求
    private static String token;
    //登录的userId
    private static String userId;
    //登录的account
    private static String account;
    /**
     * 存储数据到xml文件，持久化
     */
    private static void save(Context context){
        //获取数据持久化的SP
        SharedPreferences sp = context.getSharedPreferences(
                Account.class.getName(),Context.MODE_PRIVATE);
        //存储数据
        sp.edit().putString(KEY_PUSH_ID,pushId)
                .putBoolean(KEY_IS_BIND,isBind)
                .putString(KEY_TOKEN,token)
                .putString(KEY_USER_ID,userId)
                .putString(KEY_ACCOUNT,account)
        .apply();
    }

    /**
     * 进行数据加载
     * @param context 上下文
     */
    public static void load(Context context){
        SharedPreferences sp = context.getSharedPreferences(
                Account.class.getName(),Context.MODE_PRIVATE);
        pushId = sp.getString(KEY_PUSH_ID,"");
        isBind = sp.getBoolean(KEY_IS_BIND,false);
        token = sp.getString(KEY_TOKEN,"");
        userId = sp.getString(KEY_USER_ID,"");
        account = sp.getString(KEY_ACCOUNT,"");
    }

    /**
     * 获取pushID
     * @return pushId
     */
    public static String getPushId() {
        return pushId;
    }

    /**
     * 设置并存储设备的id
     * @param pushId 设备的推送ID
     */
    public static void setPushId(String pushId) {
        Account.pushId = pushId;
        Account.save(Factory.app());
    }

    /**
     * 返回当前账户是否登录
     * @return True 已登录
     */
    public static boolean isLogin(){
       //用户id和token不为空
        return !TextUtils.isEmpty(userId)
                && !TextUtils.isEmpty(token);
    }

    /**
     * 是否已经完善了用户信息
     * @return True 完成了
     */
    public static boolean isComplete(){
        //首先保证登录成功
        if(isLogin()){
            User self = getUser();
            return !TextUtils.isEmpty(self.getDesc())
                    && !TextUtils.isEmpty(self.getPortrait())
                    && self.getSex()!=0;
        }
        //未登录返回信息不完全
        return false;
    }
    /**
     * 是否已经绑定到了服务器
     * @return True 绑定了
     */
    public static boolean isBind() {
        return isBind;
    }

    /**
     * 设置绑定状态
     * @param isBind
     */
    public static void setBind(boolean isBind){
        Account.isBind= isBind;
        Account.save(Factory.app());
    }

    /**
     * 保存我自己的信息到持久化的xml中
     * @param model
     */
    public static void login(AccountRspModel model){
        //存储用户的token ,用户的id，方便从数据库查询到我的id
        Account.token = model.getToken();
        Account.account =model.getAccount();
        Account.userId = model.getUser().getId();
        save(Factory.app());
    }
    /**
     * 获取当前登录的信息
     * @return
     */
    public static User getUser(){
        //如果为null返回一个新的user，其次从数据库查询
        return TextUtils.isEmpty(userId)?new User():SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(userId))
                .querySingle();
    }

    /**
     * 返回用户id
     * @return
     */
    public static String getUserId(){
       return getUser().getId();
    }
    /**
     * 获取token
     * @return token
     */
    public static String getToken() {
        return token;
    }
}
