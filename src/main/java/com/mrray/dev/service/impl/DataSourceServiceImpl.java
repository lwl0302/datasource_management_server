package com.mrray.dev.service.impl;

import com.mrray.dev.entity.domain.DataSource;
import com.mrray.dev.entity.domain.Task;
import com.mrray.dev.entity.dto.*;
import com.mrray.dev.entity.vo.*;
import com.mrray.dev.respository.DataSourceRepository;
import com.mrray.dev.respository.TaskRepository;
import com.mrray.dev.service.DataSourceService;
import com.mrray.dev.utils.DatabaseUtil;
import com.mrray.dev.utils.SysUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by ln on 2017/7/24.
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Value("${extract.database.ip}")
    private String extractDatabaseIp;

    @Value("${extract.database.port}")
    private Integer extractDatabasePort;

    @Value("${extract.database.name}")
    private String extractDatabaseName;

    @Value("${extract.database.type}")
    private String extractDatabaseType;


    @Override
    public RestResponseBody testConnect(BaseResourceInfoVo baseResourceInfo) {
        DatabaseMeta databaseMeta = DatabaseUtil.getDatabaseMeta(baseResourceInfo, "test");
        RestResponseBody response = new RestResponseBody();
        if (databaseMeta == null) {
            response.setMessage("FAIL");
            response.setData("无法连接,请确认录入信息正确,且数据库打开!");
            return response;
        }
        Database databae = new Database(null, databaseMeta);
        try {
            databae.connect();
        } catch (KettleDatabaseException e) {
            response.setMessage("FAIL");
            response.setData("无法连接,请确认录入信息正确,且数据库打开!");
        } finally {
            databae.disconnect();
        }
        return response;
    }

    @Override
    public RestResponseBody testTableName(TestTableExsitDto sourceInfo) {
        RestResponseBody response = new RestResponseBody();
        if (StringUtils.isEmpty(sourceInfo.getTableName())) {
            response.setMessage("FAIL");
            response.setData("请输入需要测试的表名!");
            return response;
        }
        if (!StringUtils.isEmpty(sourceInfo.getId())) {
            DataSource dataSource = dataSourceRepository.findByRemark(sourceInfo.getId());
            if (dataSource == null) {
                response.setMessage("FAIL");
                response.setData("没有找到指定源!");
                return response;
            }
            BeanUtils.copyProperties(dataSource, sourceInfo);
        }
        DatabaseMeta databaseMeta = DatabaseUtil.getDatabaseMeta(sourceInfo, "test");
        if (databaseMeta == null) {
            response.setMessage("FAIL");
            response.setData("无法连接,请确认录入信息正确,且数据库打开!");
            return response;
        }
        Database database = new Database(null, databaseMeta);
        try {
            database.connect();
            String[] tables = database.getTablenames();
            for (String table : tables) {
                if (table.equalsIgnoreCase(sourceInfo.getTableName())) {
                    response.setMessage("FAIL");
                    response.setData("此表名已存在,请重新命名!");
                    return response;
                }
            }
        } catch (Exception e) {
            response.setMessage("FAIL");
            response.setData("无法连接,请确认录入信息正确,且数据库打开!");
            return response;
        } finally {
            database.disconnect();
        }
        return response;
    }

    @Override
    public RestResponseBody addDataSource(DataSourceDto dataSource) {
        RestResponseBody response = new RestResponseBody();
        if (!"import".equals(dataSource.getSourceType()) && !"export".equals(dataSource.getSourceType())) {
            response.setMessage("FAIL");
            response.setData("sourceType只能为import或者export!");
            return response;
        }
        DatabaseMeta databaseMeta = DatabaseUtil.getDatabaseMeta(dataSource, "test");
        if (databaseMeta == null) {
            response.setMessage("FAIL");
            response.setData("无法连接,请确认录入信息正确,且数据库打开!");
            return response;
        }
        Database databae = new Database(null, databaseMeta);
        try {
            databae.connect();
        } catch (Exception e) {
            response.setMessage("FAIL");
            response.setData("无法连接,请确认录入信息正确,且数据库打开!");
            return response;
        } finally {
            databae.disconnect();
        }
        DataSource dataBean = new DataSource();
        dataBean.setDbType(dataSource.getDbType());
        dataBean.setSourceType(dataSource.getSourceType());
        dataBean.setTemporary(dataSource.isTemporary());
        dataBean.setIp(dataSource.getIp());
        dataBean.setPort(dataSource.getPort());
        dataBean.setDatabaseName(dataSource.getDatabaseName());
        dataBean.setUsername(dataSource.getUsername());
        dataBean.setPassword(dataSource.getPassword());
        dataBean.setComment(dataSource.getComment());
        Map<String, String> result = new HashMap<String, String>();
        result.put("resourceId", save(dataBean));
        response.setData(result);
        return response;
    }

    @Override
    public RestResponseBody queryDataSource(DataSourceQueryDto dto) {
        PageQueryVo<DataBaseVo> pageVo = new PageQueryVo<>();
        Pageable page = new PageRequest(dto.getPage() - 1, dto.getSize(), Sort.Direction.fromString(dto.getDirection()), dto.getProperty());
        Page<DataSource> dataSourcePage = dataSourceRepository.findAll((Root<DataSource> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = root.isNotNull();
            Predicate accountPredicate = cb.equal(root.get("sourceType").as(String.class), dto.getSourceType());
            predicate = cb.and(predicate, accountPredicate);
            accountPredicate = cb.equal(root.get("temporary").as(boolean.class), false);
            predicate = cb.and(predicate, accountPredicate);
            if (!StringUtils.isEmpty(dto.getQuery())) {
                Predicate predicate2 = root.isNotNull();
                accountPredicate = cb.like(root.get("databaseName").as(String.class), "%" + dto.getQuery() + "%");
                predicate2 = cb.and(predicate2, accountPredicate);
                accountPredicate = cb.equal(root.get("ip").as(String.class), dto.getQuery());
                predicate2 = cb.or(predicate2, accountPredicate);
                predicate = cb.and(predicate, predicate2);
            }
            return predicate;
        }, page);
        SysUtils.mapperPageInfoToVo(dto, dataSourcePage, pageVo);
        List<DataBaseVo> content = pageVo.getContent();
        dataSourcePage.forEach(src -> {
            DataBaseVo dist = new DataBaseVo();
            BeanUtils.copyProperties(src, dist);
            dist.setId(src.getRemark());
            content.add(dist);
        });
        if ("import".equals(dto.getSourceType())) {
            List<Long> dataSourceIds = new ArrayList<Long>();
            for (DataSource database : dataSourcePage) {
                dataSourceIds.add(database.getId());
            }
            List<Task> taskList = taskRepository.findBySourceIdInAndTargetIpAndTargetPortAndTargetDatabaseNameAndTypeAndStatus(dataSourceIds, extractDatabaseIp, extractDatabasePort, extractDatabaseName, 2, "success");
            ExtractInfoVo extractInfo;
            Set<String> tables;
            String targetTableName;
            String[] tableNames;
            Task newestTask = null;
            for (DataBaseVo database : content) {
                for (DataSource data : dataSourcePage) {
                    if (database.getId().equals(data.getRemark())) {
                        newestTask = findNewestTaskBySoureId(taskList, data.getId());
                    }
                }
                if (newestTask == null) {
                    database.setExtractInfo(null);
                    continue;
                }
                extractInfo = database.getExtractInfo();
                tables = extractInfo.getTables();
                targetTableName = newestTask.getTargetTableName();
                if (!StringUtils.isEmpty(targetTableName)) {
                    tableNames = targetTableName.split(",");
                    for (String tableName : tableNames) {
                        if (!StringUtils.isEmpty(tableName)) {
                            tables.add(tableName);
                        }
                    }
                }
                if (tables.size() > 0) {
                    extractInfo.setIp(extractDatabaseIp);
                    extractInfo.setPort(extractDatabasePort);
                    extractInfo.setDatabaseName(extractDatabaseName);
                } else {
                    database.setExtractInfo(null);
                }
            }
        }
        RestResponseBody respose = new RestResponseBody();
        respose.setData(pageVo);
        return respose;
    }

    @Override
    public RestResponseBody getAllTablesById(String remark, TablesPageQueryDto dto) {
        DataSource dataResource = dataSourceRepository.findByRemark(remark);
        RestResponseBody respose = new RestResponseBody();
        if (dataResource == null || !StringUtils.isEmpty(dataResource.getFilePath()) || !"import".equals(dataResource.getSourceType()) || dataResource.isTemporary()) {
            respose.setMessage("FAIL");
            respose.setError("没有找到指定脱敏源!");
            return respose;
        }
        BaseResourceInfoVo baseResourceInfo = new BaseResourceInfoVo();
        BeanUtils.copyProperties(dataResource, baseResourceInfo);
        DatabaseMeta databaseMeta = DatabaseUtil.getDatabaseMeta(baseResourceInfo, "test");
        if (databaseMeta == null) {
            respose.setMessage("FAIL");
            respose.setData("指定数据库没有开启,或者账号密码过期导致不能连接,请查证!");
            return respose;
        }
        Database database = new Database(null, databaseMeta);
        String[] tableNames;
        try {
            database.connect();
            tableNames = database.getTablenames();
        } catch (KettleDatabaseException e) {
            respose.setMessage("FAIL");
            respose.setError("指定数据库没有开启,或者账号密码过期导致不能连接,请查证!");
            return respose;
        } finally {
            database.disconnect();
        }

        List<String> tableList = new ArrayList<String>();
        for (String tableName : tableNames) {
            if (!StringUtils.isEmpty(dto.getTableName())) {
                if (tableName.toLowerCase().contains(dto.getTableName().toLowerCase())) {
                    tableList.add(tableName);
                }
            } else {
                tableList.add(tableName);
            }
        }

        int pageCount = (tableList.size() + dto.getSize() - 1) / dto.getSize();
        int startIndex = (dto.getPage() - 1) * dto.getSize();
        int endIndex = startIndex + dto.getSize() - 1;
        int currentPageElements;
        List<Map> tableMapList = null;
        if (!CollectionUtils.isEmpty(tableList)) {
            if (tableList.size() - 1 < startIndex) {
                currentPageElements = 0;
            } else {
                if (tableList.size() - 1 < endIndex) {
                    endIndex = tableList.size() - 1;
                }
                currentPageElements = endIndex - startIndex + 1;
                tableMapList = new ArrayList<Map>();
                Map temp;
                for (int i = startIndex; i <= endIndex; i++) {
                    temp = new HashMap<String, String>();
                    temp.put("tableName", tableList.get(i));
                    tableMapList.add(temp);
                }

            }
        } else {
            currentPageElements = 0;
        }


        TablePageQueryVo pagequery = new TablePageQueryVo();
        pagequery.setTotalElements(tableList.size());
        pagequery.setSize(dto.getSize());
        pagequery.setPage(dto.getPage());
        pagequery.setTotalPage(pageCount);
        pagequery.setFirstPage(1 == dto.getPage());
        pagequery.setLastPage(dto.getPage() >= pageCount);
        pagequery.setPrevPage(dto.getPage() == 1 ? 1 : dto.getPage() - 1);
        pagequery.setNextPage(dto.getPage() >= pageCount ? pageCount : dto.getPage() + 1);
        pagequery.setCurrentPageElements(currentPageElements);

        pagequery.setId(remark);
        pagequery.setIp(extractDatabaseIp);
        pagequery.setPort(extractDatabasePort);
        pagequery.setDatabaseName(extractDatabaseName);
        pagequery.setType(extractDatabaseType);
        pagequery.setContent(tableMapList);

        if (!CollectionUtils.isEmpty(tableMapList)) {
            List<Task> taskList = taskRepository.findBySourceIdAndStatusAndTypeInAndTargetIpAndTargetPortAndTargetDatabaseName(dataResource.getId(), "success", new Integer[]{0, 2}, extractDatabaseIp, extractDatabasePort, extractDatabaseName);
            List<Task> scanTaskList = new ArrayList<Task>();
            for (Task task : taskList) {
                if (2 == task.getType()) {
                    scanTaskList.add(task);
                }
            }
            Task newestScanTask = findNewestTaskBySoureId(scanTaskList, dataResource.getId());
            if (newestScanTask != null) {
                if (!StringUtils.isEmpty(newestScanTask.getSourceTableName()) && !StringUtils.isEmpty(newestScanTask.getTargetTableName())) {
                    String[] sourceTables = newestScanTask.getSourceTableName().split(",");
                    String[] targetTables = newestScanTask.getTargetTableName().split(",");
                    String sourceTable;
                    for (Map tableMap : tableMapList) {
                        for (int i = 0; i < sourceTables.length; i++) {
                            if (tableMap.get("tableName").equals(sourceTables[i])) {
                                tableMap.put("extractTableName", targetTables[i]);
                            }
                        }
                    }
                }
                for (Task task : taskList) {
                    if (0 == task.getType() && task.getCreateTime().after(newestScanTask.getCreateTime())) {
                        for (Map tableMap : tableMapList) {
                            if (tableMap.get("tableName").equals(task.getSourceTableName())) {
                                tableMap.put("extractTableName", task.getTargetTableName());
                            }
                        }
                    }
                }
            } else {
                for (Task task : taskList) {
                    for (Map tableMap : tableMapList) {
                        if (tableMap.get("tableName").equals(task.getSourceTableName())) {
                            tableMap.put("extractTableName", task.getTargetTableName());
                        }
                    }
                }
            }
        }
        respose.setData(pagequery);
        return respose;
    }

    @Override
    public RestResponseBody getAllTablesByInfo(DatabaseDto dataSource) {
        DatabaseMeta databaseMeta = DatabaseUtil.getDatabaseMeta(dataSource, "test");
        RestResponseBody response = new RestResponseBody();
        if (databaseMeta == null) {
            response.setMessage("FAIL");
            response.setData("无法连接,请确认录入信息正确,且数据库打开!");
            return response;
        }
        Database database = new Database(null, databaseMeta);
        String tableNames[];
        try {
            database.connect();
            tableNames = database.getTablenames();
        } catch (KettleDatabaseException e) {
            response.setMessage("FAIL");
            response.setData("无法连接,请确认录入信息正确,且数据库打开!");
            return response;
        } finally {
            database.disconnect();
        }
        ExtractInfoVo tablesInfo = new ExtractInfoVo();
        tablesInfo.setIp(dataSource.getIp());
        tablesInfo.setPort(dataSource.getPort());
        tablesInfo.setDatabaseName(dataSource.getDatabaseName());
        tablesInfo.setDbType(dataSource.getDbType());
        tablesInfo.setUsername(dataSource.getUsername());
        tablesInfo.setPassword(dataSource.getPassword());
        Set<String> tables = new HashSet<>();
        if (tableNames != null && tableNames.length != 0) {
            for (String tableName : tableNames) {
                tables.add(tableName);
            }
        }
        tablesInfo.setTables(tables);
        response.setData(tablesInfo);
        return response;
    }

    @Override
    public RestResponseBody deleteDataSource(String uuid) {
        RestResponseBody resp = new RestResponseBody();
        try {
            dataSourceRepository.delete(dataSourceRepository.findByRemark(uuid));
        } catch (Exception e) {
            resp.setMessage("FAIL");
            resp.setError("指定源不存在!");
        }
        return resp;
    }

    private Task findNewestTaskBySoureId(List<Task> taskList, Long id) {
        Task newestTask = null;
        for (Task task : taskList) {
            if (id == task.getSourceId() && task.getCreateTime() != null) {
                if (newestTask == null) {
                    newestTask = task;
                } else {
                    if (task.getCreateTime().after(newestTask.getCreateTime())) {
                        newestTask = task;
                    }
                }
            }
        }
        return newestTask;
    }

    @Override
    public String save(DataSource dataSource) {
        dataSource.setRemark(RandomStringUtils.random(8, true, true).toLowerCase());
        try {
            dataSourceRepository.save(dataSource);
        } catch (DataIntegrityViolationException e) {
            dataSource.setRemark(RandomStringUtils.random(8, true, true).toLowerCase());
            save(dataSource);
        }
        return dataSource.getRemark();
    }
}
