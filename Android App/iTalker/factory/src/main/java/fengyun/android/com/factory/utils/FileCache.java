package fengyun.android.com.factory.utils;

import net.fengyun.italker.common.app.Application;
import net.fengyun.italker.utils.HashUtil;
import net.fengyun.italker.utils.StreamUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

import fengyun.android.com.factory.net.Network;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 简单的一个文件缓存。实现文件的下载操作
 * 下载成功后回调相应方法
 *
 * @author fengyun
 */

public class FileCache<Holder> {

    private File baseDir;
    private String ext;
    private CacheListener<Holder> mListener;
    private SoftReference<Holder> holderSoftReference;

    public FileCache(String baseDir, String ext, CacheListener<Holder> listener) {
        this.baseDir = new File(Application.getCacheDirFile(), baseDir);
        this.ext = ext;
        this.mListener = listener;
    }

    /**
     * 构建一个缓存文件，同一个网络路径对应一本地文件
     *
     * @return File
     */
    private File buildCacheFile(String path) {
        String key = HashUtil.getMD5String(path);
        return new File(baseDir, key + File.separator + "." + ext);
    }

    //下载方法
    public void download(Holder holder, String path) {
        //如果路径就是本地缓存路径，那么不需要下载
        if (path.startsWith(Application.getCacheDirFile().getAbsolutePath())) {
            mListener.onDownloadSucceed(holder, new File(path));
            return;
        }
        final File cacheFile  = buildCacheFile(path);
        //如果文件存在并且大小大于0
        if(cacheFile.exists() && cacheFile.length()>0){
            mListener.onDownloadSucceed(holder, cacheFile);
            return;
        }
        //把目标进行软引用
        holderSoftReference = new SoftReference<Holder>(holder);
        OkHttpClient client = Network.getClient();
        Request request = new Request.Builder()
                .url(path)
                .get()
                .build();
        //发起请求
        Call call = client.newCall(request);
        call.enqueue(new NetCallback(holder,cacheFile));
    }

    //拿最后的目标，只能使用一次
    private Holder getLastHolderAndClean(){
        if(holderSoftReference==null)
            return null;
        else {
            //拿并清理
            Holder holder = holderSoftReference.get();
            holderSoftReference.clear();
            return holder;
        }
    }

    //下载的回调
    private class NetCallback implements Callback{

        private final SoftReference<Holder> holderSoftReference;
        private final File file;

        public NetCallback(Holder holder, File file) {
            this.holderSoftReference = new SoftReference<Holder>(holder);
            this.file = file;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Holder holder = holderSoftReference.get();
            if(holder!=null && holder==getLastHolderAndClean())
                //仅仅最后一次的才是有效的
                FileCache.this.mListener.onDownloadFailed(holder);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream inputStream = response.body().byteStream();
            //文件的copy保存操作
            if(inputStream!=null && StreamUtil.copy(inputStream,file)){
                Holder holder = holderSoftReference.get();
                if(holder!=null && holder==getLastHolderAndClean())
                    //仅仅最后一次的才是有效的
                    FileCache.this.mListener.onDownloadSucceed(holder,file);
            }else{
                onFailure(call,null);
            }

        }
    }


    //相关的监听
    public interface CacheListener<Holder> {
        //下载成功,把目标丢回去
        void onDownloadSucceed(Holder holder, File file);

        //下载失败
        void onDownloadFailed(Holder holder);
    }
}
