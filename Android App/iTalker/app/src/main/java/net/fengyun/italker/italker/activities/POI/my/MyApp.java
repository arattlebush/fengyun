package net.fengyun.italker.italker.activities.POI.my;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * 百度地图的使用示例
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        //初始化百度地图数据
        SDKInitializer.initialize(this);
    }
}
