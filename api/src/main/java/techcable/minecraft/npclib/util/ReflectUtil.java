package techcable.minecraft.npclib.util;

import java.lang.reflect.Field;

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
}
