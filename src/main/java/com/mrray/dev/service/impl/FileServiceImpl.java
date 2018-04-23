package com.mrray.dev.service.impl;

import com.mrray.dev.entity.domain.DataSource;
import com.mrray.dev.entity.domain.Task;
import com.mrray.dev.entity.vo.RestResponseBody;
import com.mrray.dev.respository.DataSourceRepository;
import com.mrray.dev.respository.TaskRepository;
import com.mrray.dev.service.DataSourceService;
import com.mrray.dev.service.FileService;
import com.mrray.dev.utils.SysUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ln on 2017/7/27.
 */
@Service
public class FileServiceImpl implements FileService {
    @Value("${file.path.upload}")
    private String uploadPath;

    @Value("${file.path.desens}")
    private String desensPath;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DataSourceService dataSourceService;

    @Override
    public RestResponseBody upload(MultipartFile file) {
        RestResponseBody response = new RestResponseBody();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String date = sdf.format(new Date());
        String oldName = file.getOriginalFilename();
        String suffixName = oldName.substring(oldName.lastIndexOf(".") + 1);
        List<String> fileTypes = new ArrayList<>();
        fileTypes.add("xls");
        fileTypes.add("xlsx");
        fileTypes.add("csv");
        fileTypes.add("txt");
        if (suffixName.equals(oldName) || !fileTypes.contains(suffixName.toLowerCase())) {
            response.setMessage("FAIL");
            response.setError("不支持此文件类型!");
            return response;
        }
        File localFile = new File(uploadPath + date + UUID.randomUUID().toString() + "." + suffixName);
        while (localFile.exists()) {
            localFile = new File(uploadPath + date + UUID.randomUUID().toString() + "." + suffixName);
        }
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }
        InputStream input = null;
        FileOutputStream output = null;
        try {
            boolean createFlag = localFile.createNewFile();
            if (!createFlag) {
                response.setMessage("FAIL");
                response.setError("文件上传失败,请重试!");
                return response;
            }
            input = file.getInputStream();
            output = new FileOutputStream(localFile);
            //FileUtils.writeByteArrayToFile(localFile, file.getBytes());
            IOUtils.copy(input, output);
        } catch (IOException e) {
            SysUtils.getLog(this.getClass()).error(e);
            response.setMessage("FAIL");
            response.setError("服务器错误,请联系管理员!");
            return response;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                SysUtils.getLog(this.getClass()).error(e);
            }
        }
        DataSource dataSource = new DataSource();
        dataSource.setSourceType("import");
        dataSource.setTemporary(true);
        dataSource.setFileName(oldName);
        dataSource.setFilePath(localFile.getPath());
        dataSource.setDbType(suffixName);
        Map<String, String> result = new HashMap<String, String>();
        result.put("resourceId", dataSourceService.save(dataSource));
        response.setData(result);
        return response;
    }

    @Override
    public void download(String id, HttpServletResponse httpServletResponse) {
        Task task = taskRepository.findByRemark(id);
        if (task == null || 1 != task.getType() || StringUtils.isEmpty(task.getTargetFilePath()) || !"success".equals(task.getStatus())) {
            return;
        }
        File file = new File(task.getTargetFilePath());
        if (!file.exists()) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());
        httpServletResponse.setHeader("content-type", "application/octet-stream");
        httpServletResponse.setContentType("application/octet-stream");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + date + "." + task.getTargetType());
        FileInputStream inputStream = null;
        ServletOutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(file);
            outputStream = httpServletResponse.getOutputStream();
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            SysUtils.getLog(this.getClass()).error(e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                SysUtils.getLog(this.getClass()).error(e);
            }

        }
    }
}
