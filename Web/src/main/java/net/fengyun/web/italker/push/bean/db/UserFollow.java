package net.fengyun.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author fengyun 微信 15279149227
 * 用户关系的Model
 */
@Entity
@Table(name="tb_user_follow")
public class UserFollow {
    @Id
    @PrimaryKeyJoinColumn
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid" ,strategy = "uuid2")
    @Column(updatable = false,nullable = false)
    private String id;

    //定义个发起人，你关注某人，这里就是你
    //多对1 --> 你可以关注好多人 ， 你的每一次关注都是一条记录
    //你可以创建很多个关注的信息，所以是多对一
    //这里的多对一是User对应多个UserFollow
    //optional 不可选 必须存储，一条关注记录一定要有一个你
    @JoinColumn(name ="originId")
    @ManyToOne(optional = false)
    private User origin;
    //定义关联的是数据库的存储字段
    //定义关联表字段名为originId 对应的是User.id
    //把这个列提取到我们的Model中 不允许为空，不允许更新插入
    @Column(nullable = false,updatable = false,insertable = false)
    private String originId;

    //定义个关注的目标，你关注的人
    //也是多对1，你可以被很多人关注，每一次关注都市一条记录
    //optional 不可选
    @JoinColumn(name ="targetId")
    @ManyToOne(optional = false)
    private User target;

    //定义关联的是数据库的存储字段
    //定义关联表字段名为targetId 对应的是User.id
    //把这个列提取到我们的Model中 不允许为空，不允许更新插入
    @Column(nullable = false,updatable = false,insertable = false)
    private String targetId;

    //别名 也就是对target的备注名 可以为空
    @Column
    private String alias;

    //定义为创建时间戳 在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳 在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOrigin() {
        return origin;
    }

    public void setOrigin(User origin) {
        this.origin = origin;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
}
