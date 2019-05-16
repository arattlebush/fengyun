package net.fengyun.italker.italker.utils;

import java.util.List;

import fengyun.android.com.factory.model.db.Product;

/**
 * 获取数据的工具类
 */

public class PriceUtil {


    /**
     * 获取差价
     * @param products
     * @return
     */
    public static float[] getDifferencePrice(List<Product> products){
        float baseValue =0f;
        float maxPrice =0f;
        float minPrice =0f;
        float difference =0f;
        float[] prices = new float[3];
       if(products!=null && products.size()>0){
           for (int i = 0; i < products.size(); i++) {
               //获取开盘价
               baseValue = products.get(0).getOpen();
               if (i != 0) {
                   if (products.get(i).getLow() <minPrice) {
                       minPrice = products.get(i).getLow();
                   }
                   if(products.get(i).getHigh() > maxPrice){
                       maxPrice = products.get(i).getHigh();
                   }
                   float price = baseValue - minPrice > maxPrice - baseValue ?
                           baseValue - minPrice : maxPrice - baseValue;
                   difference = price > difference ? price : difference;
               }else{
                   minPrice =products.get(0).getLow();
                   maxPrice = products.get(0).getHigh();
                   difference = baseValue -  minPrice >maxPrice-baseValue?
                           baseValue - minPrice: maxPrice-baseValue;
               }
           }
       }
        prices[0]=difference;
        prices[1]=maxPrice;
        prices[2]=minPrice;
        return prices;
    }

}
