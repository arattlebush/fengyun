package fengyun.android.com.factory.model.card;

/**
 * @author fengyun
 * 股票产品的卡片
 */

public class ProductCard {

    private float open;
    private float high;
    private float low;
    private float close;
    private float volume;

    /**
     * 开盘价 open
     * 收盘价 high
     * 最高价 low
     * 最低价 close
     * 成交数量 volume
     * @return
     */


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

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }



    @Override
    public String toString() {
        return "ProductCard{" +
                "open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", volume=" + volume +
                '}';
    }
}
