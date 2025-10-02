package cn.xiaozhou233.juicetools.tools;

import java.lang.reflect.Field;

public class Reflect {
    public static Object getValue(String className, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = Class.forName(className);
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }
}
