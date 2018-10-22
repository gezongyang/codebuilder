package com.cfcc.codegen.generator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;

/**
 * 数据库表定义,从这里可以获取表名,字段信息
 */
public class TableDefinition {

	private String tableName; // 表名
	private String comment; // 注释
	private List<ColumnDefinition> columnDefinitions = Collections.emptyList(); // 字段定义
	
	private static final String[] bookorgNames = new String[]{"sOrgcode","sBookorgcode"};

	public TableDefinition() {
	}

	public TableDefinition(String tableName) {
		this.tableName = tableName;
	}

	public ColumnDefinition getPkColumn() {
		for (ColumnDefinition column : columnDefinitions) {
			if (column.getIsPk()) {
				return column;
			}
		}
		return null;
	}
	
	/**
	 * 判断是否有核算主体代码字段
	 * @return
	 */
	public boolean isHaveBookorg() {
		for(String bookorgName : bookorgNames) {
			for (ColumnDefinition column : columnDefinitions) {
				if(column.getJavaFieldName().equalsIgnoreCase(bookorgName)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 返回核算主体代码字段
	 * @return
	 */
	public String getBookorgName() {
		for(String bookorgName : bookorgNames) {
			for (ColumnDefinition column : columnDefinitions) {
				if(column.getJavaFieldName().equalsIgnoreCase(bookorgName)) {
					return bookorgName;
				}
			}
		}
		return "";
	}

	/**
	 * 表是否是联合主键
	 * 
	 * @return
	 */
	public boolean isMultiPk() {
		AtomicInteger ai = new AtomicInteger();
		for (ColumnDefinition column : columnDefinitions) {
			if (column.getIsPk()) {
				ai.incrementAndGet();
			}
		}
		return ai.get() > 1 ? Boolean.TRUE : Boolean.FALSE;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<ColumnDefinition> getColumnDefinitions() {
		return columnDefinitions;
	}

	public void setColumnDefinitions(List<ColumnDefinition> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
	}

}
