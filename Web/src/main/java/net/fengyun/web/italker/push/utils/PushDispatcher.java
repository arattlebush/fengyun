package net.fengyun.web.italker.push.utils;


import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.google.common.base.Strings;
import net.fengyun.web.italker.push.bean.api.base.PushModel;
import net.fengyun.web.italker.push.bean.db.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author fengyun
 */
public class PushDispatcher {
    //个推配置信息
    private final static String appId = "iOYR2AEQ0d5SST4GkOWKt6";
    private final static String appKey = "nKdxdQANbNAWyl2JqCzH12";
    private final static String masterSecret = "DPPobE8tMEAfmFCZ2SzIJ7";
    private final static String host = "http://sdk.open.api.igexin.com/apiex.htm";

    private final IIGtPush pusher;
    //要受到消息和内容的列表
    private final List<BatchBean> beans = new ArrayList<>();


    public PushDispatcher() {
        pusher = new IGtPush(host, appKey, masterSecret);
    }

    /**
     * 添加一条消息
     * @param receiver 接受者
     * @param model 接受的推送model
     * @return 是否添加成功
     */
    public boolean add(User receiver, PushModel model){
        //必须有接受者的手机id
        if(receiver==null || Strings.isNullOrEmpty(receiver.getPushId()))
            return false;
        String pushString = model.getPushString();
        if(Strings.isNullOrEmpty(pushString))
            return false;
        //构建一个目标和消息
        BatchBean batchBean = buildMessage(receiver.getPushId(),pushString);
        beans.add(batchBean);
        return true;
    }

    /***
     * 对要发送的数据进行格式化封装
     * @param clientId 接受者的id
     * @param pushString 发送的信息
     * @return BatchBean
     */
    private BatchBean buildMessage(String clientId, String pushString) {
        //透传消息，不是通知栏显示，而是在MessageReceiver收到
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(pushString);
        template.setTransmissionType(0); // 这个Type为int型，填写1则自动启动app
        //构建消息
        SingleMessage message = new SingleMessage();
        message.setData(template); //把透传消息设置到单消息模板中
        message.setOffline(true); //是否进行离线发送
        message.setOfflineExpireTime(24 * 3600 * 1000);//离线消息时常

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(clientId);
        return new BatchBean(target,message);
    }

    //进行消息最终发送
    public boolean submit() {
        IBatch batch = pusher.getBatch();
        //是否有数据需要发送
        boolean haveData = false;
        for (BatchBean bean : beans) {
            try {
                batch.add(bean.message, bean.target);
                haveData = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!haveData)
            return false;
        IPushResult result = null;
        try {
            result = batch.submit();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                //失败情况下尝试重复发送
                batch.retry();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (result != null) {
            try {
                Logger.getLogger("PushDispatcher")
                        .log(Level.INFO, (String) result.getResponse().get("result"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Logger.getLogger("PushDispatcher")
                .log(Level.WARNING, "推送服务器响应异常！！！");
        return false;
    }

    //给每个人发送消息的Bean封装
    private static class BatchBean {
        Target target;
        SingleMessage message;

        public BatchBean(Target target, SingleMessage message) {
            this.target = target;
            this.message = message;
        }
    }
}
