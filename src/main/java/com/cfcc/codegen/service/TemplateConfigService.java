package com.cfcc.codegen.service;

import java.util.HashMap;
import java.util.Map;

import com.cfcc.codegen.entity.TemplateConfig;
import com.cfcc.codegen.generator.GenerateType;

public class TemplateConfigService {

	private static Map<Integer, TemplateConfig> templateConfigMap = new HashMap<Integer, TemplateConfig>();

	static {
		templateConfigMap.put(1,
				new TemplateConfig(1, //
						"Controller", //
						"web/${context.moduleName}/controller", //
						"${context.javaBeanName}Controller.java", //
						"java", //
						"cfccvm/Controller.vm", //
						GenerateType.CONTROLLER));
		templateConfigMap.put(11,
				new TemplateConfig(11, //
						"Controller_ms", //
						"${context.moduleName}/controller", //
						"${context.javaBeanName}Controller.java", //
						"java", //
						"cfccvm/Controller_ms.vm", //
						GenerateType.CONTROLLER_MS));
		templateConfigMap.put(2,
				new TemplateConfig(2, //
						"Service", //
						"${context.moduleName}/service", //
						"${context.javaBeanName}Service.java", //
						"java", //
						"cfccvm/Service.vm", //
						GenerateType.SERVICE));
		templateConfigMap.put(3,
				new TemplateConfig(3, //
						"jsp_list", //
						"${context.moduleName}", //
						"${context.javaBeanNameLower}_list.jsp", //
						"jsp", //
						"cfccvm/jsp_list.vm",//
						GenerateType.JSP_LIST));
		templateConfigMap.put(4,
				new TemplateConfig(4, //
						"jsp_form", //
						"${context.moduleName}", //
						"${context.javaBeanNameLower}_form.jsp", //
						"jsp", //
						"cfccvm/jsp_form.vm",//
						GenerateType.JSP_FORM));
	}

	public TemplateConfig get(Object id) {
		return templateConfigMap.get(id);
	}
}
