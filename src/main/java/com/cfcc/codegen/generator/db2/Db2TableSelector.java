package com.cfcc.codegen.generator.db2;

import java.util.Map;

import com.cfcc.codegen.entity.DataBaseConfig;
import com.cfcc.codegen.generator.ColumnSelector;
import com.cfcc.codegen.generator.TableDefinition;
import com.cfcc.codegen.generator.TableSelector;

public class Db2TableSelector extends TableSelector {
	
	public Db2TableSelector(ColumnSelector columnSelector,
			DataBaseConfig dataBaseConfig) {
		super(columnSelector, dataBaseConfig);
	}

	@Override
	protected String getShowTablesSQL(String dbName) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT TABNAME AS TABLE_NAME,REMARKS AS COMMENT FROM syscat.tables st WHERE st.TYPE='T' AND st.TABSCHEMA='"+getDataBaseConfig().getUsername().toUpperCase()+"' ");
		if(this.getSchTableNames() != null && this.getSchTableNames().size() > 0) {
			StringBuilder tables = new StringBuilder();
			for (String table : this.getSchTableNames()) {
				tables.append(",'").append(table).append("'");
			}
			sb.append(" AND st.TABNAME IN ("+ tables.substring(1)+")");
		}
		return sb.toString();
	}

	// {TABLE_NAME=account.adjustBatch, COMMENT=}
	@Override
	protected TableDefinition buildTableDefinition(Map<String, Object> tableMap) {
		TableDefinition tableDefinition = new TableDefinition();
		tableDefinition.setTableName((String)tableMap.get("TABLE_NAME"));
		tableDefinition.setComment((String)tableMap.get("COMMENT"));
		return tableDefinition;
	}

}
