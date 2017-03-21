package com.wzk.mvc.dao;

import com.wzk.mvc.model.MyOpencodeInfo;

public interface MyOpencodeInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MyOpencodeInfo record);

    int insertSelective(MyOpencodeInfo record);

    MyOpencodeInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MyOpencodeInfo record);

    int updateByPrimaryKey(MyOpencodeInfo record);

	MyOpencodeInfo selectOneByExpect(Long expect);
	
	MyOpencodeInfo selectOneByLast();
}