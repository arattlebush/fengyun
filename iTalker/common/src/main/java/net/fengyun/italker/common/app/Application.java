package net.fengyun.italker.common.app;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.widget.Toast;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fengyun
 *         应用程序
 */
public class Application extends android.app.Application {

    private static Application instance;
    private List<android.app.Activity> activities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(android.app.Activity activity, Bundle savedInstanceState) {
                activities.add(activity);
            }

            @Override
            public void onActivityStarted(android.app.Activity activity) {

            }

            @Override
            public void onActivityResumed(android.app.Activity activity) {

            }

            @Override
            public void onActivityPaused(android.app.Activity activity) {

            }

            @Override
            public void onActivityStopped(android.app.Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(android.app.Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(android.app.Activity activity) {
                activities.remove(activity);
            }
        });

    }

    /**
     * 关闭所有的activity
     */
    public void finishAll() {
        for (android.app.Activity activity : activities) {
            activity.finish();
        }
        showAccountActivity(this);
    }

    protected void showAccountActivity(Context context) {

    }


    /**
     * 外部获取单例
     *
     * @return Application
     */
    public static Application getInstance() {
        return instance;
    }

    /**
     * 获取缓存文件夹地址
     *
     * @return 当前APP的缓存文件夹地址
     */
    public static File getCacheDirFile() {
        return instance.getCacheDir();
    }

    /**
     * 获取头像的临时存储文件地址
     *
     * @return 临时文件
     */
    public static File getPortraitTmpFile() {
        File dir = new File(getCacheDirFile(), "portrait");
        dir.mkdirs();
        //删除一些旧的缓存图片文件
        File[] files = dir.listFiles();
        //把以前的缓存文件给删除，如果要保存多张图片的话就别删除了
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }
        //返回当前时间戳的目录文件地址
        File path = new File(dir, SystemClock.uptimeMillis() + ".jpg");
        return path.getAbsoluteFile();
    }
    /**
     * 获取头像的临时存储文件地址
     *
     * @return 临时文件
     */
    public static File getImageTmpFile() {
        File dir = new File(getCacheDirFile(), "image");
        dir.mkdirs();
        //返回当前时间戳的目录文件地址
        File path = new File(dir, SystemClock.uptimeMillis() + ".jpg");
        return path.getAbsoluteFile();
    }

    /**
     * 获取声音文件的本地地址
     *
     * @param isTmp 是否是缓存文件， True，每次返回的文件地址是一样的
     * @return 录音文件的地址
     */
    public static File getAudioTmpFile(boolean isTmp) {
        File dir = new File(getCacheDirFile(), "audio");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }

        // aar
        File path = new File(getCacheDirFile(), isTmp ? "tmp.mp3" : SystemClock.uptimeMillis() + ".mp3");
        return path.getAbsoluteFile();
    }

    /**
     * 显示一个Toast
     *
     * @param msg 字符串
     */
    public static void showToast(final String msg) {
        // Toast 只能在主线程中显示，所有需要进行线程转换，
        // 保证一定是在主线程进行的show操作
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 这里进行回调的时候一定就是主线程状态了
                Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 显示一个Toast
     *
     * @param msgId 传递的是字符串的资源
     */
    public static void showToast(@StringRes int msgId) {
        showToast(instance.getString(msgId));
    }


    // 使用系统当前日期加以调整作为照片的名称
    public static  String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

}
