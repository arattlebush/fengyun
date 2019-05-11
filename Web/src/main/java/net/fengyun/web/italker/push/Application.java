package net.fengyun.web.italker.push;

import net.fengyun.web.italker.push.provider.AuthRequestFilter;
import net.fengyun.web.italker.push.provider.GsonProvider;
import net.fengyun.web.italker.push.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.logging.Logger;

/**
 * @author fengyun 2017/5/20 0020.
 *
 */
public class Application extends ResourceConfig{

    public Application() {

        //注册逻辑处理的包名
        packages(AccountService.class.getPackage().getName());

        //注册请求全局拦截器
        register(AuthRequestFilter.class);
        //注册Json 转换器
//        register(JacksonJsonProvider.class);

        //注册GSON转换器
        register(GsonProvider.class);

        //注册日志打印输出
        register(Logger.class);
    }
}
