package com.mrray.dev.controller;

import com.mrray.dev.entity.dto.*;
import com.mrray.dev.entity.vo.RestResponseBody;
import com.mrray.dev.service.DataSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Created by ln on 2017/7/19.
 */
@Api(value = "datasource", description = "源管理", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/datasource")
public class DataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    @ApiOperation("测试脱敏源,目的源数据库是否能连接")
    @ApiImplicitParam(name = "database", value = "测试源连接", required = true, dataType = "DatabaseDto")
    @PostMapping("/test_db_connect")
    public RestResponseBody testDataSource(@Valid @RequestBody DatabaseDto database) {
        return dataSourceService.testConnect(database);
    }

    @ApiOperation(value = "测试目的源的表名是否存在", notes = "若存在,不允许执行脱敏任务,保证每次装载的数据库由系统自动创建,用户只给定目标表名")
    @ApiImplicitParam(name = "testTableExsitDto", value = "测试目标表详细信息,tableName必填,已有源只需要填源id,临时源需要填写数据库详细信息", required = true, dataType = "TestTableExsitDto")
    @PostMapping("/test/target")
    public RestResponseBody testTarget(@RequestBody TestTableExsitDto testTableExsitDto) {
        return dataSourceService.testTableName(testTableExsitDto);
    }


    @ApiOperation("注册脱敏源,目的源数据库连接信息")
    @PostMapping("/create")
    public RestResponseBody addDataSource(@Valid @RequestBody DataSourceDto dataSource) {
        return dataSourceService.addDataSource(dataSource);
    }

    /*@ApiOperation("更新源连接信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "源id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "dataSource", value = "修改后的源连接信息", required = true, dataType = "DataSourceDto")
    })
    @PostMapping("/update/{id}")
    //未规划此功能,暂时不做
    public RestResponseBody updateDataSource(@PathVariable Long id, @RequestBody DataSourceDto dataSource) {
        return new RestResponseBody();
    }*/

    @ApiOperation("删除源")
    @ApiImplicitParam(name = "id", value = "源id", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/{id}")
    public RestResponseBody deleteDataSource(@PathVariable String id) {
        return dataSourceService.deleteDataSource(id);
    }


    @ApiOperation("根据源id获取数据库所有表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "源id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "dto", value = "分页信息", dataType = "TablesPageQueryDto", paramType = "query")
    })
    @GetMapping("/tables/{id}")
    public RestResponseBody getAllTablesById(@PathVariable String id, @Valid TablesPageQueryDto dto) {
        return dataSourceService.getAllTablesById(id, dto);
    }

    @ApiOperation("根据数据库连接信息获取数据库所有表")
    @ApiImplicitParam(name = "dataSource", value = "源连接信息", dataType = "DatabaseDto")
    @PostMapping("/tables")
    public RestResponseBody getAllTablesByInfo(@RequestBody DatabaseDto dataSource) {
        return dataSourceService.getAllTablesByInfo(dataSource);
    }

    @ApiOperation("获取所有脱敏源或者目标源")
    @ApiImplicitParam(name = "dto", value = "query代表搜索的ip或者数据库,sourceType:'import'代表脱敏源,'export'代表目标源", dataType = "DataSourceQueryDto", paramType = "query")
    @GetMapping
    public RestResponseBody queryDataSource(@Valid DataSourceQueryDto dto) {
        return dataSourceService.queryDataSource(dto);
    }


}
