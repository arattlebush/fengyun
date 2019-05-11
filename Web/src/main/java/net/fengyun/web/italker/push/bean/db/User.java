package net.fengyun.web.italker.push.bean.db;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author fengyun 微信 15279149227
 *  用户关系的Model
 */
@Entity
@Table(name="tb_user")
public class User implements Principal {

    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的内型为UUID
    @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2 uuid2是常规的UUID toString
    @GenericGenerator(name = "uuid" ,strategy = "uuid2")
    //不允许修改，不允许为null
    @Column(updatable = false,nullable = false)
    private String id;


    @Column(nullable = false, length = 128 ,unique = true)
    private String name;

    @Column(nullable = false,length = 62 ,unique = true)
    private String phone;

    @Column(nullable = false)
    private String password;

    //用于用户的头像 可以为空
    @Column
    private String portrait;

    //用于用户的描述  可以为空
    @Column
    private String description;

    //用于性别 默认为男
    @Column(nullable = false)
    private int sex = 0;

    @Column(unique = true)
    private String token;

    //用于设备的id
    @Column()
    private String pushId;

    //定义为创建时间戳 在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳 在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //一对多 一个用户可以被很多人关注 每一次关注都市一次记录
    //我关注的人列表方法
    //对应的数据库表的TB_USER_FOLLOW.originId
    @JoinColumn(name = "originId" )
    @LazyCollection(LazyCollectionOption.EXTRA)
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<UserFollow> following = new HashSet<>();

    //一对多 一个用户可以被很多人关注 每一次关注都市一次记录
    //关注我的列表方法
    //对应的数据库表的TB_USER_FOLLOW.targetId
    @JoinColumn(name = "targetId" )
    @LazyCollection(LazyCollectionOption.EXTRA)
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<UserFollow> followers = new HashSet<>();

    //我所有创建的群
    //这边只能懒加载 因为group有了急加载 只有遍历集合的时候才加载
    @JoinColumn(name ="ownerId")
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Group> groups = new HashSet<>();

    //我所有发布的朋友圈
    //这边只能懒加载 因为friendCircle有了急加载 只有遍历集合的时候才加载
    @JoinColumn(name ="releaseId")
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<FriendCircle> friendCircles = new HashSet<>();

    //对应点赞的id
    @JoinColumn(name = "userId")
    @OneToOne( fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Fabulous fabulous;

    //我所有评论信息
    //这边只能懒加载 因为Comment有了急加载 只有遍历集合的时候才加载
    @JoinColumn(name ="commentId")
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Set<Comment> comments = new HashSet<>();

    //最后一次收到消息的时间
    private LocalDateTime lastReceivedAt = LocalDateTime.now();

    public Fabulous getFabulous() {
        return fabulous;
    }

    public void setFabulous(Fabulous fabulous) {
        this.fabulous = fabulous;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getLastReceivedAt() {
        return lastReceivedAt;
    }

    public void setLastReceivedAt(LocalDateTime lastReceivedAt) {
        this.lastReceivedAt = lastReceivedAt;
    }

    public Set<UserFollow> getFollowing() {
        return following;
    }

    public void setFollowing(Set<UserFollow> following) {
        this.following = following;
    }

    public Set<UserFollow> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<UserFollow> followers) {
        this.followers = followers;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<FriendCircle> getFriendCircles() {
        return friendCircles;
    }

    public void setFriendCircles(Set<FriendCircle> friendCircles) {
        this.friendCircles = friendCircles;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
