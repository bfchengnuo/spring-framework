package com.bfchengnuo.event;

import java.util.EventListener;
import java.util.EventObject;
import java.util.Observable;
import java.util.Observer;

/**
 * Spring 事件驱动
 * 基于观察者模型，兼容 JDK 的 AP；
 * 在 GUI(AWT) 编程方面使用的非常多，最开始的 JavaBeans 也是为了解决 GUI 问题。
 * 也有一些注解实现，例如我们熟悉的 {@link javax.annotation.PostConstruct}
 *
 * 在 Spring 中大部分都是单事件监听；
 *
 * @author 冰封承諾Andy
 * @date 2020/7/12
 * @see Observer JDK 观察者
 * @see Observable JDK 被观察者
 * @see EventListener
 * @see EventObject
 */
public class ObserverDemo {
	public static void main(String[] args) {
		Observable observable = new Observable();
		// 添加观察者
		observable.addObserver(new EventObserver());
		// 发布事件
		observable.notifyObservers("Mps!");
	}

	/**
	 * 自定义的观察者
	 * EventListener 标记接口
	 */
	static class EventObserver implements Observer, EventListener {

		@Override
		public void update(Observable o, Object arg) {
			System.out.println("收到事件：" + arg);
		}
	}

	/**
	 * 自定义扩展 Observable 被观察者
	 * 因为 JDK 的 Observable 的 setChanged 方法是非 public 的，无法直接调用
	 * 也就无法触发事件
	 *
	 * 解决方法可以 setChanged 变为 public；
	 * 也可以修改 notifyObservers 方法。
	 */
	static class EventObservable extends Observable {
		@Override
		public synchronized void setChanged() {
			super.setChanged();
		}

		@Override
		public void notifyObservers(Object arg) {
			setChanged();
			// 这里可以将 arg 进行包装一下，使用 JDK 的 EventObject
			super.notifyObservers(arg);
			clearChanged();
		}
	}
}
