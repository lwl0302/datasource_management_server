package com.mrray.dev.service;

import com.mrray.dev.entity.domain.DataSource;
import com.mrray.dev.entity.dto.*;
import com.mrray.dev.entity.vo.BaseResourceInfoVo;
import com.mrray.dev.entity.vo.RestResponseBody;

/**
 * Created by ln on 2017/7/24.
 */
public interface DataSourceService {
    RestResponseBody testConnect(BaseResourceInfoVo baseResourceInfo);

    RestResponseBody testTableName(TestTableExsitDto sourceInfo);

    RestResponseBody addDataSource(DataSourceDto dataSource);

    RestResponseBody queryDataSource(DataSourceQueryDto dto);

    RestResponseBody getAllTablesById(String remark,TablesPageQueryDto dto);

    RestResponseBody getAllTablesByInfo(DatabaseDto dataSource);

    RestResponseBody deleteDataSource(String uuid);

    String save(DataSource dataSource);
}
