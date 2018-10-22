package com.cfcc.codegen.entity;

import org.apache.commons.lang3.StringUtils;

import com.cfcc.codegen.generator.GenerateType;
import com.cfcc.codegen.utils.FileUtil;

public class TemplateConfig {
	private int tcId;
	private String name;
	private String savePath;
	private String fileName;
	private String content;
	private String backUser;
	private String suffix;
	private String templateFilePath;
	private GenerateType generateType;

	public TemplateConfig(int tcId, String name, String savePath, String fileName, String suffix, String templateFilePath) {
		super();
		this.tcId = tcId;
		this.name = name;
		this.savePath = savePath;
		this.fileName = fileName;
		this.suffix = suffix;
		this.templateFilePath = templateFilePath;
	}

	public TemplateConfig(int tcId, String name, String savePath, String fileName, String suffix, String templateFilePath,
			GenerateType generateType) {
		super();
		this.tcId = tcId;
		this.name = name;
		this.savePath = savePath;
		this.fileName = fileName;
		this.suffix = suffix;
		this.templateFilePath = templateFilePath;
		this.generateType = generateType;
	}

	public GenerateType getGenerateType() {
		return generateType;
	}

	public void setGenerateType(GenerateType generateType) {
		this.generateType = generateType;
	}

	public TemplateConfig() {
		super();
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public int getTcId() {
		return tcId;
	}

	public void setTcId(int tcId) {
		this.tcId = tcId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getContent() {
		if (StringUtils.isNotBlank(this.getTemplateFilePath())) {
			return FileUtil.readFromClassPath(getTemplateFilePath());
		}
		return "请设置模板路径!";
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getBackUser() {
		return backUser;
	}

	public void setBackUser(String backUser) {
		this.backUser = backUser;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTemplateFilePath() {
		return templateFilePath;
	}

	public void setTemplateFilePath(String templateFilePath) {
		this.templateFilePath = templateFilePath;
	}

	@Override
	public String toString() {
		return "TemplateConfig [tcId=" + tcId + ", name=" + name + ", savePath=" + savePath + ", fileName=" + fileName + ", content=" + content
				+ ", backUser=" + backUser + ", suffix=" + suffix + ", templateFilePath=" + templateFilePath + "]";
	}
}
