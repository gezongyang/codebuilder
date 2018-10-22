package com.cfcc.codegen;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;

import com.cfcc.codegen.main.AutoCodeGenerator;

/**
 * 代码自动生成
 * 
 * @author hua
 * @date 2016年7月1日 下午2:28:48
 */
public class GeneratorMain {

	private static Logger logger = LogManager.getLogger(GeneratorMain.class);

	private static String GENERATOR_CONFIG_FILE = "generatorConfig-mapper-mysql-tbdas.xml"; // 配置文件
	private static Configuration config;

	static {
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(GENERATOR_CONFIG_FILE);
		try {
			config = new ConfigurationParser(new ArrayList<String>()).parseConfiguration(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			if (args != null && args.length > 0) {
				String genType = args[0];
				if ("1".equals(genType)) {
					// 生成全部数据
					new AutoCodeGenerator().generate(config);
				} else if ("2".equals(genType)) {
					// 单独生成dto和mapper
					new AutoCodeGenerator().generateMybatis(config);
				} else if ("3".equals(genType)) {
					// 单独生成Controller, Service, jsp
					new AutoCodeGenerator().generateBusiness(null, config);
				} else {
					throw new RuntimeException("参数仅支持1,2,3");
				}
			} else {
				// 生成全部数据
				new AutoCodeGenerator().generate(config);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
