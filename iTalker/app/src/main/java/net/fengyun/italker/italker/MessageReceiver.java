package net.fengyun.italker.italker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.data.helper.AccountHelper;
import fengyun.android.com.factory.model.api.PushModel;
import fengyun.android.com.factory.persistence.Account;

/**
 * @author fengyun
 * 广播接收器 个推消息接收
 */
public class MessageReceiver extends BroadcastReceiver{
    private static final String TAG = MessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)){
            case PushConsts.GET_CLIENTID:{
                Log.i(TAG, "GET_CLIENTID" + bundle.getString("clientid").toString());
                //当ID初始化的时候,获取设备id
                onClientInit(bundle.getString("clientid"));
                break;
            }
            case PushConsts.GET_MSG_DATA:{

                //常规消息送达
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String message = new String(payload);
                    Log.i(TAG, "GET_MSG_DATA" +  message);
                    onMessageArrived(message);
                }
                break;
            }
            default:
                Log.i(TAG, "OTHER" +  bundle.toString());
                break;
        }
    }

    /**
     * 当id初始化的时候
     * @param cid 设备id
     */
    private void onClientInit(String cid){
        Account.setPushId(cid);
        if (Account.isLogin()) {
            //账户登录状态，进行一次pushID绑定 没有登录的情况下不能绑定pushid
            AccountHelper.bindPush(null);
        }
    }

    /**
     * 消息达到时
     * @param message 新消息
     */
    private void onMessageArrived(String message){
        Factory.dispatchPush(message);
    }
}
