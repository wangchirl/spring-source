package com.shadow.service;

import com.shadow.spring.BeanPostProcessor;
import com.shadow.spring.Component;

/**
 * @author shadow
 * @create 2020-08-15
 * @description
 */
@Component("shadowBeanPostProcessor")
public class ShadowBeanPostProcessor implements BeanPostProcessor {
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		System.out.println("before init");
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		System.out.println("after init");
		return bean;
	}
}
