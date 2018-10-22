package com.cfcc.codegen.generator;

import java.util.List;

import com.cfcc.codegen.utils.DateUtils;
import com.cfcc.codegen.utils.FieldUtil;

/**
 * SQL上下文,这里可以取到表,字段信息<br>
 * 最终会把SQL上下文信息放到velocity中
 */
public class SQLContext {
	private TableDefinition tableDefinition; // 表结构定义
	private String packageName; // 包名
	private String moduleName; // 模块名，最终包名为 packageName.service.moduleName
	private String charset; // 编码格式 如 UTF-8 GBK

	public SQLContext(TableDefinition tableDefinition) {
		this.tableDefinition = tableDefinition;
		// 默认为全字母小写的类名
		this.packageName = getJavaBeanName().toLowerCase();
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * 返回Java类名
	 * 
	 * @return
	 */
	public String getJavaBeanName() {
		String tableName = getJavaBeanNameLF();
		return FieldUtil.upperFirstLetter(tableName);
	}

	/**
	 * 返回全部小写的java类名
	 * 
	 * @return
	 */
	public String getJavaBeanNameLower() {
		return getJavaBeanName().toLowerCase();
	}
	
	/**
	 * 返回大写模块名
	 * @return
	 */
	public String getUpperModuleName() {
		return moduleName.toUpperCase();
	}
	
	/**
	 * 返回大写表名
	 * @return
	 */
	public String getUpperJavaBeanName() {
		return getJavaBeanName().toUpperCase();
	}

	/**
	 * 返回Java类名且首字母小写
	 * 
	 * @return
	 */
	public String getJavaBeanNameLF() {
		String tableName = tableDefinition.getTableName().toLowerCase();
		tableName = FieldUtil.underlineFilter(tableName);
		tableName = FieldUtil.dotFilter(tableName);
		return FieldUtil.lowerFirstLetter(tableName);
	}

	public String getPkName() {
		if (this.tableDefinition.getPkColumn() != null) {
			return this.tableDefinition.getPkColumn().getColumnName();
		}
		return "";
	}

	public String getJavaPkName() {
		if (this.tableDefinition.getPkColumn() != null) {
			return this.tableDefinition.getPkColumn().getJavaFieldName();
		}
		return "";
	}
	
	/**
	 * 判断是否有核算主体代码字段
	 * @return
	 */
	public boolean isHaveBookorg() {
		return this.tableDefinition.isHaveBookorg();
	}
	
	/**
	 * 返回核算主体代码字段
	 * @return
	 */
	public String getBookorgName() {
		return this.tableDefinition.getBookorgName();
	}

	/**
	 * 返回java主键字段名，首字母大写 e.g. user_name -> UserName
	 * 
	 * @return
	 */
	public String getJavaPkNameUpperFirstLetter() {
		if (this.tableDefinition.getPkColumn() != null) {
			String javaFieldName = this.tableDefinition.getPkColumn().getJavaFieldName();
			char c2 = javaFieldName.charAt(1);
			if (Character.isUpperCase(c2)) {
				return javaFieldName;
			} else {
				return this.tableDefinition.getPkColumn().getJavaFieldNameUF();
			}
		}
		return "";
	}

	public String getJavaPkType() {
		if (this.tableDefinition.getPkColumn() != null) {
			return this.tableDefinition.getPkColumn().getJavaType();
		}
		return "";
	}

	public String getJavaPkBoxType() {
		if (this.tableDefinition.getPkColumn() != null) {
			return this.tableDefinition.getPkColumn().getJavaTypeBox();
		}
		return "";
	}

	public String getMybatisPkType() {
		if (this.tableDefinition.getPkColumn() != null) {
			return this.tableDefinition.getPkColumn().getMybatisJdbcType();
		}
		return "";
	}

	/**
	 * 是否是联合主键
	 * 
	 * @return
	 */
	public boolean getIsMultiPk() {
		return tableDefinition.isMultiPk();
	}

	public TableDefinition getTableDefinition() {
		return tableDefinition;
	}

	public void setTableDefinition(TableDefinition tableDefinition) {
		this.tableDefinition = tableDefinition;
	}

	public List<ColumnDefinition> getColumnDefinitionList() {
		return tableDefinition.getColumnDefinitions();
	}

	public String getPackageName() {
		return packageName;
	}
	
	public String getCurrentTimeStampStr() {
		return DateUtils.getDate("yyyy年MM月dd日 a HH:mm:ss");
	}
	
	public String getCurrentSysUserName() {
		return System.getProperty("user.name");
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public static void main(String[] args) {

		SQLContext[] tt = { new SQLContext(new TableDefinition("r_table_b")), new SQLContext(new TableDefinition("MyTable")),
				new SQLContext(new TableDefinition("user.frontUser")), new SQLContext(new TableDefinition("user.back_user")),
				new SQLContext(new TableDefinition("TD_BDGSBT")), new SQLContext(new TableDefinition("TD_BDGSBT_TeSt")) };

		for (SQLContext ctx : tt) {
			System.out.println("JavaBeanName : " + ctx.getJavaBeanName());
			System.out.println("JavaBeanNameLF : " + ctx.getJavaBeanNameLF());
			System.out.println("PackageName : " + ctx.getPackageName());
		}
		/*
		 * 输出: RTableB MyTable UserFrontUser UserBackUser
		 */
	}

}
