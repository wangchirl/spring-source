package com.shadow.service;

import com.shadow.spring.Autowired;
import com.shadow.spring.BeanNameAware;
import com.shadow.spring.Component;
import com.shadow.spring.InitializingBean;

/**
 * @author shadow
 * @create 2020-08-15
 * @description
 */
@Component("userService")
public class UserService implements BeanNameAware ,InitializingBean {


	@Autowired
	private OrderService orderService;

	public void hello() {
		System.out.println("Hello World~");
		System.out.println(orderService);
	}

	@Override
	public void setBeanName(String name) {
		System.out.println("set bean name  : " + name);
	}

	@Override
	public void afterPropertiesSet() {
		System.out.println("init");
	}
}
