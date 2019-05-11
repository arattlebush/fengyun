package net.fengyun.web.italker.push.factory;

import net.fengyun.web.italker.push.bean.api.message.MessageCreateModel;
import net.fengyun.web.italker.push.bean.db.Group;
import net.fengyun.web.italker.push.bean.db.Message;
import net.fengyun.web.italker.push.bean.db.User;
import net.fengyun.web.italker.push.utils.Hib;

/**
 * @author fengyun
 *         消息数据查询
 */
public class MessageFactory {

    //查询某一个消息
    public static Message findById(String id) {
        return Hib.query(session -> session.get(Message.class, id));
    }

    //添加一条普通消息
    public static Message add(User sender, User receiver, MessageCreateModel model) {
        Message message = new Message(sender, receiver, model);
        return save(message);
    }

    //添加一条群消息
    public static Message add(User sender, Group group, MessageCreateModel model) {
        Message message = new Message(sender, group, model);
        return save(message);
    }


    private static Message save(Message message) {
        return Hib.query(session -> {
            session.save(message);
            //写入到数据库
            session.flush();
            //紧接着查询出来
            session.refresh(message);
            return message;
        });
    }

}
