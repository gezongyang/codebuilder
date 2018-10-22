package com.cfcc.codegen.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.JDBCConnectionConfiguration;

import com.cfcc.codegen.entity.CodeFile;
import com.cfcc.codegen.entity.DataSourceConfig;
import com.cfcc.codegen.entity.GeneratorParam;
import com.cfcc.codegen.entity.TemplateConfig;
import com.cfcc.codegen.generator.SQLContext;
import com.cfcc.codegen.generator.SQLService;
import com.cfcc.codegen.generator.SQLServiceFactory;
import com.cfcc.codegen.generator.TableDefinition;
import com.cfcc.codegen.generator.TableSelector;
import com.cfcc.codegen.utils.FileUtil;
import com.cfcc.codegen.utils.VelocityUtil;
import com.cfcc.codegen.utils.XmlFormat;

public class GeneratorService {
	private TemplateConfigService templateConfigService;

	private static final String DOWNLOAD_FOLDER_NAME = "download";

	public GeneratorService(TemplateConfigService templateConfigService) {
		super();
		this.templateConfigService = templateConfigService;
	}

	public GeneratorService() {
		super();
	}

	/**
	 * 生成代码内容,map的
	 * 
	 * @param tableNames
	 * @param tcIds
	 * @param dataSourceConfig
	 * @return 一张表对应多个模板
	 */
	public List<CodeFile> generate(GeneratorParam generatorParam, DataSourceConfig dataSourceConfig) {

		List<SQLContext> contextList = this.buildSQLContextList(generatorParam, dataSourceConfig);

		List<CodeFile> codeFileList = new ArrayList<CodeFile>();

		for (SQLContext sqlContext : contextList) {
			setPackageName(sqlContext, generatorParam.getPackageName());
			setModuleName(sqlContext, generatorParam.getModuleName());

			String packageName = sqlContext.getJavaBeanNameLF();

			for (int tcId : generatorParam.getTcIds()) {

				TemplateConfig template = templateConfigService.get(tcId);

				String fileName = doGenerator(sqlContext, template.getFileName());
				String content = doGenerator(sqlContext, template.getContent());
				String savePath = doGenerator(sqlContext, template.getSavePath());

				CodeFile codeFile = new CodeFile(generatorParam.getPackageName(), fileName, content, savePath, template.getGenerateType());

				this.formatFile(codeFile);

				codeFileList.add(codeFile);
			}
		}

		return codeFileList;
	}

	private void formatFile(CodeFile file) {
		String formated = this.doFormat(file.getTemplateName(), file.getContent());
		file.setContent(formated);
	}

	private String doFormat(String fileName, String content) {
		if (fileName.endsWith(".xml")) {
			return XmlFormat.format(content);
		}
		return content;
	}

	public void setTemplateConfigService(TemplateConfigService templateConfigService) {
		this.templateConfigService = templateConfigService;
	}

	/**
	 * 生成zip
	 * 
	 * @param generatorParam
	 * @param dataSourceConfig
	 * @param webRootPath
	 * @return
	 */
	public String generateZip(GeneratorParam generatorParam, DataSourceConfig dataSourceConfig, String webRootPath) {
		List<SQLContext> contextList = this.buildSQLContextList(generatorParam, dataSourceConfig);
		String projectFolder = this.buildProjectFolder(webRootPath);

		for (SQLContext sqlContext : contextList) {
			setPackageName(sqlContext, generatorParam.getPackageName());
			for (int tcId : generatorParam.getTcIds()) {
				TemplateConfig template = templateConfigService.get(tcId);
				String content = doGenerator(sqlContext, template.getContent());
				String fileName = doGenerator(sqlContext, template.getFileName());
				String savePath = doGenerator(sqlContext, template.getSavePath());

				content = this.doFormat(fileName, content);

				if (StringUtils.isEmpty(fileName)) {
					fileName = template.getName();
				}

				FileUtil.createFolder(projectFolder + File.separator + savePath);

				String filePathName = projectFolder + File.separator + savePath + File.separator + fileName;
				FileUtil.write(content, filePathName, generatorParam.getCharset());
			}
		}

		try {
			FileUtil.zip(projectFolder, projectFolder + ".zip");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtil.deleteDir(new File(projectFolder));
		}

		return projectFolder + ".zip";
	}

	/**
	 * 返回SQL上下文列表
	 * 
	 * @param tableNames
	 * @return
	 */
	private List<SQLContext> buildSQLContextList(GeneratorParam generatorParam, DataSourceConfig dataSourceConfig) {

		List<String> tableNames = generatorParam.getTableNames();
		List<SQLContext> contextList = new ArrayList<SQLContext>();
		SQLService service = SQLServiceFactory.build(dataSourceConfig);

		TableSelector tableSelector = service.getTableSelector(dataSourceConfig);
		tableSelector.setSchTableNames(tableNames);

		List<TableDefinition> tableDefinitions = tableSelector.getTableDefinitions();

		for (TableDefinition tableDefinition : tableDefinitions) {
			SQLContext sqlContext = new SQLContext(tableDefinition);
			sqlContext.setCharset(generatorParam.getCharset());
			contextList.add(sqlContext);
		}

		return contextList;
	}

	/**
	 * 根据配置信息自动识别数据库详细配置
	 * 
	 * @param config
	 * @return
	 */
	public DataSourceConfig buildDatabaseConfig(Configuration config) {
		DataSourceConfig dataSourceConfig = new DataSourceConfig();
		dataSourceConfig.setBackUser("admin");
		JDBCConnectionConfiguration jdbcConnectionConfiguration = config.getContexts().get(0).getJdbcConnectionConfiguration();
		String connectionURL = jdbcConnectionConfiguration.getConnectionURL();
		if (StringUtils.containsIgnoreCase(connectionURL, "db2")) {
			String ipAndPort = StringUtils.substring(connectionURL, StringUtils.indexOf(connectionURL, "//") + 2);
			String[] strings = ipAndPort.split("/");
			dataSourceConfig.setDbName(strings[1]);
			dataSourceConfig.setIp(strings[0].split(":")[0]);
			dataSourceConfig.setPort(Integer.valueOf(strings[0].split(":")[1]));
		} else if (StringUtils.containsIgnoreCase(connectionURL, "mysql")) {
			// jdbc:mysql://localhost:3306/tfiepdb?characterEncoding=gbk
			String ipAndPort = StringUtils.substring(connectionURL, StringUtils.indexOf(connectionURL, "//") + 2);
			String[] strings = ipAndPort.split("/");
			dataSourceConfig.setDbName(StringUtils.split(strings[1], "?")[0]);
			dataSourceConfig.setIp(strings[0].split(":")[0]);
			dataSourceConfig.setPort(Integer.valueOf(strings[0].split(":")[1]));
		}
		dataSourceConfig.setDriverClass(jdbcConnectionConfiguration.getDriverClass());
		dataSourceConfig.setUsername(jdbcConnectionConfiguration.getUserId());
		dataSourceConfig.setPassword(jdbcConnectionConfiguration.getPassword());
		return dataSourceConfig;
	}

	private String buildProjectFolder(String webRootPath) {
		return webRootPath + File.separator + DOWNLOAD_FOLDER_NAME + File.separator + "admin" + System.currentTimeMillis();
	}

	private void setPackageName(SQLContext sqlContext, String packageName) {
		if (StringUtils.isNotBlank(packageName)) {
			sqlContext.setPackageName(packageName.toLowerCase());
		}
	}

	private void setModuleName(SQLContext sqlContext, String moduleName) {
		if (StringUtils.isNotBlank(moduleName)) {
			sqlContext.setModuleName(moduleName.toLowerCase());
		} else {
			sqlContext.setModuleName(sqlContext.getJavaBeanNameLower());
		}
	}

	private String doGenerator(SQLContext sqlContext, String template) {
		if (template == null) {
			return "";
		}
		VelocityContext context = new VelocityContext();

		context.put("context", sqlContext);
		context.put("table", sqlContext.getTableDefinition());
		context.put("pkColumn", sqlContext.getTableDefinition().getPkColumn());
		context.put("columns", sqlContext.getTableDefinition().getColumnDefinitions());

		return VelocityUtil.generate(context, template);
	}

}
