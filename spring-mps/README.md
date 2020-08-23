常见的几个问题：

## 为什么说ObjectFactory提供的是延迟查找？

ObjectFactory 或者说 ObjectProvider 可关联某一类型的 Bean，它并不是实时查找，而是在 getObject() 调用的时候才会去查找；

相当于某一类型的 Bean 依赖查找代理对象（通过代理对象实现延迟查找）。

PS：区别于 ApplicationContext 的 getBean() 是直接查找。可能需要配合 @Lazy 使用。