package net.fengyun.web.italker.push.bean.db;

import net.fengyun.web.italker.push.bean.api.group.GroupCreateModel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author fengyun 微信 15279149227
 *         用户关系的Model
 */
@Entity
@Table(name = "tb_group")
public class Group {

    @Id
    @PrimaryKeyJoinColumn
    //主键生成存储的内型为UUID
    @GeneratedValue(generator = "uuid")
    //把uuid的生成器定义为uuid2 uuid2是常规的UUID toString
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //不允许修改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //群名称
    @Column(nullable = false)
    private String name;

    //群描述
    @Column(nullable = false)
    private String description;

    //群图片
    @Column(nullable = false)
    private String picture;


    //定义为创建时间戳 在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳 在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();


    //群的创建者
    //optional 必须有一个创建者
    //fetch记载方式为急加载 加载群的时候就必须加载创建者的信息
    //cascade s练级的级别为All 所有的更改 度将进行关系更新
    @JoinColumn(name = "ownerId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private User owner;

    @Column(nullable = false, updatable = false, insertable = false)
    private String ownerId;

    public Group() {
    }

    public Group(User owner, GroupCreateModel model) {
        this.owner = owner;
        this.name = model.getName();
        this.description = model.getDesc();
        this.picture = model.getPicture();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


}
