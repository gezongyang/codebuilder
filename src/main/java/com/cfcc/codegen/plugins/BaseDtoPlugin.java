package com.cfcc.codegen.plugins;

import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;

import com.mysql.jdbc.StringUtils;

/**
 * Mybatis generator插件：用于给生成的Dto添加公共的父类 由baseDtoClass指定
 * 
 * @author hua
 * @date 2016年6月8日 下午3:02:48
 */
public class BaseDtoPlugin extends PluginAdapter {

	private FullyQualifiedJavaType serializable;
	private FullyQualifiedJavaType gwtSerializable;
	private String baseDtoClass;
	private boolean addGWTInterface;
	private boolean suppressJavaInterface;

	public BaseDtoPlugin() {
		super();
		if (!StringUtils.isEmptyOrWhitespaceOnly(baseDtoClass)) {
			serializable = new FullyQualifiedJavaType(baseDtoClass); // $NON-NLS-1$
		}
		gwtSerializable = new FullyQualifiedJavaType("com.google.gwt.user.client.rpc.IsSerializable"); //$NON-NLS-1$
	}

	public boolean validate(List<String> warnings) {
		// this plugin is always valid
		return true;
	}

	@Override
	public void setProperties(Properties properties) {
		super.setProperties(properties);
		baseDtoClass = this.properties.getProperty("baseDtoClass");
		if (StringUtility.stringHasValue(baseDtoClass)) {
			serializable = new FullyQualifiedJavaType(baseDtoClass);
			addGWTInterface = Boolean.valueOf(properties.getProperty("addGWTInterface")); //$NON-NLS-1$
			suppressJavaInterface = Boolean.valueOf(properties.getProperty("suppressJavaInterface")); //$NON-NLS-1$
		} else {
			throw new RuntimeException("缺少baseDtoClass属性(需要继承的基础dto的class)");
		}
	}

	@Override
	public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}

	@Override
	public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}

	@Override
	public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		makeSerializable(topLevelClass, introspectedTable);
		return true;
	}

	protected void makeSerializable(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		if (addGWTInterface) {
			topLevelClass.addImportedType(gwtSerializable);
			topLevelClass.addSuperInterface(gwtSerializable);
		}

		if (!suppressJavaInterface) {
			topLevelClass.addImportedType(serializable);
			topLevelClass.addSuperInterface(serializable);

			Field field = new Field();
			field.setFinal(true);
			field.setInitializationString("1L"); //$NON-NLS-1$
			field.setName("serialVersionUID"); //$NON-NLS-1$
			field.setStatic(true);
			field.setType(new FullyQualifiedJavaType("long")); //$NON-NLS-1$
			field.setVisibility(JavaVisibility.PRIVATE);
			context.getCommentGenerator().addFieldComment(field, introspectedTable);

			topLevelClass.addField(field);
		}
	}
}
