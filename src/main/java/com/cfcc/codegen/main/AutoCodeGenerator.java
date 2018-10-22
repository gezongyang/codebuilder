package com.cfcc.codegen.main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.cfcc.codegen.entity.CodeFile;
import com.cfcc.codegen.entity.DataSourceConfig;
import com.cfcc.codegen.entity.GeneratorParam;
import com.cfcc.codegen.service.GeneratorService;
import com.cfcc.codegen.service.TemplateConfigService;
import com.cfcc.codegen.utils.FileUtil;

/**
 * 代码自动生成工具类
 * 
 * @author hua
 * @date 2016年7月1日 下午2:33:01
 */
public class AutoCodeGenerator {
	private static Logger logger = LogManager.getLogger(AutoCodeGenerator.class);

	/**
	 * 在配置文件中的属性名信息
	 */
	public static final String GEN_CONFIG_DEFAULT_CHARSET = "defaultCharset"; // 默认编码
	public static final String GEN_CONFIG_BASE_PACKAGE_NAME = "basePackageName"; // 基础包名
	public static final String GEN_CONFIG_MODULE_NAME = "moduleName"; // 模块名

	public static final String GEN_CONFIG_ENABLE_GENERATE_CONTROLLER = "enableGenerateController"; // 是否生成Controller
	public static final String GEN_CONFIG_ENABLE_GENERATE_SERVICE = "enableGenerateService"; // 是否生成Service
	public static final String GEN_CONFIG_ENABLE_GENERATE_JSP = "enableGenerateJsp"; // 是否生成Jsp

	/**
	 * 生成代码[入口]
	 * 
	 * @param config
	 * @throws Exception
	 */
	public void generate(Configuration config) throws Exception {
		takeCareSourceFolder(config); // 保证代码生成后存放目录的存在
		List<String> tableNames = generateMybatis(config);
		generateBusiness(tableNames, config); // 生成代码
	}

	/**
	 * 利用MyBatis生成dto和mapper
	 * 
	 * @param config
	 *            MBG配置
	 * @return 所有生成的表
	 * @throws Exception
	 */
	public List<String> generateMybatis(Configuration config) throws Exception {
		List<String> warnings = new ArrayList<String>();
		DefaultShellCallback callback = new DefaultShellCallback(true);
		MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
		myBatisGenerator.generate(null);
		List<IntrospectedTable> introspectedTables = myBatisGenerator.getIntrospectedTables();
		Set<String> tableNameSet = new HashSet<>();
		for (IntrospectedTable introspectedTable : introspectedTables) {
			tableNameSet.add(introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
		}
		return new ArrayList<>(tableNameSet);
	}

	/**
	 * 自己写的生成Controller、Service、jsp
	 * 
	 * @param tableNames
	 *            需要生成的表名集合
	 * @param config
	 *            MBG配置
	 * @throws Exception
	 */
	public void generateBusiness(List<String> tableNames, Configuration config) throws Exception {
		if (tableNames == null || tableNames.size() == 0) {
			throw new RuntimeException("tableNames参数不能为空，该参数为待生成的表名称集合!");
		}

		GeneratorService generatorService = new GeneratorService(new TemplateConfigService());
		DataSourceConfig dataSourceConfig = generatorService.buildDatabaseConfig(config); // 自动识别数据库类型和相关配置

		Properties properties = config.getContexts().get(0).getProperties();
		String charset = properties.getProperty(GEN_CONFIG_DEFAULT_CHARSET);
		if (StringUtils.isBlank(charset)) {
			logger.warn("警告：编码格式没有设置，默认为UTF-8!(可以在配置文件中设置[defaultCharset]属性)");
			charset = "UTF-8";
		}
		String packageName = properties.getProperty(GEN_CONFIG_BASE_PACKAGE_NAME);
		if (StringUtils.isBlank(packageName)) {
			throw new RuntimeException("请在配置文件中设置基础包名[basePackageName] 如：com.cfcc.tbdas!");
		}
		String moduleName = properties.getProperty(GEN_CONFIG_MODULE_NAME);
		if (StringUtils.isBlank(moduleName)) {
			logger.warn("警告：模块名称没有设置，默认为当前实体名称!(可以在配置文件中设置[moduleName]属性)");
		}

		List<Integer> templateConfigIdList = loadTemplateConfigId(properties); // 得到需要生成哪些模板

		GeneratorParam generatorParam = new GeneratorParam();
		generatorParam.setCharset(charset);
		generatorParam.setPackageName(packageName);
		generatorParam.setModuleName(moduleName);
		generatorParam.setTableNames(tableNames);
		generatorParam.setTcIds(templateConfigIdList);

		List<CodeFile> codeFileList = generatorService.generate(generatorParam, dataSourceConfig);
		if (codeFileList == null || codeFileList.size() == 0) {
			logger.error("===>没有生成文件！！！");
			return;
		}
		FileUtil.writeCodeFileList(codeFileList, generatorParam.getCharset());
	}

	/**
	 * 处理生成代码的目录
	 * 
	 * @param config
	 */
	private void takeCareSourceFolder(Configuration config) {
		Context context = config.getContexts().get(0);
		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = context.getJavaModelGeneratorConfiguration();
		String targetProject = javaModelGeneratorConfiguration.getTargetProject();
		FileUtil.createFolder(targetProject);

		SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = context.getSqlMapGeneratorConfiguration();
		String targetProject2 = sqlMapGeneratorConfiguration.getTargetProject();
		FileUtil.createFolder(targetProject2);

		JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = context.getJavaClientGeneratorConfiguration();
		String targetProject3 = javaClientGeneratorConfiguration.getTargetProject();
		FileUtil.createFolder(targetProject3);
		
		Context context1 = config.getContexts().get(1);
		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration1 = context1.getJavaModelGeneratorConfiguration();
		String targetProject1 = javaModelGeneratorConfiguration1.getTargetProject();
		FileUtil.createFolder(targetProject1);
	}

	/**
	 * 根据配置文件得到需要生成的模板Id
	 * 
	 * @param properties
	 * @return 待生成模板Id列表
	 */
	private List<Integer> loadTemplateConfigId(Properties properties) {
		List<Integer> templateConfigIdList = new ArrayList<>();
		String enableGenerateController = properties.getProperty(GEN_CONFIG_ENABLE_GENERATE_CONTROLLER);
		if (StringUtils.isBlank(enableGenerateController) || StringUtils.equalsIgnoreCase(enableGenerateController, "true")) {
			templateConfigIdList.add(1); // 前端Controller
			templateConfigIdList.add(11); // 微服务Controller
		}
		String enableGenerateService = properties.getProperty(GEN_CONFIG_ENABLE_GENERATE_SERVICE);
		if (StringUtils.isBlank(enableGenerateService) || StringUtils.equalsIgnoreCase(enableGenerateService, "true")) {
			templateConfigIdList.add(2);
		}
		String enableGenerateJsp = properties.getProperty(GEN_CONFIG_ENABLE_GENERATE_JSP);
		if (StringUtils.isBlank(enableGenerateJsp) || StringUtils.equalsIgnoreCase(enableGenerateJsp, "true")) {
			templateConfigIdList.add(3);
			templateConfigIdList.add(4);
		}
		return templateConfigIdList;
	}

}
