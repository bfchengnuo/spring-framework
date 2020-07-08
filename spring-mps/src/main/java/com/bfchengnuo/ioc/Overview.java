package com.bfchengnuo.ioc;

import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.DefaultEventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Set;

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
 * 手动注入外部对象：{@link SingletonBeanRegistry#registerSingleton(String, Object)}
 * 不过有限制，无生命周期管理，无法延迟初始化(外部已经初始化完成)，具体实现：{@link DefaultSingletonBeanRegistry}
 *
 * 非容器管理对象作为依赖源，无法进行依赖查找，只能依赖注入，无生命周期，无延迟；
 * 相对单体对象，要求更多；
 * @see #testResolvableDependency()
 * @see ConfigurableListableBeanFactory#registerResolvableDependency(Class, Object)
 *
 * @see DependencyDescriptor 注入描述
 * @see DefaultListableBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter) DI入口
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
	 * - {@link Environment}
	 * - {@link MessageSource} 国际化
	 * - {@link LifecycleProcessor}
	 * - {@link ApplicationEventMulticaster}
	 * - systemProperties type: {@link java.util.Properties}，Java 系统属性
	 * - SystemEnvironment - Map，环境变量(user level)
	 *
	 * 内建可查找依赖：
	 * - {@link ConfigurationClassPostProcessor}
	 * - {@link AutowiredAnnotationBeanPostProcessor} @Autowire and @Value
	 * - {@link CommonAnnotationBeanPostProcessor} jsr-250 e.g.: @PostConstruct
	 * - {@link EventListenerMethodProcessor} @EventListener
	 * - {@link DefaultEventListenerFactory} adapter ApplicationEventListener
	 * - PersistenceAnnotationBeanPostProcessor (JPA support)
	 * 助记：{@link AnnotationConfigUtils#registerAnnotationConfigProcessors(BeanDefinitionRegistry, Object)}
	 *
	 * 如果某个 Bean 需要急切初始化（加入 IoC），可以尝试将其方法标注为 static；
	 *
	 * @see AbstractRefreshableApplicationContext
	 * @see DefaultListableBeanFactory 单一、集合、层次 类型
	 * @see HierarchicalBeanFactory 层次类型
	 *
	 * @see ConfigurationClass
	 */
	public static void main(String[] args) {
		// 创建 BeanFactory 容器，DefaultListableBeanFactory 作为默认实现参考 AbstractRefreshableApplicationContext
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
		// 加载配置
		reader.loadBeanDefinitions("classpath:/di-context.xml");
		System.out.println(beanFactory.getBean("user"));
	}

	//---------------------------------------------------------------------------------------------
	// TODO 循环依赖相关
	// Spring 为了解决循环依赖的问题采用了 singletonObjects、earlySingletonObjects、singletonFactories 三个级别的缓存来缓存 bean 对象
	// A、B 两个对象如果循环依赖的话，假设 A 对象先被创建，那么它会被放入 singletonFactories，
	// 当解析它的依赖属性 B 并创建时，发现依赖属性 A，此时会通过 getSingleton 将 A 从 singletonFactories 移动到 earlySingletonObjects，
	// 最终当 B 创建完注入 A 返回之后，再将 B 注入 A 才会将 A 移动到 singletonObjects。
	//---------------------------------------------------------------------------------------------

	/**
	 * 循环依赖参考：{@link DefaultSingletonBeanRegistry#getSingleton(String)}
	 */
	private static void getBean() {

	}

	/**
	 * 非容器管理对象作为依赖源测试
	 * 只能依赖注入，不能依赖查找
	 */
	private static void testResolvableDependency() {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(Overview.class);
		// 早于 bean 初始化，由 refresh 方法内的 invokeBeanFactoryPostProcessors 调用执行
		// 由于 applicationContext#getAutowireCapableBeanFactory 落后于 refresh 执行，所以这里放弃此扩展方式
		applicationContext.addBeanFactoryPostProcessor(beanFactory -> {
			// 此 string 对象可以被注入(类型方面)，不可被查找
			beanFactory.registerResolvableDependency(String.class, "Mps");
		});
		applicationContext.refresh();

		applicationContext.close();
	}

	//---------------------------------------------------------------------------------------------
	// bean 的作用域，主要为单例、原型、自定义；web 方面包含的 session、request 等也是一个道理
	// 无论那种方式，注入的都是 CGLIB 提升后的对象，一般这个对象是不变的，在实际使用时，会根据 scope 来进行不同的代理规则
	//---------------------------------------------------------------------------------------------

	/**
	 * 自定义 scope 可以使用 beanFactory 来进行注册
	 * 需要一个名称 @Scope 的时候使用；
	 * 需要一个具体的实现类，实现 Scope 接口，来处理作用域的获取销毁等逻辑
	 * 可以参考 request、session 的作用域处理
	 */
	private static void addScope() {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(Overview.class);
		applicationContext.addBeanFactoryPostProcessor(beanFactory -> {
			// 注册自定义 scope beanFactory.registerScope("name", );
		});
		applicationContext.refresh();

		applicationContext.close();
	}
}
