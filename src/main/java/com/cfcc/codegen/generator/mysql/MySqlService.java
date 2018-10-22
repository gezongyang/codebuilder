package com.cfcc.codegen.generator.mysql;

import com.cfcc.codegen.entity.DataBaseConfig;
import com.cfcc.codegen.generator.ColumnSelector;
import com.cfcc.codegen.generator.SQLService;
import com.cfcc.codegen.generator.TableSelector;

public class MySqlService implements SQLService {

	@Override
	public TableSelector getTableSelector(DataBaseConfig dataBaseConfig) {
		return new MySqlTableSelector(new MySqlColumnSelector(dataBaseConfig), dataBaseConfig);
	}

	@Override
	public ColumnSelector getColumnSelector(DataBaseConfig dataBaseConfig) {
		return new MySqlColumnSelector(dataBaseConfig);
	}


}
