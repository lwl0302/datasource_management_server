package com.mrray.dev.utils;

import com.mrray.dev.entity.vo.BaseResourceInfoVo;
import com.mrray.dev.entity.vo.EtlVo;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;

/**
 * Created by ln on 2017/7/24.
 */
public class DatabaseUtil {
    public static DatabaseMeta getDatabaseMeta(BaseResourceInfoVo baseResourceInfo, String connectionName) {
        StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append("<connection><name>")
                .append(connectionName)
                .append("</name><server>")
                .append(baseResourceInfo.getIp())
                .append("</server><type>")
                .append(baseResourceInfo.getDbType())
                .append("</type><access>Native</access><database>")
                .append(baseResourceInfo.getDatabaseName())
                .append("</database><port>")
                .append(baseResourceInfo.getPort())
                .append("</port><username>")
                .append(baseResourceInfo.getUsername())
                .append("</username><password>")
                .append(baseResourceInfo.getPassword())
                .append("</password></connection>");
        DatabaseMeta databaseMeta = null;
        try {
            databaseMeta = new DatabaseMeta(builder.toString());
            databaseMeta.addExtraOption("INFOBRIGHT", "characterEncoding", "UTF-8");
        } catch (KettleException e) {
            e.printStackTrace();
        }
        return databaseMeta;
    }

    public static String checkTableExist(EtlVo eltSource) {
        DatabaseMeta databaseMeta = getDatabaseMeta(eltSource, "source");
        if (databaseMeta == null) {
            return "指定数据库没有开启,或者账号密码过期导致不能连接,请查证!";
        }
        Database databae = new Database(null, databaseMeta);
        try {
            databae.connect();
            String[] tableNames = databae.getTablenames();
            for (String tableName : tableNames) {
                if (eltSource.getTableName().equals(tableName)) {
                    return "success";
                }
            }
        } catch (KettleDatabaseException e) {
            return "指定数据库没有开启,或者账号密码过期导致不能连接,请查证!";
        } finally {
            databae.disconnect();
        }
        return "指定表不存在或已被删除!";
    }
}
