package net.fengyun.web.italker.push.service;

import net.fengyun.web.italker.push.bean.api.base.ResponseModel;
import net.fengyun.web.italker.push.bean.api.message.MessageCreateModel;
import net.fengyun.web.italker.push.bean.card.MessageCard;
import net.fengyun.web.italker.push.bean.db.Group;
import net.fengyun.web.italker.push.bean.db.Message;
import net.fengyun.web.italker.push.bean.db.User;
import net.fengyun.web.italker.push.factory.GroupFactory;
import net.fengyun.web.italker.push.factory.MessageFactory;
import net.fengyun.web.italker.push.factory.PushFactory;
import net.fengyun.web.italker.push.factory.UserFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author fengyun
 *         消息处理
 */
@Path("/msg")
public class MessageService extends BaseService {

    //发送一条消息到服务器
    @POST//127.0.0.1/api/msg
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<MessageCard> pushMessage(MessageCreateModel model) {
        if (!MessageCreateModel.check(model)) {
            return ResponseModel.buildParameterError();
        }

        User self = getSelf();

        //查询是否已经在数据库中有了
        Message message = MessageFactory.findById(model.getId());
        if (message != null) {
            //正常返回
            return ResponseModel.buildOk(new MessageCard(message));
        }
        if (model.getReceiverType() == Message.RECTIVER_TYPE_GROUP) {
            return pushToGroup(self, model);
        } else {
            return pushToUser(self, model);
        }
    }


    /**
     * 发送到人
     * @param sender 发送者
     * @param model
     */
    private ResponseModel<MessageCard> pushToUser(User sender, MessageCreateModel model) {
        User receiver = UserFactory.findById(model.getReceiverId());
        if (receiver == null) {
            //没有找到接受者
            return ResponseModel.buildNotFoundUserError("Con't find receiver user");
        }
        if (receiver.getId().equalsIgnoreCase(sender.getId())) {
            //发送者和接受者是同一个人，就返回创建消息失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //存储数据库
        Message message = MessageFactory.add(sender, receiver, model);
        //返回
        return buildAndPushResponse(sender, message);

    }

    /**
     * 发送到群
     * @param sender 发送者
     * @param model
     */
    private ResponseModel<MessageCard> pushToGroup(User sender, MessageCreateModel model) {
        //查找一个群，找群有权限的限制的
        Group group = GroupFactory.findById(sender,model.getReceiverId());
        if (group == null) {
            //没有找到接受者群，有可能是你不是群的成员
            return ResponseModel.buildNotFoundUserError("Con't find receiver group");
        }
        Message message = MessageFactory.add(sender,group,model);

        //走通用的推送逻辑
        return buildAndPushResponse(sender,message);
    }

    //构建一个返回信息
    private ResponseModel<MessageCard> buildAndPushResponse(User sender, Message message) {
        if (message == null) {
            //存储数据库失败
            return ResponseModel.buildCreateError(ResponseModel.ERROR_CREATE_MESSAGE);
        }
        //进行推送
        PushFactory.pushNewMessage(sender, message);
        //返回
        return ResponseModel.buildOk(new MessageCard(message));
    }

}
