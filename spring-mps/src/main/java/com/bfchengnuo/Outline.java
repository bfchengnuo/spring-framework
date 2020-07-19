package com.bfchengnuo;

import com.bfchengnuo.ioc.Lookup;
import com.bfchengnuo.ioc.Overview;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.DefaultEventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.PropertySourcesPropertyResolver;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.concurrent.locks.Condition;

/**
 * Spring 核心知识大纲整理
 * 主要部分：IoC、事件、元信息
 * --------------------------------------------------------------------------------------
 * IoC 基础
 * IoC 是一个概念，具体实现有多种，例如 EJB，Spring、SPI、JNDI、Servlet、GoogleGuice 等等。
 *
 * IoC 的主要职责：
 * 依赖处理（依赖注入和依赖查找）；
 * 生命周期管理（容器、托管的资源）；
 * 配置（容器、外部化、托管的资源的配置）；
 *
 * ======BeanFactory======
 * 参考：{@link Overview}
 * 最基本的 BeanFactory 实现是 DefaultListableBeanFactory，不支持特殊 Bean 的自动注册，
 * 例如各种后置处理器，需要由 DefaultListableBeanFactory 的方法进行注册；
 * 而如果是 ApplicationContext 则只要定义在 IoC 中就会自动注册。
 * 相比 ApplicationContext，它只有在手动调用的 getBean 的时候才会进行处理依赖，bean definition 相关
 * 并且，默认简单实现 DefaultListableBeanFactory 也不支持注解驱动，当然可以尝试手动加入 {@link CommonAnnotationBeanPostProcessor} 解决。
 *
 * 新增的 {@link SmartInitializingSingleton} 在非 ApplicationContext 也不会进行执行，
 * 可以通过手动调用 BeanFactory 的 preInstantiateSingletons 方法,
 * 当执行 {@link SmartInitializingSingleton} 回调，意味着 Spring Bean 已经全部完成初始化。
 * 对比后置处理器的可以操作 Bean 的状态，此时 bean 还未被完全初始化；而在 SmartInitializingSingleton 可大胆使用。
 *
 *
 * @see DefaultListableBeanFactory 单一、集合、层次 类型
 * @see AbstractRefreshableApplicationContext 组合 DefaultListableBeanFactory
 * @see HierarchicalBeanFactory 层次类型
 *
 * @see AbstractApplicationContext BeanFactory 增强 - 上下文
 * @see BeanFactoryPostProcessor
 * @see BeanPostProcessor
 *
 * @see ConfigurationClass
 * @see AnnotatedBeanDefinitionReader
 *
 * ======Bean相关======
 * BeanDefinition
 * 默认情况下，Spring 启动过程是先将元信息解析封装为 BeanDefinition，进行依赖查找和注入的时候进行对应的 BeanDefinition 到 Spring Bean 的转化；
 * 其中包含 Bean 名字的生成（如果没指定），BeanDefinition 之间的合并；
 * 根据继承和组合关系可分为两类 BeanDefinition，无论那种都需要执行合并流程。
 * @see com.bfchengnuo.ioc.bean.BeanDefinitionDemo
 *
 * 数据绑定
 * 多用于 XML 配置和外部化配置等，注解基本不需要
 * @see com.bfchengnuo.ioc.bean.DataBinderDemo
 *
 * Spring Bean 配置元信息
 * 参考 BeanDefinition 和外部化配置相关
 * @see com.bfchengnuo.metadata.SpringBeanInfo
 *
 * @see com.bfchengnuo.ioc.bean.GenericTypeResolverDemo 泛型元信息处理
 * @see com.bfchengnuo.ioc.bean.JavaBeansDemo JavaEE 标准
 * @see com.bfchengnuo.ioc.bean.InitializationBeanDemo bean 生命周期中进行自定义的一种方法（区别 BeanPostProcessor）
 *
 * ======作用域======
 * bean 的作用域，主要为单例、原型、自定义；web 方面包含的 session、request 等也是一个道理；
 * 无论那种方式，注入的都是 CGLIB 提升后的对象，一般这个对象是不变的，在实际使用时，会根据 scope 来进行不同的代理规则；
 *
 * Spring 中分为轻量级 Bean（多由 @Bean 等手动注入/定义）与完全 Bean（多是 @Configuration 定义），轻量级不会使用 CGLIB 等提升手段，生命周期与事件也不完全。
 *
 * 自定义 scope 可以使用 beanFactory 来进行注册
 * 需要一个名称 @Scope 的时候使用；
 * 需要一个具体的实现类，实现 Scope 接口，来处理作用域的获取销毁等逻辑
 * 可以参考 request、session 的作用域处理
 * @see Overview#addScope()
 *
 * ======依赖查找======
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
 *
 * 助记：{@link AnnotationConfigUtils#registerAnnotationConfigProcessors(BeanDefinitionRegistry, Object)}
 * 如果某个 Bean 需要急切初始化（加入 IoC），可以尝试将其方法标注为 static；
 *
 * ======依赖注入======
 * 依赖注入的方式：setter、构造器、字段、方法参数、Aware接口
 * 大多数使用的是自动注入。
 * @see DependencyDescriptor 注入描述
 * @see DefaultListableBeanFactory#resolveDependency(DependencyDescriptor, String, Set, TypeConverter)  DI入口
 * PS：构造器注入参考 {@link ConstructorResolver#autowireConstructor(String, RootBeanDefinition, Constructor[], Object[])}
 *
 * 注入的两种特殊情况：
 * 手动注入外部对象：{@link SingletonBeanRegistry#registerSingleton(String, Object)}
 * 不过此方式有限制，无生命周期管理，无法延迟初始化(外部已经初始化完成)，具体实现参考：{@link DefaultSingletonBeanRegistry}
 *
 * 非容器管理对象作为依赖源，无法进行依赖查找，只能依赖注入，无生命周期，无延迟；
 * 相对单体对象，要求要更多；
 * @see Overview#testResolvableDependency()
 * @see ConfigurableListableBeanFactory#registerResolvableDependency(Class, Object)
 *
 * TODO ======循环依赖======
 * Spring 为了解决循环依赖的问题采用了 singletonObjects、earlySingletonObjects、singletonFactories 三个级别的缓存来缓存 bean 对象
 * A、B 两个对象如果循环依赖的话，假设 A 对象先被创建，那么它会被放入 singletonFactories，
 * 当解析它的依赖属性 B 并创建时，发现依赖属性 A，此时会通过 getSingleton 将 A 从 singletonFactories 移动到 earlySingletonObjects，
 * 最终当 B 创建完注入 A 返回之后，再将 B 注入 A 才会将 A 移动到 singletonObjects。
 *
 * @see DefaultSingletonBeanRegistry#getSingleton(String)
 * @see Overview#getBean()
 *
 * ======生命周期======
 * IoC 主要启动过程，生命周期：{@link AbstractApplicationContext#refresh()}
 *
 * --------------------------------------------------------------------------------------
 * Spring 事件
 * @see com.bfchengnuo.event.ObserverDemo JDK 支持
 * @see com.bfchengnuo.event.SpringEventDemo Spring Event
 *
 * --------------------------------------------------------------------------------------
 * Spring 注解
 * 主要关注在包扫描注解 @ComponentScan 上，关于模式注解，在博客的 SB 编程思想有。
 * 从 Spring4 开始，完全支持派生性。
 * @see org.springframework.context.annotation.ComponentScanAnnotationParser#parse(AnnotationAttributes, String)
 * 大多数情况下，包扫描使用 ASM 技术来解析，因为不需要验证、校验、加载，速度会比较快。
 * 不过在 Spring5 中，使用了 @Indexed 来将一部分元信息在编译的时候进行缓存，速度也提高了很多。
 *
 * 从 Spring3 开始陆续支持 @Enable 模块驱动注解，将相关注解融合方便使用；
 * 例如典型的事务、缓存、WebMvc 和 Async 模块。对 SB 的影响很大。
 * @see Import 支持配置类和 Selector，以及不太常用的直接导入 ImportBeanDefinitionRegistrar
 * @see ImportSelector
 * @see ImportBeanDefinitionRegistrar 底层支持
 *
 * 条件注解相关：
 * @see Profile
 * @see Conditional 实现原理参考
 * @see Condition
 * Conditional 实现原理参考：
 * @see ConditionContext 上下文对象
 * @see ConditionEvaluator 条件判断
 * @see ConfigurationCondition.ConfigurationPhase 配置阶段
 * @see ConfigurationClassPostProcessor 处理阶段入口
 * @see ConfigurationClassParser
 *
 * ImportBeanDefinitionRegistrar 构建相关：
 * @see ImportBeanDefinitionRegistrar#registerBeanDefinitions(AnnotationMetadata, BeanDefinitionRegistry)
 * @see AnnotatedGenericBeanDefinition
 * @see BeanDefinitionReaderUtils
 *
 * --------------------------------------------------------------------------------------
 * Environment 抽象
 * 外部化配置相关信息（包括系统、用户变量等，JVM 自动读取）
 * 注入方式：
 * - 使用 EnvironmentAware 回调
 * - 通过 @Autowired 直接注入
 * - 通过 ApplicationContext 获取（ApplicationContext 的获取方式如上面两种）
 * - getBean 通过名称 ConfigurableApplicationContext.ENVIRONMENT_BEAN_NAME
 * 无论那种，获得的都是同一个对象。
 *
 * @see Environment
 * @see PropertyResolver
 * @see PropertySourcesPropertyResolver 类型转换，ConversionService
 * @see ConfigurableEnvironment profiles 相关
 * @see PropertySourcesPlaceholderConfigurer 3.1+ 占位符处理
 * @see EmbeddedValueResolver
 * @see ProfileCondition @Profile 处理
 * @see AutowiredAnnotationBeanPostProcessor 处理 @Value，具体实现 QualifierAnnotationAutowireCandidateResolver
 * @see PropertySource
 *
 * @author 冰封承諾Andy
 * @date 2020/7/9
 */
@ComponentScan
public class Outline {
}
