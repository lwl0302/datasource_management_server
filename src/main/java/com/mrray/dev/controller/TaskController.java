package com.mrray.dev.controller;

import com.mrray.dev.entity.dto.ExtractDto;
import com.mrray.dev.entity.dto.LoadDto;
import com.mrray.dev.entity.vo.RestResponseBody;
import com.mrray.dev.service.TaskService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ln on 2017/7/20.
 */
@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    @ApiOperation("数据抽取")
    @ApiImplicitParam(name = "extractDto", value = "tableName有值代表抽取单个表,tableName未传值代表抽取整个数据库", required = true, dataType = "ExtractDto")
    @PostMapping("/extract_task")
    public RestResponseBody extract(@RequestBody ExtractDto extractDto) {
        return taskService.extract(extractDto);
    }

    @ApiOperation("数据装载")
    @ApiImplicitParam(name = "loadDto", value = "1.extractId代表数据抽取任务id;2.sourceInfo脱敏后表的数据库详细连接信息;" +
            "3.targetInfo目标源信息;3.目标源tableName必填;4.目标源有id只需要传入id与tableName;5.目标源非源管理平台管理的源需要传入数据库详细连接信息;6.若目标源为文件,则只需要传入fileName属性", required = true, dataType = "LoadDto")
    @PostMapping("/load_task")
    public RestResponseBody load(@RequestBody LoadDto loadDto) {
        return taskService.load(loadDto);
        /*RestResponseBody<Map> respBody = new RestResponseBody<Map>();
        Map<String, Long> result = new HashMap<String, Long>();
        result.put("taskId", 123L);
        respBody.setData(result);
        return respBody;*/
    }

    @ApiOperation("查询任务进度")
    @ApiImplicitParam(name = "id", value = "任务id", required = true, dataType = "String", paramType = "path")
    @GetMapping("/task/status/{id}")
    public RestResponseBody status(@PathVariable String id) {
        RestResponseBody<Map> respBody = new RestResponseBody<Map>();
        Map<String, String> result = new HashMap<String, String>();
        result.put("status", "doing");
        respBody.setData(result);
        return respBody;
    }


    @ApiOperation("删除抽取任务,所创建的新增表")
    @ApiImplicitParam(name = "id", value = "任务id", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/task/table/{id}")
    public RestResponseBody deleteTable(@PathVariable String id) {
        return taskService.deleteTable(id);
    }

}
