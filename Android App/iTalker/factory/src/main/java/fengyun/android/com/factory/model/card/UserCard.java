package fengyun.android.com.factory.model.card;

import net.fengyun.italker.factory.model.Author;

import java.util.Date;

import fengyun.android.com.factory.model.db.User;

/**
 * 用户卡片，用于接收服务器返回
 * @author fengyun
 */

public class UserCard implements Author{

    private String id;
    private String name;
    private String phone;
    private String portrait;
    private String desc;
    private int sex = 0;

    //用户最后的更新时间
    private Date modifyAt;

    //用户的关注的时间
    private int follows;

    //用户的粉丝的数量
    private int following;
    private boolean isFollow;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public Date getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(Date modifyAt) {
        this.modifyAt = modifyAt;
    }

    public int getFollows() {
        return follows;
    }

    public void setFollows(int follows) {
        this.follows = follows;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    //缓存一个对应的user，不能被Gson框架解析使用
    private transient User user;

    public User build() {

        if (user == null) {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setPortrait(portrait);
            user.setPhone(phone);
            user.setDesc(desc);
            user.setSex(sex);
            user.setFollow(isFollow);
            user.setFollowing(following);
            user.setFollows(follows);
            user.setModifyAt(modifyAt);
            this.user = user;
        }
        return user;
    }

    @Override
    public String toString() {
        return "UserCard{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", portrait='" + portrait + '\'' +
                ", desc='" + desc + '\'' +
                ", sex=" + sex +
                ", modifyAt=" + modifyAt +
                ", follows=" + follows +
                ", following=" + following +
                ", isFollow=" + isFollow +
                ", user=" + user +
                '}';
    }
}
