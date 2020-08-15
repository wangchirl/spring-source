package com.shadow.spring;

/**
 * @author shadow
 * @create 2020-08-15
 * @description
 */
public class BeanDefinition {

	private String scope;

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Class getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class beanClass) {
		this.beanClass = beanClass;
	}

	private Class beanClass;
}
