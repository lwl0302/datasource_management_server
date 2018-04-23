package com.mrray.dev.respository;

import com.mrray.dev.entity.domain.Task;

import java.util.List;

/**
 * Created by ln on 2017/7/25.
 */
public interface TaskRepository extends BaseRepository<Task> {
    List<Task> findBySourceIdInAndTargetIpAndTargetPortAndTargetDatabaseNameAndTypeAndStatus(List<Long> sourceIds, String targetIp, Integer targetPort, String targetDatabaseName, Integer type, String status);

    List<Task> findBySourceIdAndStatusAndTypeInAndTargetIpAndTargetPortAndTargetDatabaseName(Long sourceId, String status, Integer[] types, String targetIp, Integer targetPort, String targetDatabaseName);

    Task findByRemark(String remark);
}
