package net.fengyun.italker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 * @author fengyun
 */

public class DateTimeUtil {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yy-MM-dd", Locale.ENGLISH);

    /**
     * 获取一个简单的时间字符串
     * @param date 时间
     * @return 时间
     */
    public static String getSampleData(Date date){
        return FORMAT.format(date);
    }

}
