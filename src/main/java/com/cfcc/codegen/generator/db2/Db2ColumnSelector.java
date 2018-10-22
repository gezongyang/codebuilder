package com.cfcc.codegen.generator.db2;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cfcc.codegen.entity.DataBaseConfig;
import com.cfcc.codegen.generator.ColumnDefinition;
import com.cfcc.codegen.generator.ColumnSelector;

/**
 * 用于DB2获取列信息
 * 
 * @author hua
 * @date 2016年6月29日 下午5:44:19
 */
public class Db2ColumnSelector extends ColumnSelector {

	private static String TABKE_DETAIL_SQL = new StringBuilder()//
			.append(" SELECT ")//
			.append("     NAME,IDENTITY,COLTYPE,LENGTH,NULLS,COLNO,REMARKS,CONSTNAME ")//
			.append(" FROM ")//
			.append("     (SELECT * FROM SYSIBM.SYSCOLUMNS WHERE TBNAME='%s' AND TBCREATOR='%s' ) SS ")//
			.append(" LEFT JOIN ")//
			.append("     SYSIBM.SYSKEYCOLUSE ST ")//
			.append(" ON SS.TBCREATOR=ST.TBCREATOR AND SS.TBNAME=ST.TBNAME AND SS.NAME=ST.COLNAME ")//
			.append(" ORDER BY SS.COLNO ")//
			.toString();//

	public Db2ColumnSelector(DataBaseConfig dataBaseConfig) {
		super(dataBaseConfig);
	}

	@Override
	protected String getColumnInfoSQL(String tableName) {
		return String.format(TABKE_DETAIL_SQL, tableName.toUpperCase(), getDataBaseConfig().getUsername().toUpperCase());
	}

	/*
	 * rowMap: {COLUMN_NAME=barId, IS_IDENTITY=true, COMMENT=网吧ID, IS_PK=1,
	 * TYPE=int}
	 */
	@Override
	protected ColumnDefinition buildColumnDefinition(Map<String, Object> rowMap) {
		Set<String> columnSet = rowMap.keySet();

		for (String columnInfo : columnSet) {
			rowMap.put(columnInfo.toUpperCase(), rowMap.get(columnInfo));
		}

		ColumnDefinition columnDefinition = new ColumnDefinition();

		columnDefinition.setColumnName((String) rowMap.get("NAME"));
		String identityValue = (String) rowMap.get("IDENTITY");
		boolean isIdentity = StringUtils.equalsIgnoreCase(identityValue, "Y")?Boolean.TRUE:Boolean.FALSE;
		columnDefinition.setIsIdentity(isIdentity);
		
		String constnameValue = (String) rowMap.get("CONSTNAME"); //实际上是主键的名称，如TB_PKEY_1
		if(StringUtils.isNotBlank(constnameValue)) {
			columnDefinition.setIsPk(Boolean.TRUE);
		}
		columnDefinition.setType((String) rowMap.get("COLTYPE"));

		columnDefinition.setComment((String) rowMap.get("REMARKS"));
		
		String nullsValue = (String) rowMap.get("NULLS");
		boolean isNull = StringUtils.equalsIgnoreCase(nullsValue, "Y")?Boolean.TRUE:Boolean.FALSE;
		columnDefinition.setIsNull(isNull);
		
		return columnDefinition;
	}

}
