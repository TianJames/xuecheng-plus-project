package com.xuecheng.media.api;

import com.james.base.model.RestResponse;

import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Api(value = "大文件上传接口",tags = "大文件上传接口")
@RestController
public class BigFilesController {
    @Autowired
    private MediaFileService mediaFileService;
    /**
     * 检查文件是否存在于数据库中
     * @param fileMd5
     * @return
     */
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkfile(@RequestParam("fileMd5") String fileMd5){
        return mediaFileService.checkFile(fileMd5);
    }

    /**
     * 分块文件上传前的检测
     * @param fileMd5
     * @param chunk
     * @return
     */
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5,@RequestParam("chunk") int chunk){
        return mediaFileService.checkChunk(fileMd5,chunk);
    }

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {
        File tempFile = File.createTempFile("video", "temp");
        file.transferTo(tempFile);
        String absolutePath = tempFile.getAbsolutePath();
        RestResponse response = mediaFileService.uploadchunk(chunk, fileMd5, absolutePath);
        return response;
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse<Boolean> mergechunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(fileName);
        uploadFileParamsDto.setTags("视频文件");
        uploadFileParamsDto.setFileType("001002");
        RestResponse restResponse = mediaFileService.mergeChunks(companyId, fileMd5, chunkTotal, uploadFileParamsDto);
        return restResponse;

    }


}
