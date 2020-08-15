package com.shadow.spring;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shadow
 * @create 2020-08-15
 * @description
 */
public class AnnotationConfigApplicationContext {


	private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
	private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
	private List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();


	public AnnotationConfigApplicationContext(Class configClass) {
		// 1.扫描
		List<Class> list = doScan(configClass);
		// 2.注册 BeanDefinition
		registrybd(list);
		// 3.实例化
		createBean();
	}

	private void createBean() {
		// 3.实例化
		for (String beanName : beanDefinitionMap.keySet()) {
			BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
			doCreateBean(beanName, beanDefinition);
		}


	}

	private Object doCreateBean(String beanName, BeanDefinition beanDefinition) {
		// 先去尝试获取bean
		Object bean = null;
		try {

			Class beanClass = beanDefinition.getBeanClass();

			// 实例化
			bean = beanClass.getDeclaredConstructor().newInstance();
			// 4.属性填充
			Field[] fields = beanClass.getDeclaredFields();
			for (Field field : fields) {
				// @Autowired
				if (field.isAnnotationPresent(Autowired.class)) {
					// 得到属性名称 - beanName
					String fieldName = field.getName();
					field.setAccessible(true);
					field.set(bean, getBean(fieldName));
				}
			}

			// 5.*aware接口的回调处理
			if(BeanNameAware.class.isAssignableFrom(beanClass)) {
				((BeanNameAware) bean).setBeanName(beanName);
			}
			// 6.beanPostProcessor - before 方法
			for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
				beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
			}

			// 7.初始化方法 - init-method , @PostConstruct  InitializingBean
			if(InitializingBean.class.isAssignableFrom(beanClass)) {
				((InitializingBean)bean).afterPropertiesSet();
			}

			// 8.beanPostProcessor - after 方法
			for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
				beanPostProcessor.postProcessAfterInitialization(bean, beanName);
			}
			// 是单列的
			if (beanDefinition.getScope().equals("singleton")) {
				// 存入单例池
				singletonObjects.put(beanName, bean);
			}
			// 创建完成
			// 注册 Disposable bean - destroy 方法
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return bean;
	}


	public Object getBean(String beanName) {

		BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
		if (beanDefinition.getScope().equals("prototype")) {
			// 原型
			return doCreateBean(beanName, beanDefinition);
		} else {
			// 单例
			Object bean = singletonObjects.get(beanName);
			if (bean == null) {
				bean = doCreateBean(beanName, beanDefinition);
				singletonObjects.put(beanName, bean);
			}
			return bean;
		}
	}

	private void registrybd(List<Class> list) {
		// registry beanDefinition
		for (Class aClass : list) {
			BeanDefinition bd = new BeanDefinition();
			if (aClass.isAnnotationPresent(Component.class)) {
				Component component = (Component) aClass.getAnnotation(Component.class);
				String beanName = component.value();

				System.out.println(beanName);
				// 存放到 BeanDefinition 对象中
				bd.setBeanClass(aClass);
				// 判断是否单例
				if (aClass.isAnnotationPresent(Scope.class)) {
					Scope scope = (Scope) aClass.getAnnotation(Scope.class);
					String value = scope.value();
					bd.setScope(value);
				} else {
					bd.setScope("singleton");
				}

				// 找到 实现了 beanPostProcessor 相关的类 这些类先于普通 bean 初始化并存入到 一个list 中，使用时直接获取执行方法
				if(BeanPostProcessor.class.isAssignableFrom(aClass)) {
					try {
						BeanPostProcessor postProcessor = (BeanPostProcessor) aClass.getDeclaredConstructor().newInstance();
						beanPostProcessors.add(postProcessor);
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}


				// 存放到 map 中
				beanDefinitionMap.put(beanName, bd);
			}
		}
	}

	private List<Class> doScan(Class configClass) {

		List<Class> list = new ArrayList<>();

		String scanPath = "";
		// 拿到扫描路径
		if (configClass.isAnnotationPresent(ComponentScan.class)) {
			ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
			scanPath = componentScan.value();
		}
		// 路径替换
		scanPath = scanPath.replace(".", "/");
		System.out.println(scanPath);
		// 找到路径下的class文件
		ClassLoader classLoader = AnnotationConfigApplicationContext.class.getClassLoader();
		URL resource = classLoader.getResource(scanPath);
		File dir = new File(resource.getFile());
		// 找到目录下的文件
		File[] files = dir.listFiles();
		for (File file : files) {
			// 加载类
			String path = file.getAbsolutePath();
			path = path.substring(path.indexOf("com"), path.indexOf(".class"));
			path = path.replace("\\", ".");
			try {
				Class<?> aClass = classLoader.loadClass(path);
				list.add(aClass);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return list;
	}


}
