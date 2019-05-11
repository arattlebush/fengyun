package net.fengyun.web.italker.push.service;

import net.fengyun.web.italker.push.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * @author fengyun 微信15279149227
 */
public class BaseService {

    //添加一个上下文注解 该注解会自动赋值，具体的值是我们拦截器中的值
    @Context
    protected SecurityContext securityContext;

    /**
     * 从上下文中直接获取自己的信息
     * @return
     */
    protected User getSelf(){
        return (User) securityContext.getUserPrincipal();
    }
}
