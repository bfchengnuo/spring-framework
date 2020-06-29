package com.bfchengnuo.context;

import com.bfchengnuo.domain.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 测试构建
 *
 * @author 冰封承諾Andy
 * @date 2020/6/17
 */
public class BasicDemo {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(BasicDemo.class);

		// 启动上下文
		applicationContext.refresh();
		User user = applicationContext.getBean("user", User.class);
		System.out.println(user);

		applicationContext.close();
	}

	@Bean
	private User user() {
		return new User("mps",12);
	}
}
