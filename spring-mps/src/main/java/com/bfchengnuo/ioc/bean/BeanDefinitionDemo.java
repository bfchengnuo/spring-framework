package com.bfchengnuo.ioc.bean;

import com.bfchengnuo.domain.User;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

/**
 * BeanDefinition test
 *
 * Bean 名称生成：
 * @see  BeanNameGenerator
 * @see DefaultBeanNameGenerator
 * @see  AnnotationBeanNameGenerator
 * PS: 别名方式
 *
 * @see BeanDefinitionRegistry#registerBeanDefinition(String, BeanDefinition) BeanDefinition 注册中心
 * 具体实现：{@link DefaultListableBeanFactory#registerBeanDefinition(String, BeanDefinition)}
 *
 * root（无继承）：{@link RootBeanDefinition}
 * Generic（普通，例如有继承关系）：{@link GenericBeanDefinition}
 * 合并操作（Generic）：{@link ConfigurableBeanFactory#getMergedBeanDefinition(String)}
 * 默认情况一般都是 Generic，合并之后，可能变为 Root，或者其父变为 Root
 *
 * @see AbstractBeanFactory#doGetBean(String, Class, Object[], boolean)  注意创建 Bean 的过程
 *
 * 非主流实现：{@link org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor}
 * 可以拦截 Bean 创建，在之前进行处理或者替换，例如 RPC
 *
 * @see AbstractAutowireCapableBeanFactory bean create 的主要实现，例如构造方法注入、后置处理器的调用等
 * @see org.springframework.beans.factory.SmartInitializingSingleton 一种特殊的回调，SpringBean 初始化完成阶段
 * @author 冰封承諾Andy
 * @date 2020/7/4
 */
public class BeanDefinitionDemo {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(BeanDefinitionDemo.class);

		// 通过 BeanDefinition 注册 Bean
		regUserBean(applicationContext, "user");

		applicationContext.refresh();
		System.out.println(applicationContext.getBean(User.class));
		applicationContext.close();
	}

	/**
	 * 通过 BeanDefinitionBuilder 来注册 Bean
	 * @param registry 注册器, applicationContext 也可
	 * @param beanName 名称，空则使用 BeanDefinitionReaderUtils 生成
	 */
	private static void regUserBean(BeanDefinitionRegistry registry, String beanName) {
		// 通过 BeanDefinitionBuilder 构建
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
		builder.addPropertyValue("name", "mps")
				.addPropertyValue("age", 12);

		if (StringUtils.hasText(beanName)) {
			registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
			return;
		}
		BeanDefinitionReaderUtils.registerWithGeneratedName(builder.getBeanDefinition(), registry);
	}

	/**
	 * BeanDefinition 简单构建
	 */
	private static void simpleBeanDefinition() {
		// 通过 BeanDefinitionBuilder 构建
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
		builder.addPropertyValue("name", "mps")
				.addPropertyValue("age", 12);
		// beanDefinition 并非是最终状态，还可以通过 setter 方法进行调整
		BeanDefinition beanDefinition = builder.getBeanDefinition();


		// 通过 AbstractBeanDefinition 以及派生类构建
		GenericBeanDefinition genericBeanDefinition = new GenericBeanDefinition();
		genericBeanDefinition.setBeanClass(User.class);
		MutablePropertyValues propertyValues = new MutablePropertyValues();
		propertyValues.addPropertyValue("name", "skye");
		// add 可链式调用
		propertyValues.add("age", 22);
		genericBeanDefinition.setPropertyValues(propertyValues);
	}
}
