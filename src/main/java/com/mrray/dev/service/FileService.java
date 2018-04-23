package com.mrray.dev.service;

import com.mrray.dev.entity.vo.RestResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by ln on 2017/7/27.
 */
public interface FileService {
    RestResponseBody upload(MultipartFile file);

    void download(String id, HttpServletResponse httpServletResponse);
}
