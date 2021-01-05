package com.yan.study.biz.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String format(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    public static void main(String[] args) {
        System.out.println(format(new Date()));
    }

}
