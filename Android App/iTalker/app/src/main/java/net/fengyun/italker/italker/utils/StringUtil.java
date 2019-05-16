package net.fengyun.italker.italker.utils;

/**
 * 字符串的工具类
 */
public class StringUtil {


    //转换
    public static String getVolUnit(float num) {

        int e = (int) Math.floor(Math.log10(num));
        if (e >= 4) {
            return "x万";
        }else if (e == 3) {
            return "x千";
        }else if (e == 2) {
            return "x100";
        }else if (e == 1){
            return "x10";
        }else {
            return "x1";
        }
    }
}
