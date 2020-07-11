package com.bfchengnuo.ioc.bean;

import com.bfchengnuo.domain.User;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据绑定相关
 * 一般连同数据类型转换使用，多用在 XML 中，注解方式是不需要转换的
 *
 * 默认 DataBinder 会忽略未知属性，这些特性可以进行调整，通过 setter 方法;
 * 即使绑定有问题也不会抛异常，只能通过 {@link BindingResult} 来查看。
 *
 * @see BeanDefinition#getPropertyValues()
 * @see PropertyValues 作为元数据
 * @see BeanWrapper 使用 {@link BeanPropertyBindingResult} 的时候会关联 BeanWrapper，build 方法生成
 * @author 冰封承諾Andy
 * @date 2020/7/10
 * @see org.springframework.beans.propertyeditors.ClassEditor string -> class，还有一些其他的内建实现，用于数据转换
 *
 * 从 Spring3 开始提供了新的类型转换工具：
 * @see Converter 根据泛型来使用，擦除问题
 * @see GenericConverter
 * @see ConditionalConverter
 * @see ConversionServiceFactoryBean 由它注册各类 Converter
 * @see ConversionService 统一类型转换服务
 *
 * 关于类型转换的补充：
 * AbstractApplicationContext -> 查找 "conversionService" ConversionService Bean
 * -> 传递到 ConfigurableBeanFactory#setConversionService(ConversionService) 也就是塞到 BeanFactory 中
 * -> AbstractAutowireCapableBeanFactory#instantiateBean
 * -> 被 AbstractBeanFactory#getConversionService 读取
 *
 * -> BeanDefinition -> BeanWrapper -> 属性转换（数据来源：PropertyValues）->
 * setPropertyValues(PropertyValues) -> TypeConverter#convertIfNecessnary
 * TypeConverterDelegate#convertIfNecessnary  -> PropertyEditor or ConversionService
 */
public class DataBinderDemo {
	public static void main(String[] args) {
		User user = new User();
		DataBinder dataBinder = new DataBinder(user, "user");

		Map<String, Object> map = new HashMap<>();
		map.put("name", "mps");
		map.put("age", "23");
		// 支持复合类型
		map.put("obj.id", "23");
		PropertyValues propertySource = new MutablePropertyValues(map);

		// 进行数据绑定
		dataBinder.bind(propertySource);
		System.out.println(user);

		BindingResult bindingResult = dataBinder.getBindingResult();
		System.out.println(bindingResult);
	}
}
