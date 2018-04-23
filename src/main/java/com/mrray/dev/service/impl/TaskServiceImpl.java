package com.mrray.dev.service.impl;

import com.mrray.dev.entity.domain.DataSource;
import com.mrray.dev.entity.domain.Task;
import com.mrray.dev.entity.dto.ExtractDto;
import com.mrray.dev.entity.dto.LoadDto;
import com.mrray.dev.entity.dto.SourceInfoDto;
import com.mrray.dev.entity.dto.TargetInfoDto;
import com.mrray.dev.entity.vo.EtlVo;
import com.mrray.dev.entity.vo.ExtractRespVo;
import com.mrray.dev.entity.vo.LoadRespVo;
import com.mrray.dev.entity.vo.RestResponseBody;
import com.mrray.dev.feign.TaskManagerFeign;
import com.mrray.dev.respository.DataSourceRepository;
import com.mrray.dev.respository.TaskRepository;
import com.mrray.dev.service.TaskService;
import com.mrray.dev.utils.DatabaseUtil;
import com.mrray.dev.utils.SysUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.di.trans.steps.tableoutput.TableOutputMeta;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ln on 2017/8/1.
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Value("${extract.database.ip}")
    private String extractDatabaseIp;

    @Value("${extract.database.port}")
    private Integer extractDatabasePort;

    @Value("${extract.database.name}")
    private String extractDatabaseName;

    @Value("${extract.database.type}")
    private String extractDatabaseType;

    @Value("${extract.database.username}")
    private String extractDatabaseUsername;

    @Value("${extract.database.password}")
    private String extractDatabasePassword;

    @Autowired
    private TaskManagerFeign taskManagerFeign;

    @Override
    public RestResponseBody extract(ExtractDto extractDto) {
        RestResponseBody respBody = new RestResponseBody();
        //DataSource dataSource = dataSourceRepository.findByRemark(extractDto.getId());
        Task task = new Task();
        task.setStatus("doing");
        //task.setSourceId(dataSource.getId());
        Map<String, String> result = new HashMap<String, String>();
        result.put("taskId", save(task));
        Thread mainThread = Thread.currentThread();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //等待主线程执行完毕
                    while (!"WAITING".equals(mainThread.getState().name())) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    SysUtils.getLog(this.getClass()).error(e);
                    Thread.currentThread().interrupt();
                }

                DataSource dataSource = dataSourceRepository.findByRemark(extractDto.getId());
                ExtractRespVo extractRespVo = new ExtractRespVo();
                extractRespVo.setIp(extractDatabaseIp);
                extractRespVo.setPort(extractDatabasePort);
                extractRespVo.setDbType(extractDatabaseType);
                extractRespVo.setDatabaseName(extractDatabaseName);
                extractRespVo.setUsername(extractDatabaseUsername);
                extractRespVo.setPassword(extractDatabasePassword);
                extractRespVo.setExtractId(task.getRemark());
                List<String> tableNameList = extractRespVo.getTableNames();
                if (dataSource == null || !"import".equals(dataSource.getSourceType())) {
                    task.setStatus("fail");
                    task.setFailCasuse("没有找到指定源!");
                } else if (StringUtils.isEmpty(dataSource.getFilePath())) {
                    task.setSourceId(dataSource.getId());
                    EtlVo etlSource = new EtlVo();
                    BeanUtils.copyProperties(dataSource, etlSource);
                    etlSource.setTableName(extractDto.getTableName());
                    EtlVo etlTarget = new EtlVo();
                    etlTarget.setIp(extractDatabaseIp);
                    etlTarget.setDatabaseName(extractDatabaseName);
                    etlTarget.setPort(extractDatabasePort);
                    etlTarget.setDbType(extractDatabaseType);
                    etlTarget.setPassword(extractDatabasePassword);
                    etlTarget.setUsername(extractDatabaseUsername);

                    task.setSourceIp(dataSource.getIp());
                    task.setSourcePort(dataSource.getPort());
                    task.setSourceType(dataSource.getDbType());
                    task.setSourceDatabaseName(dataSource.getDatabaseName());
                    task.setTargetIp(extractDatabaseIp);
                    task.setTargetPort(extractDatabasePort);
                    task.setTargetType(extractDatabaseType);
                    task.setTargetDatabaseName(extractDatabaseName);
                    task.setType(!StringUtils.isEmpty(etlSource.getTableName()) ? 0 : 2);

                    if (StringUtils.isEmpty(etlSource.getTableName())) {
                        //etlTrans(task, sourceDatabaseMeta, targetDatabaseMeta);
                        DatabaseMeta databaseMeta = DatabaseUtil.getDatabaseMeta(etlSource, "source");
                        Database database = new Database(null, databaseMeta);
                        String[] tableNames = new String[0];
                        try {
                            database.connect();
                            tableNames = database.getTablenames();
                        } catch (KettleDatabaseException e) {
                            task.setStatus("fail");
                            task.setFailCasuse("脱敏源数据库没有开启,或者账号密码过期导致不能连接,请查证!");
                        } finally {
                            database.disconnect();
                        }
                        for (String tableName : tableNames) {
                            etlSource.setTableName(tableName);
                            setTargetTableName(etlTarget);
                            tableNameList.add(etlTarget.getTableName());
                            etlTrans(task, etlSource, etlTarget);
                            if ("fail".equals(task.getStatus())) {
                                break;
                            }
                        }
                    } else {
                        String checkResult = DatabaseUtil.checkTableExist(etlSource);
                        if (!"success".equals(checkResult)) {
                            task.setStatus("fail");
                            task.setFailCasuse(checkResult);
                        } else {
                            setCreateTableSql(task, etlSource);
                            setTargetTableName(etlTarget);
                            tableNameList.add(etlTarget.getTableName());
                            etlTrans(task, etlSource, etlTarget);
                        }
                    }

                }
                if (!"fail".equals(task.getStatus())) {
                    task.setStatus("success");
                }
                taskRepository.save(task);

                //这里再调任务管理接口,发送任务结果
                RestResponseBody resp = new RestResponseBody();
                resp.setMessage(task.getStatus());
                resp.setError(task.getFailCasuse());
                resp.setData(extractRespVo);
                taskManagerFeign.extract(resp);
            }
        }).start();
        respBody.setData(result);
        return respBody;
    }

    private void setCreateTableSql(Task task, EtlVo etlSource) {
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            if ("mysql".equalsIgnoreCase(etlSource.getDbType())) {
                con = DriverManager.getConnection("jdbc:mysql://" + etlSource.getIp() + ":" + etlSource.getPort() + "/" + etlSource.getDatabaseName() + "?user=" + etlSource.getUsername() + "&password=" + etlSource.getPassword());
                stmt = con.createStatement();
                rs = stmt.executeQuery("show create table " + etlSource.getTableName());
                if (rs.next()) {
                    task.setCreateTableSql(rs.getString(2));
                }
            } else if ("oracle".equalsIgnoreCase(etlSource.getDbType())) {
                con = DriverManager.getConnection("jdbc:oracle:thin:@" + etlSource.getIp() + ":" + etlSource.getPort() + ":" + etlSource.getDatabaseName(), etlSource.getUsername(), etlSource.getPassword());
                stmt = con.createStatement();
                rs = stmt.executeQuery("select SYS.dbms_metadata.get_ddl('TABLE','" + etlSource.getTableName() + "') from dual");
                if (rs.next()) {
                    task.setCreateTableSql(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            SysUtils.getLog(this.getClass()).error(e);
        } finally {
            SysUtils.closeJdbc(con, stmt, rs);
        }
    }


    private void etlTrans(Task task, EtlVo etlSource, EtlVo etlTarget) {

        task.setSourceTableName(StringUtils.isEmpty(task.getSourceTableName()) ? etlSource.getTableName() : task.getSourceTableName() + "," + etlSource.getTableName());
        task.setTargetTableName(StringUtils.isEmpty(task.getTargetTableName()) ? etlTarget.getTableName() : task.getTargetTableName() + "," + etlTarget.getTableName());

        TransMeta transMeta = getTrans(etlSource, etlTarget);
        DatabaseMeta targetDatabaseMeta = transMeta.findDatabase("target");
        Database targetdb = new Database(null, targetDatabaseMeta);
        try {
            String createTableSql = transMeta.getSQLStatementsString();

            if (0 == task.getType() && task.getSourceType().equalsIgnoreCase(task.getTargetType()) && !StringUtils.isEmpty(task.getCreateTableSql())) {
                createTableSql = task.getCreateTableSql().replaceFirst(task.getSourceTableName(), task.getTargetTableName());
            }

            if (1 == task.getType()) {
                if (!StringUtils.isEmpty(task.getCreateTableSql())) {
                    createTableSql = task.getCreateTableSql();
                } else {
                    task.setCreateTableSql(createTableSql);
                }
            }
            if (!StringUtils.isEmpty(createTableSql)) {
                targetdb.connect();
                if ("ORACLE".equalsIgnoreCase(targetDatabaseMeta.getDatabaseInterface().getPluginId()) && createTableSql.endsWith(";")) {
                    createTableSql = createTableSql.substring(0, createTableSql.length() - 1);
                }
                targetdb.execStatement(createTableSql);
                targetdb.disconnect();
            }
            Trans trans = new Trans(transMeta);
            trans.execute(null);
            trans.waitUntilFinished();
        } catch (KettleStepException e) {
            SysUtils.getLog(this.getClass()).error(e);
            task.setStatus("fail");
        } catch (KettleDatabaseException e) {
            SysUtils.getLog(this.getClass()).error(e);
            task.setStatus("fail");
        } catch (KettleException e) {
            SysUtils.getLog(this.getClass()).error(e);
            task.setStatus("fail");
        } finally {
            targetdb.disconnect();
        }
    }

    private void setTargetTableName(EtlVo etlTarget) {
        etlTarget.setTableName(UUID.randomUUID().toString());
        if (!"指定表不存在或已被删除!".equals(DatabaseUtil.checkTableExist(etlTarget))) {
            setTargetTableName(etlTarget);
        }
    }

    private TransMeta getTrans(EtlVo etlSource, EtlVo etlTarget) {
        TransMeta transMeta = new TransMeta();
        DatabaseMeta sourceDatabaseMeta = DatabaseUtil.getDatabaseMeta(etlSource, "source");
        DatabaseMeta targetDatabaseMeta = DatabaseUtil.getDatabaseMeta(etlTarget, "target");
        transMeta.addDatabase(sourceDatabaseMeta);
        transMeta.addDatabase(targetDatabaseMeta);

        PluginRegistry registry = PluginRegistry.getInstance();

        TableInputMeta tableInput = new TableInputMeta();
        tableInput.setDatabaseMeta(sourceDatabaseMeta);
        if ("oracle".equalsIgnoreCase(etlSource.getDbType())) {
            tableInput.setSQL("SELECT * FROM \"" + etlSource.getTableName() + "\"");
        } else if ("mysql".equalsIgnoreCase(etlSource.getDbType())) {
            tableInput.setSQL("SELECT * FROM `" + etlSource.getTableName() + "`");
        } else {
            tableInput.setSQL("SELECT * FROM " + etlSource.getTableName());
        }
        String tableInputPluginId = registry.getPluginId(StepPluginType.class, tableInput);
        StepMeta tableInputMetaStep = new StepMeta(tableInputPluginId, "table input", tableInput);
        tableInputMetaStep.setDraw(true);
        tableInputMetaStep.setLocation(100, 100);
        transMeta.addStep(tableInputMetaStep);

        String tostepname = "write to [" + etlTarget.getTableName() + "]";
        TableOutputMeta toi = new TableOutputMeta();
        toi.setDatabaseMeta(targetDatabaseMeta);
        if ("oracle".equalsIgnoreCase(etlTarget.getDbType())) {
            toi.setTablename("\"" + etlTarget.getTableName() + "\"");
        } else {
            toi.setTablename(etlTarget.getTableName());
        }

        toi.setCommitSize(200);
        toi.setTruncateTable(true);
        String tableOutputPluginId = registry.getPluginId(StepPluginType.class, toi);
        StepMeta tostep = new StepMeta(tableOutputPluginId, tostepname, (StepMetaInterface) toi);
        tostep.setLocation(550, 100);
        tostep.setDraw(true);
        transMeta.addStep(tostep);
        TransHopMeta hi = new TransHopMeta(tableInputMetaStep, tostep);
        transMeta.addTransHop(hi);
        return transMeta;
    }


    @Override
    public String save(Task task) {
        task.setRemark(RandomStringUtils.random(8, true, true).toLowerCase());
        try {
            taskRepository.save(task);
        } catch (DataIntegrityViolationException e) {
            task.setRemark(RandomStringUtils.random(8, true, true).toLowerCase());
            save(task);
        }
        return task.getRemark();
    }

    @Override
    public RestResponseBody load(LoadDto loadDto) {
        RestResponseBody respBody = new RestResponseBody();
        Task task = new Task();
        task.setStatus("doing");
        task.setType(1);
        Map<String, String> result = new HashMap<String, String>();
        result.put("taskId", save(task));
        new Thread(new Runnable() {
            @Override
            public void run() {

                SourceInfoDto sourceInfo = loadDto.getSourceInfo();
                TargetInfoDto targetInfo = loadDto.getTargetInfo();

                if (sourceInfo == null || StringUtils.isEmpty(sourceInfo.getTableName())
                        || StringUtils.isEmpty(sourceInfo.getDatabaseName()) || StringUtils.isEmpty(sourceInfo.getDbType())
                        || StringUtils.isEmpty(sourceInfo.getIp()) || StringUtils.isEmpty(sourceInfo.getPassword())
                        || StringUtils.isEmpty(sourceInfo.getUsername()) || sourceInfo.getPort() == null) {
                    task.setStatus("fail");
                    task.setFailCasuse("需要装载的源数据不全!");
                } else {
                    EtlVo sourceEtl = new EtlVo();
                    BeanUtils.copyProperties(sourceInfo, sourceEtl);
                    String checkTableExist = DatabaseUtil.checkTableExist(sourceEtl);
                    task.setSourceIp(sourceEtl.getIp());
                    task.setSourcePort(sourceEtl.getPort());
                    task.setSourceType(sourceEtl.getDbType());
                    task.setSourceDatabaseName(sourceEtl.getDatabaseName());
                    if (!"success".equals(checkTableExist)) {
                        task.setStatus("fail");
                        task.setFailCasuse(checkTableExist);
                    } else {
                        if (StringUtils.isEmpty(targetInfo.getFileType())) {
                            if (!StringUtils.isEmpty(targetInfo.getId()) && !StringUtils.isEmpty(targetInfo.getTableName())) {
                                DataSource targetData = dataSourceRepository.findByRemark(targetInfo.getId());
                                if (targetData == null) {
                                    task.setStatus("fail");
                                    task.setFailCasuse("目标源不存在,请指定正确目标源!");
                                } else {

                                    task.setTargetId(targetData.getId());
                                    task.setTargetIp(StringUtils.isEmpty(targetData.getIp()) ? null : targetData.getIp());
                                    task.setTargetPort(targetData.getPort() == null ? null : targetData.getPort());
                                    task.setTargetType(StringUtils.isEmpty(targetData.getDbType()) ? null : targetData.getDbType());
                                    task.setTargetDatabaseName(StringUtils.isEmpty(targetData.getDatabaseName()) ? null : targetData.getDatabaseName());

                                    EtlVo targetEtl = new EtlVo();
                                    BeanUtils.copyProperties(targetData, targetEtl);
                                    targetEtl.setTableName(targetInfo.getTableName());
                                    String checkTargetTableResult = DatabaseUtil.checkTableExist(targetEtl);
                                    if ("指定表不存在或已被删除!".equals(checkTargetTableResult)) {
                                        Task extractTask = taskRepository.findByRemark(loadDto.getExtractId());
                                        if (extractTask != null && !StringUtils.isEmpty(extractTask.getCreateTableSql()) && targetEtl.getDbType().equalsIgnoreCase(extractTask.getSourceType())) {
                                            task.setCreateTableSql(extractTask.getCreateTableSql().replaceFirst(extractTask.getSourceTableName(), targetEtl.getTableName()));
                                        }
                                        etlTrans(task, sourceEtl, targetEtl);
                                    } else {
                                        task.setStatus("fail");
                                        task.setFailCasuse("success".equals(checkTargetTableResult) ? "目标表已存在!" : checkTargetTableResult);
                                    }
                                }

                            } else {
                                task.setStatus("fail");
                                task.setFailCasuse("请指定目标源!");
                            }
                            //dataSourceRepository.findByRemark();
                        } else {
                            //装载到文件
                        }
                    }

                }
                if (!"fail".equals(task.getStatus())) {
                    task.setStatus("success");
                }
                taskRepository.save(task);
                RestResponseBody resp = new RestResponseBody();
                resp.setMessage(task.getStatus());
                resp.setError(task.getFailCasuse());
                LoadRespVo loadResp = new LoadRespVo();
                loadResp.setLoadId(task.getRemark());
                resp.setData(loadResp);
                taskManagerFeign.load(resp);
            }
        }).start();

        respBody.setData(result);
        return respBody;
    }

    @Override
    public RestResponseBody deleteTable(String id) {
        RestResponseBody response = new RestResponseBody();
        Task task = taskRepository.findByRemark(id);
        if (task == null) {
            response.setMessage("FAIL");
            response.setError("没有找到指定抽取任务!");
        } else if (!"success".equals(task.getStatus())) {
            response.setMessage("FAIL");
            response.setError("此抽取任务不成功,没有中间表需要删除!");
        } else if (0 != task.getType() && 2 != task.getType()) {
            response.setMessage("FAIL");
            response.setError("此任务不是抽取任务,没有创建中间表!");
        } else {
            String targetTableName = task.getTargetTableName();
            if (!StringUtils.isEmpty(targetTableName)) {
                String[] tableNames = targetTableName.split(",");
                Connection con = null;
                Statement stmt = null;
                try {
                    con = DriverManager.getConnection("jdbc:mysql://" + extractDatabaseIp + ":" + extractDatabasePort + "/" + extractDatabaseName + "?user=" + extractDatabaseUsername + "&password=" + extractDatabasePassword);
                    stmt = con.createStatement();
                    for (String tableName : tableNames) {
                        if (!StringUtils.isEmpty(tableName)) {
                            stmt.execute(String.format("DROP TABLE IF EXISTS `%s`", tableName));
                        }
                    }
                } catch (SQLException e) {
                    SysUtils.getLog(this.getClass()).error(e);
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException e) {
                            SysUtils.getLog(this.getClass()).error(e);
                        }
                    }
                    if (con != null) {
                        try {
                            con.close();
                        } catch (SQLException e) {
                            SysUtils.getLog(this.getClass()).error(e);
                        }
                    }
                }
            }
        }
        return response;
    }
}
