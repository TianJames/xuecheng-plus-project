package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BigFileTest {
    @Test
    public void test_chunk() throws IOException {
        File sourceFile = new File("E:\\java\\video\\2.mp4");
        String chunkPath = "E:\\java\\video\\chunk\\";
        File chunkFolder = new File(chunkPath);
        byte[] buff = new byte[1024];
        int chunkSize = 1024 * 1024 * 5;
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        RandomAccessFile r = new RandomAccessFile(sourceFile, "r");
        for (int i = 0; i < chunkNum; i++) {
            File file = new File(chunkPath + i);
            RandomAccessFile rw = new RandomAccessFile(file, "rw");
            int len = -1;
            while ((len = r.read(buff)) != -1) {
                rw.write(buff, 0, len);
                if (file.length() >= chunkSize) {
                    break;
                }
            }
            rw.close();
        }
        r.close();
    }

    @Test
    public void test_merge() throws IOException {
        File chunkFolder = new File("E:\\java\\video\\chunk\\");
        //原始文件
        File originalFile = new File("E:\\java\\video\\2.mp4");
        //合并文件
        File mergeFile = new File("E:\\java\\video\\2_1.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024];
        //分块列表
        File[] fileArray = chunkFolder.listFiles();
        // 转成集合，便于排序
        List<File> fileList = Arrays.asList(fileArray);
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);

            }
            raf_read.close();
        }
        raf_write.close();

        //校验文件
        try (

                FileInputStream fileInputStream = new FileInputStream(originalFile);
                FileInputStream mergeFileStream = new FileInputStream(mergeFile);

        ) {
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }

        }

    }
    @Test
    void io_test(){
        File sourceFile = new File("E:\\java\\video\\2.mp4");
        System.out.println(sourceFile.length()/1024*1024);

    }
}
