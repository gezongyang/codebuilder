package com.cfcc.codegen.generator.db2;

import com.cfcc.codegen.entity.DataBaseConfig;
import com.cfcc.codegen.generator.ColumnSelector;
import com.cfcc.codegen.generator.SQLService;
import com.cfcc.codegen.generator.TableSelector;

public class Db2Service implements SQLService {

	@Override
	public TableSelector getTableSelector(DataBaseConfig dataBaseConfig) {
		return new Db2TableSelector(new Db2ColumnSelector(dataBaseConfig), dataBaseConfig);
	}

	@Override
	public ColumnSelector getColumnSelector(DataBaseConfig dataBaseConfig) {
		return new Db2ColumnSelector(dataBaseConfig);
	}


}
