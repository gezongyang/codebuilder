package com.cfcc.codegen.generator;

/**
 * 代码生成类型
 * 
 * @author hua
 *
 */
public enum GenerateType {

	CONTROLLER(1, "前端控制器"), //
	CONTROLLER_MS(2, "微服务后端控制器"), //
	SERVICE(3, "业务服务"), //
	JSP_LIST(4, "列表页面"), //
	JSP_FORM(5, "表单页面");

	private GenerateType(int code, String desc) {
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
