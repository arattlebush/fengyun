package fengyun.android.com.factory.model.api.account;


import fengyun.android.com.factory.model.db.User;

/**
 * 账户的返回model
 * @author fengyun
 */
public class AccountRspModel {

    //用户基本信息
    private User user;
    //当前登录的账号
    private String account;
    //当前登录成功后获取的Token
    //可以用个Token获取用户的所有信息
    private String token;
    //标示是否已经绑定到设备PushId
    private boolean isBind;

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AccountRspModel{" +
                "user=" + user +
                ", account='" + account + '\'' +
                ", token='" + token + '\'' +
                ", isBind=" + isBind +
                '}';
    }
}
