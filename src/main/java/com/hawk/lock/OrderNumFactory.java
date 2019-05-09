package com.hawk.lock;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zhangdonghao
 * @date 2019/5/9
 */
public class OrderNumFactory {
    private static int i = 0;
    public String createOrderNum(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss|");
        return dateFormat.format(new Date())+ ++i;
    }

}
