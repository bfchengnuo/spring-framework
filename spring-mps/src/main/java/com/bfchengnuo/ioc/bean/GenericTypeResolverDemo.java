package com.bfchengnuo.ioc.bean;

import com.bfchengnuo.domain.User;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 泛型的解析
 * 运行阶段泛型虽然会擦除，但是依然保存在字节码中，通过反射可以获得泛型信息；
 * 在 Spring 中也提供了一些工具类和对泛型信息的封装；
 *
 * @see GenericTypeResolver
 * @see GenericTypeResolver#getTypeVariableMap(Class)
 * @see ResolvableType 4.0 新增泛型优化实现，不变类型，模版：工厂 for*，转换 as*，处理 resolve*
 * @author 冰封承諾Andy
 * @date 2020/7/11
 * @see MethodParameter Spring 对方法参数的封装
 */
public class GenericTypeResolverDemo {
	public static void main(String[] args) throws NoSuchMethodException {
		Method method = GenericTypeResolverDemo.class.getMethod("mps");
		Method method1 = GenericTypeResolverDemo.class.getMethod("mps1");
		Class<?> returnType = GenericTypeResolver.resolveReturnType(method, GenericTypeResolverDemo.class);
		// 普通返回值类型
		System.out.println(returnType);

		// 获取返回类型中的泛型的参数（要是具体参数）
		System.out.println(GenericTypeResolver.resolveReturnTypeArgument(method1, List.class));
	}

	public static void newApi() {
		ResolvableType resolvableType = ResolvableType.forClass(MpsList.class);
		System.out.println("SuperType: " + resolvableType.getSuperType());
		System.out.println("SuperType.SuperType: " + resolvableType.getSuperType().getSuperType());

		System.out.println(resolvableType.asCollection().resolveGeneric(0)); // user.class
	}

	public static String mps() {
		return "mps";
	}

	public static List<User> mps1() {
		return null;
	}

	static class MpsList extends ArrayList<User>{
		private static final long serialVersionUID = 1L;
	}
}
