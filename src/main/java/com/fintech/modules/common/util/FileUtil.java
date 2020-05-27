package com.fintech.modules.common.util;

import org.springframework.util.FileCopyUtils;

import java.io.*;

public class FileUtil {

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        File f = new File("D:\\development\\apache-tomcat-8.5.29-windows-x86.zip");
        byte[] bytes = FileCopyUtils.copyToByteArray(new FileInputStream(f));

        File fo = new File("D:\\development\\apache-tomcat-8.5.29-windows-x86_bak.zip");
        FileOutputStream out = new FileOutputStream(fo);
        out.write(bytes);
        System.out.println(bytes.length);
    }
}
