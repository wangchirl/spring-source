package com.shadow.spring;

/**
 * @author shadow
 * @create 2020-08-15
 * @description
 */
public interface BeanPostProcessor {

	Object postProcessBeforeInitialization(Object bean, String beanName);

	Object postProcessAfterInitialization(Object bean, String beanName);

}
