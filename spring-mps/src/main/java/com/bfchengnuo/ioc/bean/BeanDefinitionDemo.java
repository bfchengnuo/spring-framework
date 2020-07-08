package com.bfchengnuo.ioc.bean;

import com.bfchengnuo.domain.User;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

/**
 * BeanDefinition test
 *
 * Bean 名称生成：
 * @see  org.springframework.beans.factory.support.BeanNameGenerator
 * @see org.springframework.beans.factory.support.DefaultBeanNameGenerator
 * @see  org.springframework.context.annotation.AnnotationBeanNameGenerator
 * PS: 别名方式
 *
 * @see BeanDefinitionRegistry#registerBeanDefinition(String, BeanDefinition) BeanDefinition 注册中心
 * 具体实现：{@link DefaultListableBeanFactory#registerBeanDefinition(String, BeanDefinition)}
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
	 * @param registry 注册器
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
