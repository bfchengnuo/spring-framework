package com.bfchengnuo.ioc;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * IoC 是一个概念，具体实现有多种，例如 EJB，Spring、SPI、JNDI、Servlet、GoogleGuice 等等。
 *
 * IoC 的主要职责：
 * 依赖处理（依赖注入和依赖查找）；
 * 生命周期管理（容器、托管的资源）；
 * 配置（容器、外部化、托管的资源的配置）；
 *
 * 依赖注入的方式：setter、构造器、字段、方法参数、Aware接口
 *
 * IoC 主要启动过程，声明周期：{@link AbstractApplicationContext#refresh()}
 *
 * @author 冰封承諾Andy
 * @date 2020/7/2
 */
public class Overview {
	/**
	 * 依赖查找：{@link Lookup}
	 * 依赖查找和依赖注入的 Bean 并不一定是同源, 依赖注入可以注入内建 Bean，或者非 Bean 对象；
	 * BeanFactory 提供了基本的容器功能，而 ApplicationContext 作为其的一个超集（或者子接口），提供了更多的扩展功能；
	 *
	 * 内建 Bean 主要有：
	 * - {@link org.springframework.core.env.Environment}
	 * - {@link org.springframework.context.MessageSource} 国际化
	 * - {@link org.springframework.context.LifecycleProcessor}
	 * - {@link org.springframework.context.event.ApplicationEventMulticaster}
	 * - systemProperties type: {@link java.util.Properties}，Java 系统属性
	 * - SystemEnvironment - Map，环境变量(user level)
	 *
	 * 内建可查找依赖：
	 * - {@link org.springframework.context.annotation.ConfigurationClassPostProcessor}
	 * - {@link org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor} @Autowire and @Value
	 * - {@link org.springframework.context.annotation.CommonAnnotationBeanPostProcessor} jsr-250 e.g.: @PostConstruct
	 * - {@link org.springframework.context.event.EventListenerMethodProcessor} @EventListener
	 * - {@link org.springframework.context.event.DefaultEventListenerFactory} adapter ApplicationEventListener
	 * - PersistenceAnnotationBeanPostProcessor (JPA support)
	 * 助记：{@link org.springframework.context.annotation.AnnotationConfigUtils#registerAnnotationConfigProcessors(BeanDefinitionRegistry, Object)}
	 *
	 * @see org.springframework.context.support.AbstractRefreshableApplicationContext
	 * @see DefaultListableBeanFactory 单一、集合、层次 类型
	 * @see HierarchicalBeanFactory 层次类型
	 *
	 * @see org.springframework.context.annotation.ConfigurationClass
	 */
	public static void main(String[] args) {
		// 创建 BeanFactory 容器，DefaultListableBeanFactory 作为默认实现参考 AbstractRefreshableApplicationContext
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
		// 加载配置
		reader.loadBeanDefinitions("classpath:/di-context.xml");
		System.out.println(beanFactory.getBean("user"));
	}
}
