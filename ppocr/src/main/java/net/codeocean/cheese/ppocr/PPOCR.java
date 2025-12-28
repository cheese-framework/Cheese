package net.codeocean.cheese.ppocr;

import android.graphics.Bitmap;

public class PPOCR {
    static {
        System.loadLibrary("cheese_ppocr");
    }
    static  Boolean isInit=false;
    public Boolean init(){
        if (!isInit){
            isInit =initr();
        }
        return isInit;
    }
    public String ocr(Bitmap bitmap){
        if (isInit){
            return infer(bitmap);
        }
        return "请先初始化Ocr！";
    }


    public native boolean initr();

    public native String infer(Bitmap bitmap);
}
