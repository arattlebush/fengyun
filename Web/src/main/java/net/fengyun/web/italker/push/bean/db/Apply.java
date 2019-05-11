package net.fengyun.web.italker.push.bean.db;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author fengyun 微信 15279149227
 *         消息申请
 */
@Entity
@Table(name = "tb_apply")
public class Apply {

    public static final int TYPE_ADD_USER = 1;
    public static final int TYPE_ADD_GROUP = 2;

    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的内型为UUID
    @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2 uuid2是常规的UUID toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //不允许修改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //描述部分，对我们的申请信息做描述
    //eg 我想加你为好友
    @Column(nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT")
    private String attach;

    //当前申请的类型
    @Column(nullable = false)
    private int type = TYPE_ADD_USER;

    //目标id 不建立强关联，不建立主外键关系
    //type = TYPE_ADD_USER 对应的是user.id
    //type =TYPE_ADD_GROUP 对应的是group.id
    @Column(nullable = false)
    private String targerId;


    //定义为创建时间戳 在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳 在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //申请人 可为空 为系统消息
    //一个人可以有很多个申请
    @ManyToOne()
    @JoinColumn(name = "applicantId")
    private User user;

    @Column(insertable = false, updatable = false)
    private String applicantId;

    // 目标Id 不进行强关联，不建立主外键关系
    // type->TYPE_ADD_USER：User.id
    // type->TYPE_ADD_GROUP：Group.id
    @Column(nullable = false)
    private String targetId;

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTargerId() {
        return targerId;
    }

    public void setTargerId(String targerId) {
        this.targerId = targerId;
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

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }
}
