package fengyun.android.com.factory.net;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import net.fengyun.italker.utils.HashUtil;

import java.io.File;
import java.util.Date;

import fengyun.android.com.factory.Factory;

/**
 * @author fengyun
 *         上传工具类 用于上传任意文件到阿里OSS存储
 */
public class UploadHelper {

    private static final String TAG = "UploadHelper";

    //与你们的存储区域 这边要改成自己的
    public static final String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    private static final String AccessKeyId="LTAI8AJPPJZG1We0";
    private static final String AccessKeySecret="fHkrZSDmXF5awfVD4VOvGPt10QHITk";
    private static final String BUCKET_NAME = "italker-gy";

    private static OSS getClient() {


        // 构造方法两个参数 一个accessKeyId 一个是accessKeySecret
        OSSCredentialProvider credentialProvider = new
                OSSPlainTextAKSKCredentialProvider(AccessKeyId, AccessKeySecret);
        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }

    /**
     * 上传的最终方法，成功返回一个路径
     *
     * @param objkey 上传上去后，在服务器上的独立的key
     * @param path   需要上传的文件路径
     * @return 存储的地址
     */
    public static String upload(String objkey, String path) {
        // 构造一个上传请求
        PutObjectRequest request = new
                PutObjectRequest(BUCKET_NAME, objkey, path);

        try {
            //初始化上传的client
            OSS client = getClient();
            //开始同步上传
            PutObjectResult result = client.putObject(request);
            //得到一个外网可访问的地址
            String url = client.presignPublicObjectURL(BUCKET_NAME, objkey);
            Log.e(TAG, String.format("PublicObjectURL:%s", url));
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传普通图片
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImage(String path) {
        String key = getImageObjkey(path);
        return upload(key,path);
    }

    /**
     * 上传头像
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path) {
        String key = getPortraitObjkey(path);
        return upload(key,path);
    }

    /**
     * 上传音频
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path) {
        String key = getAudioObjkey(path);
        return upload(key,path);
    }

    /**
     * 分月存储，避免一个文件夹太多
     * @return
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    /**
     * 得到普通图片的key image/201703/dawewqfas243rfwr234.jpg
     * @param path
     * @return
     */
    public static String getImageObjkey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("image/%s/%s.jpg", dateString, fileMd5);
    }

    /**
     * 得到头像的key portrait/201703/dawewqfas243rfwr234.jpg
     * @param path
     * @return
     */
    public static String getPortraitObjkey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("portrait/%s/%s.jpg", dateString, fileMd5);
    }

    /**
     * 得到音频的key audio/201703/dawewqfas243rfwr234.mp3
     * @param path
     * @return
     */
    public static String getAudioObjkey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("audio/%s/%s.mp3", dateString, fileMd5);
    }
}
