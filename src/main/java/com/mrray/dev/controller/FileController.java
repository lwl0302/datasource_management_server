package com.mrray.dev.controller;

import com.mrray.dev.entity.vo.RestResponseBody;
import com.mrray.dev.service.impl.FileServiceImpl;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by ln on 2017/7/21.
 */
@RequestMapping("/file")
@Controller
public class FileController {

    @Autowired
    private FileServiceImpl fileService;

    @ApiOperation(value = "文件上传",consumes ="multipart/form-data" )
    @ApiParam(name = "file", value = "需要脱敏的文件",type = "file")
    @PostMapping(value = "/upload")
    @ResponseBody
    public RestResponseBody upload(@RequestParam("file") MultipartFile file) {
        return fileService.upload(file);
    }

    @ApiOperation("文件下载")
    @ApiImplicitParam(name = "id", value = "数据装载任务id", required = true, dataType = "String", paramType = "path")
    @GetMapping(value = "/download/{id}")
    public void download(@PathVariable String id, HttpServletResponse httpServletResponse) {
        fileService.download(id,httpServletResponse);
    }


}
