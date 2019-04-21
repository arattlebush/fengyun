package net.fengyun.italker.italker.frags.main.assist;

/**
 * Created by gaoyuana on 2017/4/2.
 */

public class Class {
    private String name;
    private int imageId;
    public Class(String name, int imageId){
    this.name=name;
        this.imageId=imageId;
    }
    public String getName(){
        return name;
    }
    public int getImageId(){
        return imageId;
    }
}
