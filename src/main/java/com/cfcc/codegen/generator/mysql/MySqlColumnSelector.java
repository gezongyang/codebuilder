package com.cfcc.codegen.generator.mysql;

import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.cfcc.codegen.entity.DataBaseConfig;
import com.cfcc.codegen.generator.ColumnDefinition;
import com.cfcc.codegen.generator.ColumnSelector;

/**
 * mysql表信息查询
 *
 */
public class MySqlColumnSelector extends ColumnSelector {

	public MySqlColumnSelector(DataBaseConfig dataBaseConfig) {
		super(dataBaseConfig);
	}

	/**
	 * SHOW FULL COLUMNS FROM 表名
	 */
	@Override
	protected String getColumnInfoSQL(String tableName) {
		return "SHOW FULL COLUMNS FROM " + tableName;
	}

	/*
	 * {FIELD=username, EXTRA=, COMMENT=用户名, COLLATION=utf8_general_ci,
	 * PRIVILEGES=select,insert,update,references, KEY=PRI, NULL=NO,
	 * DEFAULT=null, TYPE=varchar(20)}
	 */
	protected ColumnDefinition buildColumnDefinition(Map<String, Object> rowMap) {
		Set<String> columnSet = rowMap.keySet();

		for (String columnInfo : columnSet) {
			rowMap.put(columnInfo.toUpperCase(), rowMap.get(columnInfo));
		}

		ColumnDefinition columnDefinition = new ColumnDefinition();

		columnDefinition.setColumnName((String) rowMap.get("FIELD"));

		boolean isIdentity = "auto_increment".equalsIgnoreCase((String) rowMap.get("EXTRA"));
		columnDefinition.setIsIdentity(isIdentity);

		boolean isPk = "PRI".equalsIgnoreCase((String) rowMap.get("KEY"));
		columnDefinition.setIsPk(isPk);

		String type = (String) rowMap.get("TYPE");
		String actualDbType = buildType(type);
		columnDefinition.setType(actualDbType);

		int[] lenArr = resolveLength(type);
		columnDefinition.setLength(lenArr[0]);
		columnDefinition.setPrecision(lenArr[1]);

		columnDefinition.setComment((String) rowMap.get("COMMENT"));

		boolean isNull = "YES".equalsIgnoreCase((String) rowMap.get("NULL"));
		columnDefinition.setIsNull(isNull);

		return columnDefinition;
	}

	// 将varchar(50)转换成VARCHAR
	private String buildType(String type) {
		if (StringUtils.hasText(type)) {
			int index = type.indexOf("(");
			if (index > 0) {
				return type.substring(0, index).toUpperCase();
			}
			return type;
		}
		return "VARCHAR";
	}

	// 获取字段长度限制
	private int[] resolveLength(String type) {
		int[] lenArr = new int[2];
		if (StringUtils.hasText(type)) {
			int start = type.indexOf("(");
			if (start > 0) {
				int precisionPosition = type.indexOf(",");
				int end = type.indexOf(")");
				String lengthStr = type.substring(start + 1, end);
				if (precisionPosition > 0) {
					String[] lenStrArr = lengthStr.split(",");
					lenArr[0] = Integer.valueOf(lenStrArr[0]);
					lenArr[1] = Integer.valueOf(lenStrArr[1]);
				} else {
					lenArr[0] = Integer.valueOf(lengthStr);
				}
			}
		}
		return lenArr;
	}

}
