package com.cfcc.codegen.generator;

import com.cfcc.codegen.utils.FieldUtil;
import com.cfcc.codegen.utils.SqlTypeUtil;

/**
 * 表字段信息
 */
public class ColumnDefinition {

	private String columnName; // 数据库字段名
	private String type; // 数据库类型
	private boolean isIdentity; // 是否自增
	private boolean isPk; // 是否主键
	private String comment; // 字段注释
	private boolean isNull; // 是否允许为空 true-可以为空 false-不可为空
	private int length; // 字段长度限制
	private int precision; // 小数位数

	public boolean getIsNull() {
		return isNull;
	}

	public void setIsNull(boolean isNull) {
		this.isNull = isNull;
	}

	/**
	 * 是否是自增主键
	 * 
	 * @return
	 */
	public boolean getIsIdentityPk() {
		return isPk && isIdentity;
	}

	/**
	 * 返回java字段名,并且第一个字母大写
	 * 
	 * @return
	 */
	public String getJavaFieldNameUF() {
		return FieldUtil.upperFirstLetter(getJavaFieldName().trim());
	}

	public String getJavaFieldName() {
		return FieldUtil.underlineFilter(columnName.trim().toLowerCase());
	}

	public String getJavaCameFieldName() {
		String javaFieldName = this.getJavaFieldName();
		char c2 = javaFieldName.charAt(1);
		if (Character.isUpperCase(c2)) {
			return javaFieldName;
		} else {
			return this.getJavaFieldNameUF();
		}
	}

	/**
	 * 获得基本类型,int,float
	 * 
	 * @return
	 */

	public String getJavaType() {
		String typeLower = type.toLowerCase();
		String tt = this.getJavaTypeBox();
		return SqlTypeUtil.convertToJavaType(typeLower.trim());
	}

	public String getPropertyFilterType() {
		String javaType = SqlTypeUtil.convertToJavaType(type.toLowerCase().trim());
		switch (javaType) {
		case "String":
			return "LIKES";
		case "Integer":
			return "EQI";
		case "int":
			return "EQI";
		case "short":
			return "EQI";
		case "long":
			return "EQL";
		case "Date":
			return "EQD";
		case "BigDecimal":
			return "EQM";
		case "double":
			return "EQN";
		case "float":
			return "EQN";
		case "boolean":
			return "EQB";
		default:
			break;
		}
		return "LIKES";
	}

	/**
	 * 获得装箱类型,Integer,Float
	 * 
	 * @return
	 */

	public String getJavaTypeBox() {
		String typeLower = type.toLowerCase();
		return SqlTypeUtil.convertToJavaBoxType(typeLower.trim());
	}

	public String getMybatisJdbcType() {
		String typeLower = type.toLowerCase();
		return SqlTypeUtil.convertToMyBatisJdbcType(typeLower.trim());
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getIsIdentity() {
		return isIdentity;
	}

	public void setIsIdentity(boolean isIdentity) {
		this.isIdentity = isIdentity;
	}

	public boolean getIsPk() {
		return isPk;
	}

	public void setIsPk(boolean isPk) {
		this.isPk = isPk;
	}

	public String getComment() {
		return comment;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public void setComment(String comment) {
		if (comment == null) {
			comment = "";
		}
		this.comment = comment;
	}

}
