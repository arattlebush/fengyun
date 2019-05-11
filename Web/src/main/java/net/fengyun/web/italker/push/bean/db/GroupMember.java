package net.fengyun.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author fengyun 微信 15279149227
 *         群成员表
 */
@Entity
@Table(name = "tb_group_member")
public class GroupMember {

    public static final int NOTIFY_LEVEL_INVALID = -1;//不接受消息

    public static final int NOTIFY_LEVEL_NONE = 0;//默认通知级别

    public static final int NOTIFY_LEVEL_CLOSE = 1;//接受消息不提示

    public static final int PERMISSION_TYPE_NONE = 0;//默认权限

    public static final int PERMISSION_TYPE_ADMIN = 1;//管理员

    public static final int PERMISSION_TYPE_ADMIN_SU = 100;//创建者

    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的内型为UUID
    @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2 uuid2是常规的UUID toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //不允许修改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //别名 也就是对群里的备注名 可以为空
    @Column
    private String alias;

    //消息通知级别
    @Column(nullable = false)
    private int nitifyLevel = NOTIFY_LEVEL_NONE;

    //成员的权限类型
    @Column(nullable = false)
    private int permissionType = PERMISSION_TYPE_NONE;


    //定义为创建时间戳 在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳 在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //成员信息对应的成员
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User user;

    @Column(nullable = false, insertable = false, updatable = false)
    private String userId;


    //成员信息对应的群信息
    @JoinColumn(name = "groupId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Group group;


    @Column(nullable = false, insertable = false, updatable = false)
    private String groupId;
    // 消息通知级别
    @Column(nullable = false)
    private int notifyLevel = NOTIFY_LEVEL_NONE;

    public GroupMember() {
    }

    public GroupMember(User user, Group group) {
        this.user = user;
        this.group = group;
    }

    public int getNotifyLevel() {
        return notifyLevel;
    }

    public void setNotifyLevel(int notifyLevel) {
        this.notifyLevel = notifyLevel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getNitifyLevel() {
        return nitifyLevel;
    }

    public void setNitifyLevel(int nitifyLevel) {
        this.nitifyLevel = nitifyLevel;
    }

    public int getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(int permissionType) {
        this.permissionType = permissionType;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
