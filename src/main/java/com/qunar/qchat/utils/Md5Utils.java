package com.qunar.qchat.utils;

import org.springframework.util.DigestUtils;

/**
 * Md5Utils
 *
 * @author binz.zhang
 * @date 2018/9/21
 */

public class Md5Utils {
    public static String md5Encode(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

}
