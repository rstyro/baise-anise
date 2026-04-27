package com.lrs.core.system.controller;

import cn.hutool.core.io.FileUtil;
import com.lrs.common.enums.ApiResultEnum;
import com.lrs.common.enums.UploadImageType;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.utils.ImgUtil;
import com.lrs.common.vo.R;
import com.lrs.core.config.CommonConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Resource
    private CommonConfig.UploadConfig uploadConfig;

    /**
     * 上传图片
     * @param file 文件
     */
    @PostMapping(value = {"/image"})
    public R<String> imgUpload(@RequestParam(value = "file") MultipartFile file,@RequestParam(required = false) String type){
        String url = uploadFile(file, UploadImageType.matchName(type));
        return R.ok(url);
    }

    private String uploadFile(MultipartFile file, UploadImageType imageType){
        if (file.isEmpty()) {
            throw new ServiceException(ApiResultEnum.ERROR_IO,null);
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        String suffixName = Objects.requireNonNull(fileName).substring(fileName.lastIndexOf("."));
        String folder = "/"+ imageType.getName()+"/";
        fileName  = System.currentTimeMillis()+suffixName;
        File dest = ImgUtil.createFile(uploadConfig.getRoot()+folder+fileName);
        try {
            file.transferTo(Objects.requireNonNull(dest));
        }catch (IOException e) {
            log.error(e.getMessage(),e);
            throw new ServiceException(ApiResultEnum.ERROR_IO);
        }
        return "/show"+folder+fileName;
    }

    @PostMapping(value = {"/tinyImage","tinyFile"})
    @ResponseBody
    public Object tinyImage(@RequestParam(value = "file") MultipartFile file){
        String url = uploadFile(file,UploadImageType.FILE);
        Map<String,String> data = new HashMap<>();
        // tinymce上传文件必须要返回location这样的格式
        data.put("location",url);
        return data;
    }
}
