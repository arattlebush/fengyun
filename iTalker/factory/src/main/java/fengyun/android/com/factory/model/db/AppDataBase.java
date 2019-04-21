package fengyun.android.com.factory.model.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * 数据库基本信息
 * @author fengyun
 */
@Database(name = AppDataBase.NAME,version = AppDataBase.VERSION)
public class AppDataBase {

    public static final String NAME = "AppDataBase";
    public static final int VERSION = 2;

}
