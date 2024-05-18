package com.xuecheng.media;

import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MinioTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://127.0.0.1:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    @Test
    void test_upload() throws Exception {
        UploadObjectArgs testbucket = UploadObjectArgs.builder()
                .bucket("testbucket")
                .filename("E:\\c\\data_structure\\main.c")
                .object("/main.c")
                .build();
        minioClient.uploadObject(testbucket);

    }

    @Test
        //文件分块上传
    void upload_test() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (int i = 0; i < 20; i++) {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")
                    .filename("E:\\java\\video\\chunk\\" + i)
                    .object("chunk/" + i)
                    .build();
            minioClient.uploadObject(testbucket);
        }

    }

    //分块合并
    @Test
    void merge_test() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //参与合并的分块文件
        List<ComposeSource> sourceList = Stream.iterate(0, i -> ++i).limit(20)
                .map(i -> ComposeSource.builder()
                        .bucket("testbucket")
                        .object("chunk/" + i)
                        .build()).collect(Collectors.toList());


        //合并之后的信息
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("testbucket")
                .object("merge1.mp4")
                .sources(sourceList)
                .build();
        minioClient.composeObject(composeObjectArgs);
    }
}
