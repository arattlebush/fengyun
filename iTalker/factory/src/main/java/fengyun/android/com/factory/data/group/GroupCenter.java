package fengyun.android.com.factory.data.group;

import fengyun.android.com.factory.model.card.GroupCard;
import fengyun.android.com.factory.model.card.GroupMemberCard;

/**
 * 群中心的基本定义
 * @author fengyun
 */

public interface GroupCenter {
    //分发处理一堆群卡片的信息，并更新到数据库
    void  dispatch(GroupCard... cards);

    //分发处理一堆群成员卡片的信息，并更新到数据库
    void  dispatch(GroupMemberCard... cards);
}
