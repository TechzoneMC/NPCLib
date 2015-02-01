package net.techcable.npclib.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.*;

@Getter
public class ReflectUtil {
	
	private ReflectUtil() {}
	
	public static Field makeField(Class<?> clazz, String fieldName) {
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException ex) {
			throw new RuntimeException(ex);
		}	
	}
	
	public static void setField(Field field, Object objToSet, Object value) {
		field.setAccessible(true);
		try {
			field.set(objToSet, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Method makeMethod(Class<?> clazz, String methodName, Class<?>... paramaters) {
	    try {
	        return clazz.getDeclaredMethod(methodName, paramaters);
	    } catch (NoSuchMethodException ex) {
	        throw new RuntimeException(ex);
	    }
	}
	
	public static <T> T callMethod(Method method, Object instance, Object... paramaters) {
	    method.setAccessible(true);
	    try {
	        return (T) method.invoke(instance, paramaters);
	    } catch (IllegalArgumentException | IllegalAccessException ex) {
	        throw new RuntimeException(ex);
	    } catch (InvocationTargetException ex) {
		    throw new RuntimeException(ex.getCause());
		}
	}
}
