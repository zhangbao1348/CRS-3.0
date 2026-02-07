package com.crs.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据转换工具类
 * 用于处理各种数据类型的转换
 */
public class DataConverter {

    // 日期格式
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 将日期转换为字符串
     * @param date 日期对象
     * @param format 日期格式
     * @return 日期字符串
     */
    public static String dateToString(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 将日期转换为字符串，使用默认格式 yyyy-MM-dd
     * @param date 日期对象
     * @return 日期字符串
     */
    public static String dateToString(Date date) {
        return dateToString(date, DATE_FORMAT);
    }

    /**
     * 将日期转换为 datetime 字符串，使用格式 yyyy-MM-dd HH:mm:ss
     * @param date 日期对象
     * @return datetime 字符串
     */
    public static String datetimeToString(Date date) {
        return dateToString(date, DATETIME_FORMAT);
    }

    /**
     * 将字符串转换为日期
     * @param dateStr 日期字符串
     * @param format 日期格式
     * @return 日期对象
     */
    public static Date stringToDate(String dateStr, String format) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将字符串转换为日期，使用默认格式 yyyy-MM-dd
     * @param dateStr 日期字符串
     * @return 日期对象
     */
    public static Date stringToDate(String dateStr) {
        return stringToDate(dateStr, DATE_FORMAT);
    }

    /**
     * 将weekdays字符串转换为数组
     * @param weekdaysStr weekdays字符串，格式为逗号分隔的数字
     * @return weekdays数组
     */
    public static List<String> weekdaysStrToList(String weekdaysStr) {
        if (weekdaysStr == null || weekdaysStr.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.stream(weekdaysStr.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * 将weekdays数组转换为字符串
     * @param weekdaysList weekdays数组
     * @return weekdays字符串，格式为逗号分隔的数字
     */
    public static String weekdaysListToStr(List<String> weekdaysList) {
        if (weekdaysList == null || weekdaysList.isEmpty()) {
            return "";
        }
        return weekdaysList.stream()
                .collect(Collectors.joining(","));
    }

    /**
     * 将整数列表转换为字符串
     * @param intList 整数列表
     * @return 逗号分隔的整数字符串
     */
    public static String intListToString(List<Integer> intList) {
        if (intList == null || intList.isEmpty()) {
            return "";
        }
        return intList.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    /**
     * 将字符串转换为整数列表
     * @param intStr 逗号分隔的整数字符串
     * @return 整数列表
     */
    public static List<Integer> stringToIntList(String intStr) {
        if (intStr == null || intStr.isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.stream(intStr.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    /**
     * 将字符串转换为布尔值
     * @param boolStr 布尔字符串（true/false, 1/0, yes/no）
     * @return 布尔值
     */
    public static boolean stringToBoolean(String boolStr) {
        if (boolStr == null) {
            return false;
        }
        boolStr = boolStr.trim().toLowerCase();
        return "true".equals(boolStr) || "1".equals(boolStr) || "yes".equals(boolStr);
    }

    /**
     * 将布尔值转换为字符串
     * @param bool 布尔值
     * @return 布尔字符串（true/false）
     */
    public static String booleanToString(boolean bool) {
        return String.valueOf(bool);
    }
}
