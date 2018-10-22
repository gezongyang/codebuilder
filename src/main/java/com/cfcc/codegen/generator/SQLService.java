package com.cfcc.codegen.generator;

import com.cfcc.codegen.entity.DataBaseConfig;

public interface SQLService {

	public TableSelector getTableSelector(DataBaseConfig dataBaseConfig);

	public ColumnSelector getColumnSelector(DataBaseConfig dataBaseConfig);

}
