package fengyun.android.com.factory.model.db;

/**
 * @author 风云
 * 股票产品信息表
 */
//@Table(database = AppDataBase.class)
public class Product extends BaseDbModel<Product>{

//    @PrimaryKey
    private String id; // 主键
//    @Column //开盘价
    private float open;
//    @Column //最高价
    private float high;
//    @Column //最低价
    private float low;
//    @Column //收盘价
    private float close;
//    @Column //成交数
    private int volume;
//    @Column
    private String time;

    public Product(float open, float high, float low, float close, int volume, String time) {
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.time = time;
    }

    public float getOpen() {
        return open;
    }

    public void setOpen(float open) {
        this.open = open;
    }

    public float getHigh() {
        return high;
    }

    public void setHigh(float high) {
        this.high = high;
    }

    public float getLow() {
        return low;
    }

    public void setLow(float low) {
        this.low = low;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    //TODO 判断是否是同一个   暂时不做
    @Override
    public boolean isSame(Product old) {
        return false;
    }

    @Override
    public boolean isUiContentSame(Product old) {
        return false;
    }
}
