package com.bfchengnuo;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.locks.Condition;

/**
 * Spring 核心知识大纲整理
 * 主要部分：IoC、事件、元信息
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
 * @see org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition
 * @see org.springframework.beans.factory.support.BeanDefinitionReaderUtils
 *
 * @author 冰封承諾Andy
 * @date 2020/7/9
 */
@ComponentScan
public class Outline {
}
