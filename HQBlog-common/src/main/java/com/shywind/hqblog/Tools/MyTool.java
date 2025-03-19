package com.shywind.hqblog.Tools;

import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyTool {
    // 定义一个静态方法 getHashString，接收一个字符串参数 originalStr，返回一个字符串
    public static String getHashString(String originalStr){
        try{
            // 获取 SHA-256 算法的 MessageDigest 实例
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            // 将原始字符串转换为字节数组，并更新到 MessageDigest 实例中
            messageDigest.update(originalStr.getBytes());

            // 计算哈希值，结果是一个字节数组
            byte[] result = messageDigest.digest();

            // 使用 HexBinaryAdapter 将字节数组转换为十六进制字符串
            String hexString = new HexBinaryAdapter().marshal(result);

            // 返回十六进制字符串
            return hexString;
        } catch (NoSuchAlgorithmException e) {
            // 如果指定的算法（SHA-256）不可用，捕获异常并打印错误信息
            System.out.println(e);

            // 返回 null 表示哈希计算失败
            return null;
        }
    }
}