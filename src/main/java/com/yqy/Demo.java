package com.yqy;

/**
 * @author bahsk
 * @createTime 2022-02-02 22:17
 * @description
 * @program: lightframework
 */
public class Demo {

    public static final String STR = "com.imoocm.yqy";

    public static void main(String[] args) {
        String str = STR;
        String replace = str.replace("m.", "/");
        System.out.println(replace);
    }
}
