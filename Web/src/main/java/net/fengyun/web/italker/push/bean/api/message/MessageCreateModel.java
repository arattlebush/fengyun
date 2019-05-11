package net.fengyun.web.italker.push.bean.api.message;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import net.fengyun.web.italker.push.bean.db.Message;

/**
 * API 请求的Model格式
 * @author fengyun
 */
public class MessageCreateModel {

    //Id从客户端生成，一个UUID
    @Expose
    private String id;
    @Expose
    private String content;
    @Expose
    private String attach;
    @Expose
    private int type = Message.TYPE_STR;
    @Expose
    private String receiverId;

    //接受者的类型,群、人
    @Expose
    private int receiverType = Message.RECTIVER_TYPE_NONE;


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

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(int receiverType) {
        this.receiverType = receiverType;
    }


    public static boolean check(MessageCreateModel model) {
        // Model 不允许为null，
        // 并且只需要具有一个及其以上的参数即可
        return  model!=null
                &&  !(Strings.isNullOrEmpty(model.id)
                || Strings.isNullOrEmpty(model.content)
                || Strings.isNullOrEmpty(model.receiverId)
//                || Strings.isNullOrEmpty(model.attach)
        )
                && (model.receiverType == Message.RECTIVER_TYPE_NONE || model.receiverType == Message.RECTIVER_TYPE_GROUP)

                && (model.type == Message.TYPE_STR
                || model.type == Message.TYPE_PIC
                || model.type == Message.TYPE_FILE
                || model.type == Message.TYPE_AUDIO);

    }

}
