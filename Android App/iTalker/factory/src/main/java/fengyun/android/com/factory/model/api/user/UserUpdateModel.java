package fengyun.android.com.factory.model.api.user;

/**
 * 用户更新的model
 * @author fengyun
 */
public class UserUpdateModel {
    private String name;
    private String portrait;
    private String desc;
    private int sex;



    public UserUpdateModel(String portrait, String desc, int sex) {
        this("",portrait,desc,sex);
    }

    public UserUpdateModel(String name, String portrait, String desc, int sex) {
        this.name = name;
        this.portrait = portrait;
        this.desc = desc;
        this.sex = sex;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
