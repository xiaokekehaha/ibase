package com.wzk.mvc.dao;

import com.wzk.mvc.model.MyOpenballInfo;

public interface MyOpenballInfoMapper {
	int deleteByPrimaryKey(Long id);

	int insert(MyOpenballInfo record);

	int insertSelective(MyOpenballInfo record);

	MyOpenballInfo selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(MyOpenballInfo record);

	int updateByPrimaryKey(MyOpenballInfo record);

	MyOpenballInfo selectOneByLast();

	MyOpenballInfo selectOneByExpect(Long expect);
}