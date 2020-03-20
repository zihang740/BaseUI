package com.hzh.frame.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author
 * @version 1.0
 * @date 2020/2/2
 */
public class GZIPTools {

    //压缩
    public static byte[] compress(String str, String encoding){
        if(str == null || str.length() == 0){
            return null;
        }

        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        byte[] compressRes = null;
        try {
            gzip = new GZIPOutputStream(outs);
            byte[] tmpBytes = str.getBytes(encoding);
            gzip.write(tmpBytes);
            gzip.close();
            compressRes = outs.toByteArray();  //一定要放在gzip.close()之后
            outs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressRes;
    }

    //解压
    public static byte[] uncompress(byte[] uncompressSource){
        if(uncompressSource == null || uncompressSource.length == 0){
            return null;
        }

        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        ByteArrayInputStream ins = new ByteArrayInputStream(uncompressSource);
        GZIPInputStream ungzip;
        byte[] uncompressRes = null;
        try {
            ungzip = new GZIPInputStream(ins);
            byte[] buff = new byte[1024];
            int n = 0;
            while((n = ungzip.read(buff)) >= 0){
                outs.write(buff, 0, n);
            }
            ungzip.close();
            uncompressRes = outs.toByteArray(); //这个放在ungzip.close()前也可以
            ins.close();
            outs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uncompressRes;
    }
}
