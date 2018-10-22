package com.cfcc.codegen.entity;

import com.cfcc.codegen.generator.GenerateType;

public class CodeFile {
	public CodeFile(String tableName, String templateName, String content) {
		this.tableName = tableName;
		this.templateName = templateName;
		this.content = content;
	}

	public CodeFile(String tableName, String templateName, String content, String savePath) {
		super();
		this.tableName = tableName;
		this.templateName = templateName;
		this.content = content;
		this.savePath = savePath;
	}
	
	public CodeFile(String tableName, String templateName, String content, String savePath,GenerateType generateType) {
		super();
		this.tableName = tableName;
		this.templateName = templateName;
		this.content = content;
		this.savePath = savePath;
		this.generateType = generateType;
	}

	public CodeFile() {
		super();
	}

	private String tableName;
	private String templateName;
	private String content;
	private String savePath; // 相对于包名的路径 如. web/controller
	private GenerateType generateType;

	public GenerateType getGenerateType() {
		return generateType;
	}

	public void setGenerateType(GenerateType generateType) {
		this.generateType = generateType;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "CodeFile [tableName=" + tableName + ", templateName=" + templateName + ", content=" + content + "]";
	}

}
