package com.shadow;

import com.shadow.config.MyConfig;
import com.shadow.service.UserService;
import com.shadow.spring.AnnotationConfigApplicationContext;

/**
 * @author shadow
 * @create 2020-08-15
 * @description
 *
 *
 *
	AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(MyConfig.class);

	UserService userService = ac.getBean("userService");

	userService.test();
 *
 */
public class Application {
	public static void main(String[] args) {

		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(MyConfig.class);

		UserService userService = (UserService) ac.getBean("userService");

		userService.hello();

	}
}
