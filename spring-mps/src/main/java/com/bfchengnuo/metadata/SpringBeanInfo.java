package com.bfchengnuo.metadata;

import com.bfchengnuo.domain.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.core.env.*;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Spring Bean 配置元信息
 * 主要为 BeanDefinition 提供信息支持，包含的是类相关基础信息
 * 分为三类：
 * @see GenericBeanDefinition
 * @see RootBeanDefinition
 * @see AnnotatedBeanDefinition
 * 其中，注解的元信息比较特殊，是个接口，具体由两个实现，分为 JDK 反射方式和 ASM 方式。可参考 {@link AnnotatedGenericBeanDefinition}
 *
 * 配置信息的装载由 {@link BeanDefinitionReader} 来实现，它的常见实现类：
 * @see XmlBeanDefinitionReader
 * @see PropertiesBeanDefinitionReader 依赖 {@link java.util.Properties}
 * @see AnnotatedBeanDefinitionReader 注解是特殊实现
 * 其他拓展：
 * @see ClassPathScanningCandidateComponentProvider 注解信息，例如 @Component 注解扫描（registerDefaultFilters）
 * @see CommonAnnotationBeanPostProcessor 来处理 JavaEE 相关的注解，例如 @Resource，以及后置处理相关等
 *
 * ----------------------------------------------------------------------------
 * Bean 属性信息
 * 属性元信息顶层为 {@link PropertyValues}，可以被迭代。
 * 可以认为是组合模式（组合了多个 PropertyValue）
 * 常用实现：
 * @see MutablePropertyValues 可修改实现
 * @see PropertyValue 元素成员信息
 *
 * ----------------------------------------------------------------------------
 * 外部化配置
 * 一般通过 {@link org.springframework.context.annotation.PropertySource} 注解来注入处理，JDK8 以后可以重复使用；
 * 并且是有顺序的，指的是插入顺序；
 * @see PropertySource
 * @see MutablePropertySources
 * @see Environment
 * @see YamlProcessor yaml 支持，有两个具体实现
 *
 * ----------------------------------------------------------------------------
 * 其他，扩展自定义 XML 解析，与 BeanDefinition 注册的相关内容略过，例如 Mybatis 适配 Spring 就可能会用到
 * 需要自定义 XSD 约束；
 * 自己继承/实现 {@link org.springframework.beans.factory.xml.NamespaceHandlerSupport} 来进行 XML 标签的注册；
 * 自己继承/实现 {@link org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser} 来进行 xml 解析；
 * 类似 SPI 配置：META-INF/spring.handlers
 * xsd 映射：META-INF/spring.schemas
 * 最后解析完毕后即可获得 BeanDefinition.
 *
 * @author 冰封承諾Andy
 * @date 2020/7/9
 * @see com.bfchengnuo.ioc.bean.BeanDefinitionDemo 构建参考
 */
public class SpringBeanInfo {
	/**
	 * from https://github.com/bfchengnuo/JavaReplay/blob/master/DiveInSpring/src/main/java/com/bfchengnuo/diveinspring/metadata/BeanConfigurationMetadataDemo.java
	 */
	public static void main(String[] args) {
		// 通用型 BeanDefinition 声明
		BeanDefinition bd = new GenericBeanDefinition();
		bd.setAttribute("name", "mps");

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(User.class);
		builder.addPropertyValue("name", "skye");
		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
		// 附加属性，不会影响 Bean 的实例化，辅助作用
		beanDefinition.setAttribute("name", "mps");
		beanDefinition.setSource(SpringBeanInfo.class);


		// BeanFactory 的默认实现为 DefaultListableBeanFactory
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		// 注册 BeanDefinition
		beanFactory.registerBeanDefinition("user", beanDefinition);
		// 添加后置处理器
		beanFactory.addBeanPostProcessor(new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				if (ObjectUtils.nullSafeEquals("user", beanName)
						&& User.class.equals(bean.getClass())) {
					// 取出附加信息
					BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
					System.out.println("PostProcessor - bd - name: " + bd.getAttribute("name"));
					// 这里可以通过 bean 对象来修改属性
				}
				// 可以返回一个修改后的 bean 或者新 bean（wrap、代理）
				// 返回 null 表示不做修改
				return bean;
			}
		});

		// 依赖查找
		User user = beanFactory.getBean("user", User.class);
		System.out.println(user);
	}

	private void readXml() {
		// 创建 IoC 容器
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
		reader.loadBeanDefinitions("classpath:/META-INF/xxx.xml");
		// ...
	}

	private static void propertySource() {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(SpringBeanInfo.class);

		// 扩展 Environment 中的 PropertySource
		Map<String, Object> map = new HashMap<>();
		map.put("name", "mps");
		PropertySource<Map<String, Object>> propertySource = new MapPropertySource("my-property", map);

		applicationContext.getEnvironment().getPropertySources().addFirst(propertySource);

		applicationContext.refresh();
		System.out.println(applicationContext.getEnvironment().getPropertySources());
		applicationContext.close();
	}

	/**
	 * yaml 扩展
	 * 以此可以处理 SB 中非 application.yml 标准的 yml 资源；
	 * 使用 @PropertySource 的时候 factory 属性可指定此类型。
	 * 注解的 name 和 val 会被传递到此方法的参数中
	 */
	static class YamlPropertySourceFactory implements PropertySourceFactory {
		@Override
		public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
			YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
			yamlPropertiesFactoryBean.setResources(resource.getResource());
			Properties yamlProperties = yamlPropertiesFactoryBean.getObject();
			return new PropertiesPropertySource(name, yamlProperties);
		}
	}
}
