package fengyun.android.com.factory.data.user;

import fengyun.android.com.factory.model.card.UserCard;

/**
 * 用户中心的基本定义
 * @author fengyun
 */

public interface UserCenter {

    //分发处理一堆用户卡片的信息，并更新到数据库
    void  dispatch(UserCard... cards);
}
