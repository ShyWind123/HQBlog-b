package com.shywind.hqblog.Tools;

import jakarta.xml.bind.annotation.adapters.HexBinaryAdapter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyTool {
    public static String getHashString(String originalStr){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(originalStr.getBytes());
            // 计算哈希值
            byte[] result = messageDigest.digest();
            // 将哈希值转换为十六进制字符串
            String hexString = new HexBinaryAdapter().marshal(result);
            return hexString;
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            return null;
        }
    }
}
