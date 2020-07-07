package com.bfchengnuo.ioc;

import com.bfchengnuo.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

/**
 * 依赖查找示例
 *
 * @author 冰封承諾Andy
 * @date 2020/7/2
 */
public class Lookup {
	public static void main(String[] args) {
		// 会自动启动上下文
		BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:/di-context.xml");

		// 实时查找
		User user = beanFactory.getBean("user", User.class);
		System.out.println(user);

		// 延迟查找，或者高版本可以使用 beanFactory.getBeanProvider()
		// ObjectProvider 更加的安全
		@SuppressWarnings("unchecked")
		ObjectFactory<User> lazyUser = (ObjectFactory<User>) beanFactory.getBean("objectFactory");
		System.out.println(lazyUser.getObject());
		ObjectProvider<User> beanProvider = beanFactory.getBeanProvider(User.class);
		System.out.println(beanProvider.getObject());

		lookupCollectionByType(beanFactory);
		lookupCollectionByType( beanFactory);
	}

	/**
	 * 根据类型查找全部实例
	 * 可适用于按照注解查询：{@link ListableBeanFactory#getBeansWithAnnotation(Class)}
	 * @param beanFactory 容器
	 */
	private static void lookupCollectionByType(BeanFactory beanFactory) {
		if (beanFactory instanceof ListableBeanFactory) {
			// key - id
			Map<String, User> userMap = ((ListableBeanFactory) beanFactory).getBeansOfType(User.class);
			System.out.println(userMap);
		}
	}
}
