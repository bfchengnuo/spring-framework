package com.bfchengnuo.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.*;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.ErrorHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * Spring 事件大纲
 * Spring 事件的基础（核心）：
 * @see ApplicationEvent
 * @see ApplicationListener
 * @see ApplicationEventPublisher
 * @see ApplicationEventMulticaster
 *
 * 注册监听器的几种方法：
 * - 接口方式，参考 {@link #simpleContext()}
 * - Spring Bean 方式，实现 ApplicationListener 接口。
 * - 注解方式，@EventListener; 此方式不能使用基本的上下文 GenericApplicationContext
 * PS：因为发布过程可能会涉及父类的重复触发，所以解决重复触发的问题，可以使用一个 Set 集合的 add 进行判断；
 * @see #onEvent(ApplicationEvent)
 *
 * Spring 事件的发布：
 * - 通过 {@link ApplicationEventPublisher} ，DI(Aware) 获取。
 * - 通过 {@link ApplicationEventMulticaster}，DI 和依赖查找都可，主要对应一对多关系。
 * @see AbstractApplicationContext#initApplicationEventMulticaster()
 * ApplicationEventPublisher 其实是基于 ApplicationEventMulticaster 来实现的，
 * 通过 ApplicationContext 作为联系的纽带
 *
 * ApplicationContextEvent 派生事件：
 * @see ContextRefreshedEvent
 * @see ContextStartedEvent
 * @see ContextStoppedEvent
 * @see ContextClosedEvent
 *
 * Spring Payload 事件：
 * 简化 Spring 事件发送，关注事件源主体；
 * 发送方法：{@link ApplicationEventPublisher#publishEvent(Object)}
 * 也就是，发送事件的时候并不需要 new 一个 ApplicationEvent 类型。
 * @see PayloadApplicationEvent
 *
 * 自定义 Spring 事件：
 * 1 扩展 {@link ApplicationEvent}
 * 2 实现 {@link ApplicationListener}
 * 3 注册 {@link ApplicationListener}，例如使用 addApplicationListener()
 * 4 发布自定义事件，使用 1 中扩展的类型。
 * @see MySpringEvent
 *
 * 同步和异步广播：
 * 默认都是同步模式，这种也便于理解。在 Spring 中大部分同步就可以解决问题，并且比较适合；线程用在事件处理上还是有点划不来。
 * 如果是涉及 Web 方面，这种有超时的可能，一直等待就显得很慢，这种情况可以异步试试。
 * @see SimpleApplicationEventMulticaster#setTaskExecutor(Executor) 切换方式
 * 与 @Async 的区别是，使用 setTaskExecutor 设置后是全局异步，使用注解只能达到部分异步，并不会影响全局。
 * 使用注解无法直接实现动态切换异步/同步，如果需要自定义线程池，只需要创建一个 Executor 类型，名为 taskExecutor 的 Bean。
 * @see #asyncEventHandler()
 *
 * 事件异常处理：参考 {@link SimpleApplicationEventMulticaster#setErrorHandler(ErrorHandler)}
 * 这个方法不是面向接口的，需要进行转为 SimpleApplicationEventMulticaster
 * @see #asyncEventHandler()
 *
 * 与 SpringBoot 事件联系：
 * 基于 Spring 事件，新增了一些 SB 特有的事件类型；SC 中也差不多。
 * 参考：https://bfchengnuo.com/2020/04/18/SpringBoot%E7%BC%96%E7%A8%8B%E6%80%9D%E6%83%B3%E4%B9%8B%E4%B8%8D%E6%B1%82%E7%94%9A%E8%A7%A3/
 *
 * @author 冰封承諾Andy
 * @date 2020/7/13
 * @see ApplicationContextEvent
 * @see AbstractApplicationContext
 * @see Async
 * @see EnableAsync
 * @see org.springframework.context.support.ApplicationContextAwareProcessor#invokeAwareInterfaces(Object) 注入顺序参考
 *
 * @see SimpleApplicationEventMulticaster 原理参考
 * @see ErrorHandler 异常处理
 * @see ResolvableType 泛型处理
 */
public class SpringEventDemo {
	public static void main(String[] args) {
		simpleContext();
		// ApplicationContext 本身就是一个 ApplicationEventPublisher
		// AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		// context.publishEvent("...");
	}

	private static void simpleContext() {
		GenericApplicationContext applicationContext = new GenericApplicationContext();
		applicationContext.addApplicationListener(event -> System.out.println("接受到 Spring 事件：" + event));

		applicationContext.refresh();
		applicationContext.close();
	}

	/**
	 * 使用注解来进行监听 Spring 事件
	 * 支持异步，使用 @Async，不同的线程处理;
	 * PS：使用异步之前需要先开启，@EnableAsync;
	 * 可以通过 @Order 控制顺序
	 * @param event 事件源
	 */
	@EventListener
	public void onEvent(ApplicationEvent event) {
		System.out.println("Spring event: " + event);
	}

	/**
	 * 异步事件示例
	 */
	public void asyncEventHandler() {
		GenericApplicationContext context = new GenericApplicationContext();

		// 1.添加自定义 Spring 事件监听器
		context.addApplicationListener((ApplicationListener<MySpringEvent>) event ->
				System.out.printf("[线程 ： %s] 监听到事件 : %s\n", Thread.currentThread().getName(), event));

		// 2.启动 Spring 应用上下文
		// 初始化 ApplicationEventMulticaster
		context.refresh();

		// 依赖查找 ApplicationEventMulticaster
		ApplicationEventMulticaster applicationEventMulticaster =
				context.getBean(AbstractApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME,
						ApplicationEventMulticaster.class);

		// 判断当前 ApplicationEventMulticaster 是否为 SimpleApplicationEventMulticaster
		if (applicationEventMulticaster instanceof SimpleApplicationEventMulticaster) {
			SimpleApplicationEventMulticaster simpleApplicationEventMulticaster =
					(SimpleApplicationEventMulticaster) applicationEventMulticaster;
			// 切换 taskExecutor，便于区分，设置线程池的名称
			ExecutorService taskExecutor = newSingleThreadExecutor(new CustomizableThreadFactory("my-spring-event-thread-pool"));
			// 同步 -> 异步
			// 因为 SimpleApplicationEventMulticaster 才有这个方法，所以需要强转
			simpleApplicationEventMulticaster.setTaskExecutor(taskExecutor);

			// 添加 ContextClosedEvent 事件处理
			// 监听 ContextClosedEvent 来自动关闭线程池
			applicationEventMulticaster.addApplicationListener((ApplicationListener<ContextClosedEvent>) event -> {
				if (!taskExecutor.isShutdown()) {
					taskExecutor.shutdown();
				}
			});

			simpleApplicationEventMulticaster.setErrorHandler(e -> {
				System.err.println("当 Spring 事件异常时，原因：" + e.getMessage());
			});
		}

		// 测试异常处理
		context.addApplicationListener((ApplicationListener<MySpringEvent>) event -> {
			throw new RuntimeException("故意抛出异常");
		});

		// 3. 发布自定义 Spring 事件
		context.publishEvent(new MySpringEvent("Hello,World"));

		// 4. 关闭 Spring 应用上下文（ContextClosedEvent）
		context.close();
	}

	/**
	 * 自定义 Spring 事件
	 */
	static class MySpringEvent extends ApplicationEvent{
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new {@code ApplicationEvent}.
		 *
		 * @param source the object on which the event initially occurred or with
		 *               which the event is associated (never {@code null})
		 */
		public MySpringEvent(String source) {
			super(source);
		}

		@Override
		public String getSource() {
			return (String) super.getSource();
		}

		public String getMessage() {
			return getSource();
		}
	}
}
