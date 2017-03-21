package com.wzk.mvc.dao;

import com.wzk.mvc.model.LogIcecoldmonitor;

public interface LogIcecoldmonitorMapper {
    int deleteByPrimaryKey(Integer logId);

    int insert(LogIcecoldmonitor record);

    int insertSelective(LogIcecoldmonitor record);

    LogIcecoldmonitor selectByPrimaryKey(Integer logId);

    int updateByPrimaryKeySelective(LogIcecoldmonitor record);

    int updateByPrimaryKeyWithBLOBs(LogIcecoldmonitor record);

    int updateByPrimaryKey(LogIcecoldmonitor record);
}