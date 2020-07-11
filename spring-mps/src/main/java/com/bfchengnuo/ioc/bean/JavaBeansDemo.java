package com.bfchengnuo.ioc.bean;

import com.bfchengnuo.domain.User;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.Arrays;

/**
 * JavaBeans 基础
 * SpringCore 可以说是基于 JavaBeans 的；
 * 其中的 {@link org.springframework.beans.BeanWrapper} 也是源于 JavaBeans，进行了一定的扩展或者说增强
 * <p>
 * JavaBeans 的要求很宽松，仅仅是关注 getter 和 setter 方法；
 * 即使属性与 setter/getter 不一致也不影响，甚至可以不存在。
 *
 * @author 冰封承諾Andy
 * @date 2020/7/10
 */
public class JavaBeansDemo {
	public static void main(String[] args) throws IntrospectionException {
		// 排除 object，例如其中的 getClass 属性
		BeanInfo beanInfo = Introspector.getBeanInfo(User.class, Object.class);
		// 解析属性
		Arrays.stream(beanInfo.getPropertyDescriptors()).forEach(propertyDescriptor -> {
			// 获取 getter 方法
			propertyDescriptor.getReadMethod();
			// 获取 setter 方法
			propertyDescriptor.getWriteMethod();
			System.out.println(propertyDescriptor);
		});
		System.out.println();

		// 解析方法
		Arrays.stream(beanInfo.getMethodDescriptors()).forEach(System.out::println);
	}
}
