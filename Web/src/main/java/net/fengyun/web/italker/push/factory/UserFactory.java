package net.fengyun.web.italker.push.factory;

import com.google.common.base.Strings;
import net.fengyun.web.italker.push.bean.db.User;
import net.fengyun.web.italker.push.bean.db.UserFollow;
import net.fengyun.web.italker.push.utils.Hib;
import net.fengyun.web.italker.push.utils.TextUtil;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author fengyun 微信 15279149227
 *         用户的处理模块
 */
public class UserFactory {


    //通过token字段找到查询到用户信息
    //只能自己使用，查询的信息是个人信息。非他人信息
    public static User findByToken(String token) {
        return Hib.query(session -> {
            User user = (User) session.createQuery("from User Where token=:token")
                    .setParameter("token", token).uniqueResult();
            return user;
        });
    }

    //通过手机号找到user
    public static User findByPhone(String phone) {
        return Hib.query(session -> {
            User user = (User) session.createQuery("from User Where phone=:inPhone")
                    .setParameter("inPhone", phone).uniqueResult();
            return user;
        });
    }

    //通过用户名找到user
    public static User findByName(String name) {
        return Hib.query(session -> {
            User user = (User) session.createQuery("from User Where name=:inName")
                    .setParameter("inName", name).uniqueResult();
            return user;
        });
    }

    //通过ID找到user
    public static User findById(String id) {
        //通过ID更方便
        return Hib.query(session -> session.get(User.class, id));
    }

    /**
     * 使用账号和密码进行登录
     *
     * @param account  手机号
     * @param password 密码
     * @return
     */
    public static User login(String account, String password) {
        final String accountStr = account.trim();
        //进行同样的处理 然后才能匹配
        final String encodePassword = encodePassword(password);
        User user = Hib.query(session ->
                (User) session.createQuery("from User Where phone=:inPhone and password=:inPassword")
                        .setParameter("inPhone", accountStr)
                        .setParameter("inPassword", encodePassword)
                        .uniqueResult()
        );
        if (user != null) {
            //对user进行登录处理
            user = login(user);
        }
        return user;
    }


    /**
     * 用户注册 需要写入数据库，并返回数据的User信息
     * 传入的数据是用户账号和用户密码和用户的昵称
     *
     * @return
     */
    public static User register(String account, String password, String name) {
        //去除首尾空格
        account = account.trim();
        //处理密码
        password = encodePassword(password);
        User user = createUser(account, password, name);
        if (user != null) {
            user = login(user);
        }
        return user;
    }


    /**
     * 把一个user进行登录操作
     * 本质上市对token操作
     *
     * @param user
     * @return
     */
    public static User login(User user) {
        //使用一个随机的UUid值 充当token
        String newToken = UUID.randomUUID().toString();
        //进行一次base64对称加密
        newToken = TextUtil.encodeBase64(newToken);
        user.setToken(newToken);
        //进行数据库保存或者更新的操作
        return update(user);
    }

    /**
     * 给当前的账户绑定PushId
     *
     * @param user   自己的User
     * @param pushId 自己设备的PushId
     * @return User
     */
    public static User bindPushId(User user, String pushId) {
        //查询有没有其他用户绑定这个设备
        //取消绑定，避免推送混乱
        if (Strings.isNullOrEmpty(pushId)) {
            return null;
        }
        Hib.queryOnly(session -> {
            List<User> userList = (List<User>) session.
                    createQuery("from User where lower(pushId)=:pushId and id!=:userId")
                    .setParameter("pushId", pushId.toLowerCase())
                    .setParameter("userId", user.getId()).list();
            for (User u : userList) {
                u.setPushId(null);
                session.saveOrUpdate(u);
            }
        });
        if (pushId.equalsIgnoreCase(user.getPushId())) {
            //如果当前需要绑定的设备ID 之前已经绑定过了
            return user;
        } else {
            //如果当前账户之前的设备ID和需要绑定的不同
            //需要单点登录让之前的设备退出账户 给之前的设备推送一条退出消息
            if (Strings.isNullOrEmpty(user.getPushId())) {
                //推送一个退出消息
                PushFactory.pushLogout(user);
            }
            user.setPushId(pushId);
            //更新新的设备id
            return update(user);
        }

    }

    /**
     * 更新数据库用户信息
     *
     * @param user 用户
     * @return 返回一个用户回去
     */
    public static User update(User user) {
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }

    /**
     * 注册部分的新建用户逻辑
     *
     * @param account  手机号码
     * @param password 加密后的密码
     * @param name     用户名
     * @return 返回一个用户
     */
    public static User createUser(String account, String password, String name) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);
        //数据库存储
        return Hib.query(session -> {
            session.save(user);
            return user;
        });
    }

    /**
     * 对密码进行加密
     *
     * @param password 原文
     * @return 密文
     */
    private static String encodePassword(String password) {
        //去除首尾空格
        password = password.trim();
        //进行md5 非对称加密，加盐会更安全 盐也要存储
        password = TextUtil.getMD5(password);

        //再进行一次对称的Base64加密，当然可以采取加盐的方案
        return TextUtil.encodeBase64(password);
    }

    /**
     * 获取联系人列表
     *
     * @param self User
     * @return List<User>
     */
    public static List<User> contacts(User self) {
        return Hib.query(session -> {
            //重新加载一次用户信息到self中，和当前的session绑定
            session.load(self, self.getId());
            //获取我关注的人
            Set<UserFollow> flows = self.getFollowing();
            //java8语法，通过 stream中的map方法拿到取出每一个UserFollow
            //在拿到 UserFollow的getTarget()方法 最后toList()的
            return flows.stream()
                    .map(UserFollow::getTarget)
                    .collect(Collectors.toList());
        });
    }

    /**
     * 关注人的操作
     *
     * @param origin 发起者
     * @param target 被关注的人
     * @param alias  备注名
     * @return 被关注的人
     */
    public static User follow(final User origin, final User target, final String alias) {

        UserFollow follow = getUserFollow(origin, target);
        if (follow != null) {
            return follow.getTarget();
        }
        return Hib.query(session -> {
            //想要操作懒加载的数据，需要懒加载一次
            session.load(origin, origin.getId());
            session.load(target, target.getId());
            //我关注人的时候，同时他也关注我
            //所以需要添加两天UserFollow数据
            UserFollow userFollow = new UserFollow();
            userFollow.setOrigin(origin);
            userFollow.setTarget(target);
            userFollow.setAlias(alias);

            UserFollow targetFollow = new UserFollow();
            targetFollow.setOrigin(target);
            targetFollow.setTarget(origin);

            //保存数据到数据库
            session.save(userFollow);
            session.save(targetFollow);
            return target;
        });

    }

    /**
     * 查询两个人是否已经关注
     *
     * @param origin 发起者
     * @param target 被关注的人
     * @return 返回中间类 UserFollow
     */
    public static UserFollow getUserFollow(final User origin, final User target) {
        return Hib.query(session -> (UserFollow) session
                .createQuery("from UserFollow where originId =:originId and targetId =:targetId")
                .setParameter("originId", origin.getId())
                .setParameter("targetId", target.getId())
                .setMaxResults(1)
                //唯一返回
                .uniqueResult()
        );
    }

    /**
     * 搜索联系人的实现
     *
     * @param name 查询的名字，允许为空
     * @return 查询到的用户集合，如果name为空，则返回最近的用户
     */
    public static List<User> search(String name) {
        if (Strings.isNullOrEmpty(name))
            name = "";//保证不能为null的情况，减少后面的一下判断和额外的错误
        final String searchName = "%" + name + "%";
        return Hib.query(session ->
                //查询的条件 name忽略大小写，并且使用like（模糊查询）
                //头像和描述必须完善才能查询的到
                (List<User>) session.createQuery("from User where lower(name) like :name and portrait is not null and description is not null")
                        .setParameter("name", "" + searchName + "")
                        .setMaxResults(20)//最多20条数据
                        .list()

        );
    }
}
