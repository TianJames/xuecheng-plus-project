package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.james.base.model.PageParms;
import com.james.base.model.PageResult;
import com.james.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MinioClient minioClient;
    @Value("${minio.bucket.files}")
    private String bucketFiles;

    @Value("${minio.bucket.videofiles}")
    private String bucketVideoFiles;

    @Autowired
    MediaFileService currentProxy;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParms pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        //上传到minio   yyyy/MM/ss/MD5.extension
        String filename = uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        //根据扩展名拿到mimeType
        String mimeType = getMimeType(extension);
        //设置保存到minio时的文件地址
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String folder = sdf.format(new Date());
        //获取文件的md5

        String fileMd5 = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(localFilePath));
            fileMd5 = DigestUtils.md5DigestAsHex(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String objectName = folder + fileMd5 + extension;
        //保存到minio
        boolean b = addMediaFilesToMinIO(localFilePath, objectName, mimeType, bucketFiles);
        //将信息保存到数据库
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucketFiles, objectName);
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    private static String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucketFiles + "/" + objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                log.error("保存文件信息到数据库失败");
            }
        }
        return mediaFiles;
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        //查询文件信息
        // 首先查询数据库中是否存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //数据库中存在，在查询minio中是否存在
            String bucket = mediaFiles.getBucket();
            String filePath = mediaFiles.getFilePath();
            InputStream stream = null;
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .build();
            try {
                stream = minioClient.getObject(getObjectArgs);
                if (stream != null) {
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            try {
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        //通过fileMd5值拿到分块文件在minio中保存的地址
        String chunkFilePath = getChunkFilePath(fileMd5);
        //查询当前chunk的分块文件是否存在于文件夹中
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucketVideoFiles)
                .object(chunkFilePath + chunkIndex)
                .build();
        InputStream stream = null;
        try {
            stream = minioClient.getObject(getObjectArgs);
            if (stream != null) {
                //分块已经存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //分块不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse uploadchunk(int chunkIndex, String fileMd5, String localFilePath) {
        //将分块文件上传到minio中
        String chunkFileFolderPath = getChunkFilePath(fileMd5);
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        String mimeType = getMimeType(null);

        try {
            boolean b = addMediaFilesToMinIO(localFilePath, chunkFilePath, mimeType, bucketVideoFiles);
            if (b) {
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return RestResponse.validfail(false, "上传文件失败");
    }

    @Override
    public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        //通过md5找到分块文件的地址，进行合并
        String chunkFileFolderPath = getChunkFilePath(fileMd5);
        List<ComposeSource> sourceList = Stream.iterate(0, i -> ++i).limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucketVideoFiles)
                        .object(chunkFileFolderPath + i)
                        .build()).collect(Collectors.toList());
        String filename = uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        String mergetFilePath = getFilePathByMd5(fileMd5, extension);
        //合并文件
        try {
            ObjectWriteResponse response = minioClient.composeObject(ComposeObjectArgs.builder()
                    .bucket(bucketVideoFiles)
                    .object(mergetFilePath)
                    .sources(sourceList)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("合并文件失败，fileMd5:{},", fileMd5);
            return RestResponse.validfail(false, "合并文件异常");
        }
        //验证md5
        File file = downloadFileFromMinio(bucketVideoFiles, mergetFilePath);
        try (InputStream stream = new FileInputStream(file)) {
            String mergeFileMd5 = DigestUtils.md5DigestAsHex(stream);
            if (!fileMd5.equals(mergeFileMd5)) {
                log.error("");
                return RestResponse.validfail(false, "文件校验失败");
            }
            uploadFileParamsDto.setFileSize(file.length());
        } catch (Exception e) {
            e.printStackTrace();
            return RestResponse.validfail(false, "文件校验失败");
        }
        //文件入库
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucketVideoFiles, mergetFilePath);
        if (mediaFiles == null) {
            return RestResponse.validfail(false, "文件入库失败");
        }
        //清除分块文件
        clearChunkFiles(chunkFileFolderPath, chunkTotal);
        return RestResponse.success(true);
    }

    private File downloadFileFromMinio(String bucketVideoFiles, String mergetFilePath) {
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketVideoFiles)
                    .object(mergetFilePath)
                    .build());
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    private void clearChunkFiles(String chunkFilePath, int chunkTotal) {
        try {
            Iterable<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i).limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFilePath.concat(Integer.toString(i)))).collect(Collectors.toList());
            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs
                    .builder()
                    .bucket(bucketVideoFiles)
                    .objects(deleteObjects)
                    .build();
            Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(item -> {
                try {
                    item.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("清除分块文件失败");
        }
    }

    private String getFilePathByMd5(String fileMd5, String extension) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + extension;
    }

    private String getChunkFilePath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    public boolean addMediaFilesToMinIO(String localFilePath, String objectName, String mimeType, String bucket) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("保存文件成功");
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
