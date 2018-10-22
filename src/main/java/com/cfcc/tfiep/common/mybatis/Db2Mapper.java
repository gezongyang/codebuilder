package com.cfcc.tfiep.common.mybatis;

import tk.mybatis.mapper.common.ExampleMapper;
import tk.mybatis.mapper.common.Marker;
import tk.mybatis.mapper.common.RowBoundsMapper;
import tk.mybatis.mapper.common.SqlServerMapper;
import tk.mybatis.mapper.common.base.BaseDeleteMapper;
import tk.mybatis.mapper.common.base.BaseSelectMapper;
import tk.mybatis.mapper.common.base.BaseUpdateMapper;

public interface Db2Mapper<T> extends 
		BaseSelectMapper<T>, 
		SqlServerMapper<T>,
		BaseUpdateMapper<T>, 
		BaseDeleteMapper<T>,
		ExampleMapper<T>, 
		RowBoundsMapper<T>, 
		Marker{
}
