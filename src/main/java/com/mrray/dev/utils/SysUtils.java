package com.mrray.dev.utils;


import com.mrray.dev.entity.dto.PageQueryDto;
import com.mrray.dev.entity.vo.PageQueryVo;
import com.mrray.dev.entity.vo.SortVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Page;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by tanghuan on 2017/3/3.
 */
public class SysUtils {
    public static void mapperPageInfoToVo(PageQueryDto dto, Page page, PageQueryVo pageVo) {
        pageVo.setPage(dto.getPage());
        pageVo.setSize(dto.getSize());
        pageVo.setFirstPage(page.isFirst());
        pageVo.setLastPage(page.isLast());
        int prevPage = page.isFirst() ? 1 : dto.getPage() - 1;
        pageVo.setPrevPage(prevPage);
        int nextPage = page.isLast() ? page.getTotalPages() : dto.getPage() + 1;
        pageVo.setNextPage(nextPage);
        pageVo.setTotalElements(page.getTotalElements());
        pageVo.setTotalPage(page.getTotalPages());
        pageVo.setCurrentPageElements(page.getNumberOfElements());
        pageVo.setSort(new SortVo(dto.getProperty(), dto.getDirection()));
    }

    public static Log getLog(Class clazz) {
        return LogFactory.getLog(clazz);
    }

    /**
     * 验证时间段是否合法
     *
     * @param timeRange 需要验证的时间段,如08:00-18:00
     * @return
     */
    public static String checkTimeRange(String timeRange) {
        if (!StringUtils.isEmpty(timeRange)) {
            String[] times = timeRange.split("-");
            if (times.length != 2) {
                return "时间段不合法!";
            }
            String startTime = times[0];
            String endTime = times[1];
            if (!checkTime(startTime)) {
                return "起始时间不合法!";
            }
            if (!checkTime(endTime)) {
                return "结束时间不合法!";
            }
            if (parseTimetoInt(startTime) > parseTimetoInt(endTime)) {
                return "时间段不合法!";
            }
        }
        return "正确";
    }

    /**
     * 验证时间是否合法
     *
     * @param time 需要验证时间,格式如08:00
     */
    public static boolean checkTime(String time) {
        if (!StringUtils.isEmpty(time)) {
            String timeRegex = "(2[0-3]|[0-1]\\d)(:[0-5]\\d)";
            return Pattern.compile(timeRegex).matcher(time).matches();
        }
        return false;
    }

    /**
     * 将时间转换为int值,以比较时间大小
     *
     * @param time 需要转换的时间,格式如08:00
     */
    public static int parseTimetoInt(String time) {
        String[] times = time.split(":");
        return Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
    }

    /**
     * 校验日期范围是否合法
     *
     * @param dateRange 需要校验的日期范围,格式为yyyy/MM/dd-yyyy/MM/dd
     */
    public static String checkDateRange(String dateRange) {
        if (!StringUtils.isEmpty(dateRange)) {
            String[] dates = dateRange.split("-");
            if (dates.length != 2) {
                return "日期范围不合法!";
            }
            String startDate = dates[0];
            String endDate = dates[1];
            if (!checkDate(startDate)) {
                return "起始日期不合法!";
            }
            if (!checkDate(endDate)) {
                return "结束日期不合法!";
            }
            SimpleDateFormat fm = new SimpleDateFormat("yyyy/MM/dd");
            try {
                Date dateStart = fm.parse(startDate);
                Date dateEnd = fm.parse(endDate);
                if (dateStart.after(dateEnd)) {
                    return "起始日期与结束日期顺序异常!";
                }
            } catch (ParseException e) {
                return "日期范围不合法!";
            }
        }

        return "正确";
    }

    /**
     * 验证时间格式是否合法
     *
     * @param date 需要校验的时间,输入时间格式为yyyy/MM/dd
     */
    public static boolean checkDate(String date) {
        if (!StringUtils.isEmpty(date)) {
            String dateRegex = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})/(((0[13578]|1[02])/(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)/(0[1-9]|[12][0-9]|30))|(02/(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))/02/29)";
            return Pattern.compile(dateRegex).matcher(date).matches();
        }
        return false;
    }

    /**
     * 校验时间是否在某时间段
     *
     * @param time      要校验时间,格式如08:00
     * @param timeRange 时间段,格式如08:00-18:00
     */
    public static boolean checkTimeInRange(String time, String timeRange) {
        int timeInt = parseTimetoInt(time);
        String[] times = timeRange.split("-");
        if (timeInt >= parseTimetoInt(times[0]) && timeInt <= parseTimetoInt(times[1])) {
            return true;
        }
        return false;
    }

    /**
     * 校验日期是否在某日期段内
     *
     * @param date      要校验的日期,格式如2017/01/22
     * @param dateRange 日期段,格式如2017/01/01-2017/02/01
     */
    public static boolean checkDateInRange(String date, String dateRange) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String[] dates = dateRange.split("-");
        Date checkDate;
        Date startDate;
        Date endDate;
        try {
            checkDate = sdf.parse(date);
            startDate = sdf.parse(dates[0]);
            endDate = sdf.parse(dates[1]);
        } catch (ParseException e) {
            return false;
        }
        if (checkDate.before(startDate) || checkDate.after(endDate)) {
            return false;
        }
        return true;
    }

    public static void closeJdbc(Connection con, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            getLog(SysUtils.class).error(e);
        }
    }


}
