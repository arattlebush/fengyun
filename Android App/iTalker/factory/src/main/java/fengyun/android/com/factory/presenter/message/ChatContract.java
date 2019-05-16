package fengyun.android.com.factory.presenter.message;

import net.fengyun.italker.factory.presenter.BaseContract;

import java.util.List;

import fengyun.android.com.factory.model.db.Group;
import fengyun.android.com.factory.model.db.Message;
import fengyun.android.com.factory.model.db.User;
import fengyun.android.com.factory.model.db.view.MemberUserModel;

/**
 * 聊天契约
 * @author fengyun
 */

public interface ChatContract {

    interface Presenter extends BaseContract.Presenter{
        //发送文字
        void pushText(String content);
        //发送语音
        void pushAudio(String path,long time);
        //发送图片
        void pushImages(String[] paths);
        //发送文件
        void pushFile(String path);
        //重新发送一个消息，返回是否调度成功
        boolean rePush(Message message);
    }
    //界面的基类
    interface View<InitModel> extends BaseContract.RecyclerView<Presenter,Message>{
        //初始化的model
        void onInit(InitModel model);
    }
    //用户界面
    interface UserView extends View<User>{

    }
    //群界面
    interface GroupView extends View<Group>{
        //显示管理员菜单
        void showAdminOption(boolean isAdmin);

        //初始化成员信息
        void onInitGroupMembers(List<MemberUserModel> members,long moreCount);


    }

}
