package com.hzh.frame.util;

import java.util.regex.Pattern;

/**
 * 字符相关处理
 * @version 1.0
 * @date 2018/11/16
 */
public class StringUtil {

    // 获得汉语拼音首字母
    public static String getAlpha(String str) {

        if (str.equals("-")) {
            return "&";
        }
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }
}
