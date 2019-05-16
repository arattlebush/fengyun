package fengyun.android.com.factory.net;

import android.text.TextUtils;

import net.fengyun.italker.common.Common;

import java.io.IOException;

import fengyun.android.com.factory.Factory;
import fengyun.android.com.factory.persistence.Account;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求的封装
 *
 * @author fengyun
 */
public class Network {

    private static Network instance;
    private Retrofit retrofit;
    private OkHttpClient client;

    static {
        instance = new Network();
    }

    private Network() {

    }

    public static OkHttpClient getClient(){
        if (instance.client != null) {
            return instance.client;
        }
        //得到一个Ok Client
        instance.client = new OkHttpClient.Builder()
                //给所有添加一个拦截器
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //拿到我们的请求
                        Request original = chain.request();
                        //重新进行build
                        Request.Builder builder = original.newBuilder();
                        if (!TextUtils.isEmpty(Account.getToken())) {
                            //注入token
                            builder.addHeader("token", Account.getToken());
                        }
                        builder.addHeader("Content-Type", "application/json");
                        Request newRequest = builder.build();
                        //返回
                        return chain.proceed(newRequest);
                    }
                })
                .build();
        //初始化client
        return instance.client;
    }

    //构建一个 Retrofit
    public static Retrofit getRetrofit() {
        if (instance.retrofit != null) {
            return instance.retrofit;
        }
        //得到一个Ok Client
        OkHttpClient client =getClient();
        //Retrofit.Builder
        Retrofit.Builder builder = new Retrofit.Builder();

        //设置电脑连接
        instance.retrofit = builder.baseUrl(Common.Constance.API_URL)
                //设置client
                .client(client)
                //设置Gson解析
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .build();
        return instance.retrofit;
    }

    /**
     * 返回一个请求代理
     *
     * @return RemoteService
     */
    public static RemoteService remote() {
        return Network.getRetrofit().create(RemoteService.class);
    }
}
