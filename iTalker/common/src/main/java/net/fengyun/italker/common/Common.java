package net.fengyun.italker.common;

/**
 * @author fengyun
 * 公共的
 */
public class Common {

    /**
     * 一些不可变的永恒的参数
     * 通常用于一些配置
     */
    public interface Constance{
        //手机号的正则,11位手机号
        String REGEX_MOBILE = "[1][3,4,5,7,8][0-9]{9}$";
        //对应自己的虚拟ip地址
        String API_URL = "http://139.199.20.26:8080/iTalker-1.0-SNAPSHOT/api/";

        //最大的上传图片大小 860KB
        long MAX_UPLOAD_IMAGE_LENGTH = 860*1024;
    }
}
