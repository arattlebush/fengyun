package net.fengyun.web.italker.push.bean.api.account;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;

/**
 * @author fengyun 微信 15279149227
 * 注册的 model
 */
public class RegisterModel {
    //不加这个布局就没有数据 这个是请求的字段名
    @Expose
    private String account;
    @Expose
    private String password;
    @Expose
    private String name;
    @Expose
    private String pushId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    //校检 判断参数是不是传错了
    public static boolean check(RegisterModel model){
        return model!=null
                && !Strings.isNullOrEmpty(model.account)
                && !Strings.isNullOrEmpty(model.name)
                && !Strings.isNullOrEmpty(model.password)
               ;
    }
}


