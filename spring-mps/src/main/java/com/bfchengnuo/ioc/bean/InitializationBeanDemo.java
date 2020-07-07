package com.bfchengnuo.ioc.bean;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 初始化生命周期中，常见的有三种方式进行自定义
 * - 使用 @PostConstruct 注解
 * - 实现 InitializingBean 接口
 * - 通过 xml 或者 @Bean 配置 init 方法
 * 顺序就是以上顺序。
 * 销毁也是类似，@PreDestroy/DisposableBean
 *
 * 拓展：@Lazy
 *
 * @author 冰封承諾Andy
 * @date 2020/7/4
 */
@Component
public class InitializationBeanDemo implements InitializingBean {
	@PostConstruct
	private void init() {
		System.out.println("init in @PostConstruct");
	}

	public static void main(String[] args) {

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("init in InitializingBean");
	}
}
