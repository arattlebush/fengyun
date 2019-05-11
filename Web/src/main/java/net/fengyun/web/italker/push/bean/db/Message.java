package net.fengyun.web.italker.push.bean.db;

import net.fengyun.web.italker.push.bean.api.message.MessageCreateModel;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author fengyun 微信 15279149227
 *         消息表
 */
@Entity
@Table(name = "tb_message")
public class Message {
    public static final int RECTIVER_TYPE_NONE = 1;//发送给人的
    public static final int RECTIVER_TYPE_GROUP = 2;//发送给群的
    public static final int TYPE_STR = 1;//字符串类型
    public static final int TYPE_PIC = 2;//图片类型
    public static final int TYPE_FILE = 3;//文件类型
    public static final int TYPE_AUDIO = 3;//语音类型

    public Message() {
    }

    //普通朋友发送的构造函数
    public Message(User sender, User receiver, MessageCreateModel model) {
        this.id = model.getId();
        this.content = model.getContent();
        this.attach = model.getAttach();
        this.type = model.getType();
        this.sender = sender;
        this.receiver = receiver;
    }

    //发送给群的构造函数
    public Message(User sender, Group group, MessageCreateModel model) {
        this.id = model.getId();
        this.content = model.getContent();
        this.attach = model.getAttach();
        this.type = model.getType();
        this.sender = sender;
        this.group = group;
    }

    @Id
    @PrimaryKeyJoinColumn
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    //不允许修改，不允许为null
    @Column(updatable = false, nullable = false)
    private String id;

    //内容不允许为空 类型为Text
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //附件
    @Column
    private String attach;

    //消息类型 区分是什么消息
    @Column(nullable = false)
    private int type;

    //定义为创建时间戳 在创建时就已经写入
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt = LocalDateTime.now();

    //定义为更新时间戳 在创建时就已经写入
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt = LocalDateTime.now();

    //发送者
    @JoinColumn(name = "senderId")
    @ManyToOne(optional = false)
    private User sender;
    //这个字段仅仅只是为了对应sender的数据库字段sendleId
    //不允许手动的更新或者插入
    @Column(nullable = false,updatable = false, insertable = false)
    private String senderId;

    //接受者
    @JoinColumn(name = "receiverId")
    @ManyToOne
    private User receiver;
    //这个字段仅仅只是为了对应sender的数据库字段receiverId
    //不允许手动的更新或者插入
    @Column(updatable = false, insertable = false)
    private String receiverId;

    //一个群可以接受多个消息
    @JoinColumn(name = "groupId")
    @ManyToOne
    private Group group;
    //这个字段仅仅只是为了对应sender的数据库字段receiverId
    //不允许手动的更新或者插入
    @Column(updatable = false, insertable = false)
    private String groupId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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
