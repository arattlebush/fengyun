package net.fengyun.web.italker.push.bean.card;

import com.google.gson.annotations.Expose;
import net.fengyun.web.italker.push.bean.db.User;
import net.fengyun.web.italker.push.utils.Hib;

import java.time.LocalDateTime;

/**
 * @author fengyun
 */
public class UserCard {

    //用户的id
    @Expose
    private String id;

    //用户昵称
    @Expose
    private String name;

    //用户的电话
    @Expose
    private String phone;

    //用户的头像
    @Expose
    private String portrait;

    //用户描述
    @Expose
    private String desc;

    //用户性别
    @Expose
    private int sex = 0;

    //用户最后的更新时间
    @Expose
    private LocalDateTime modifyAt = LocalDateTime.now();

    //用户的关注的时间
    @Expose
    private int follows;

    //用户的粉丝的数量
    @Expose
    private int following;
    @Expose
    private boolean isFollow;

    public UserCard(final User user, boolean isFollow) {
        this.id = user.getId();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.portrait = user.getPortrait();
        this.desc = user.getDescription();
        this.sex = user.getSex();
        this.modifyAt = user.getUpdateAt();
        this.isFollow = isFollow;
        // 得到关注人和粉丝的数量
//        user.getFollowers().size()//懒加载会报错
        Hib.queryOnly(session -> {
            session.load(user, user.getId());
            //这个时候仅仅只是进行了数量查询，并没有查询整个集合
            //要查询整个集合，必须在session存在情况下进行遍历
            //或者使用 Hibernate.initialize(user.getFollowers());
            follows = user.getFollowers().size();
            following = user.getFollowing().size();
        });

    }

    public UserCard(final User user) {
        this(user, false);

    }

    public LocalDateTime getModifyAt() {
        return modifyAt;
    }

    public void setModifyAt(LocalDateTime modifyAt) {
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

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follo) {
        isFollow = follo;
    }
}
