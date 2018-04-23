package com.mrray.dev.respository;

import com.mrray.dev.entity.domain.DataSource;

/**
 * Created by ln on 2017/7/24.
 */
public interface DataSourceRepository extends BaseRepository<DataSource> {
    DataSource findByRemark(String remark);
}
