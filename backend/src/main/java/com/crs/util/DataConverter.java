package com.crs.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据转换工具类 (DataConverter)
 * 
 * <p>本类提供了 CRS 系统中常用的跨类型数据转换功能，主要涵盖：</p>
 * <ul>
 *     <li>日期与字符串的相互转换（支持标准日期及日期时间格式）。</li>
 *     <li>周循环设定 (Weekdays) 的格式化处理（逗号分隔字符串与列表的转换）。</li>
 *     <li>基础数据类型（整数列表、布尔值等）的解析与格式化。</li>
 * </ul>
 */
public class DataConverter {

    /** 默认日期格式：年-月-日 */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    /** 默认日期时间格式：年-月-日 时:分:秒 */
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 将 Date 对象转换为指定格式的字符串。
     * 
     * @param date 日期对象
     * @param format 指定的日期格式（如 "yyyy-MM-dd"）
     * @return 格式化后的日期字符串，若输入为 null 则返回空字符串
     */
    public static String dateToString(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 将 Date 对象转换为默认格式 (yyyy-MM-dd) 的字符串。
     * 常用场景：价格日历、房态查询。
     * 
     * @param date 日期对象
     * @return 日期字符串
     */
    public static String dateToString(Date date) {
        return dateToString(date, DATE_FORMAT);
    }

    /**
     * 将 Date 对象转换为日期时间格式 (yyyy-MM-dd HH:mm:ss) 的字符串。
     * 常用场景：操作日志、订单创建时间显示。
     * 
     * @param date 日期对象
     * @return 日期时间字符串
     */
    public static String datetimeToString(Date date) {
        return dateToString(date, DATETIME_FORMAT);
    }

    /**
     * 将字符串按指定格式解析为 Date 对象。
     * 
     * @param dateStr 日期字符串
     * @param format 解析格式
     * @return Date 对象，若解析失败或输入为空则返回 null
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
     * 将字符串按默认格式 (yyyy-MM-dd) 解析为 Date 对象。
     * 
     * @param dateStr 日期字符串
     * @return Date 对象
     */
    public static Date stringToDate(String dateStr) {
        return stringToDate(dateStr, DATE_FORMAT);
    }

    /**
     * 将逗号分隔的周几字符串（如 "1,2,3,4,5"）转换为字符串列表。
     * 常用场景：价格码的适用周几限制解析。
     * 
     * @param weekdaysStr 逗号分隔的周几字符串
     * @return 字符串列表
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
     * 将周几字符串列表转换为逗号分隔的字符串。
     * 常用场景：将前端提交的周几数组持久化到数据库。
     * 
     * @param weekdaysList 周几列表
     * @return 逗号分隔的字符串
     */
    public static String weekdaysListToStr(List<String> weekdaysList) {
        if (weekdaysList == null || weekdaysList.isEmpty()) {
            return "";
        }
        return weekdaysList.stream()
                .collect(Collectors.joining(","));
    }

    /**
     * 将整数列表转换为逗号分隔的字符串。
     * 常用场景：存储关联的 ID 集合。
     * 
     * @param intList 整数列表
     * @return 逗号分隔的字符串
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
     * 将逗号分隔的整数字符串转换为整数列表。
     * 
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
     * 将各种风格的布尔字符串转换为 boolean 值。
     * 支持 "true", "1", "yes" 识别为 true。
     * 
     * @param boolStr 输入字符串
     * @return 解析后的布尔值
     */
    public static boolean stringToBoolean(String boolStr) {
        if (boolStr == null) {
            return false;
        }
        boolStr = boolStr.trim().toLowerCase();
        return "true".equals(boolStr) || "1".equals(boolStr) || "yes".equals(boolStr);
    }

    /**
     * 将布尔值转换为其字符串形式。
     * 
     * @param bool 布尔值
     * @return "true" 或 "false"
     */
    public static String booleanToString(boolean bool) {
        return String.valueOf(bool);
    }
}

