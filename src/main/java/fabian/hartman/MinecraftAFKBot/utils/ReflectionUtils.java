package fabian.hartman.MinecraftAFKBot.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtils {
    private ReflectionUtils() {
    }

    public static List<Field> getAllFields(final Object object) {
        List<Field> allFields = new ArrayList<>();
        allFields.addAll(getDeclaredFields(object));
        allFields.addAll(getInheritedFields(object));
        return allFields;
    }

    public static void setField(Field field, Object obj, Object value) {
        try {
            field.setAccessible(true);

            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getField(Field field, Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Field> getDeclaredFields(final Object object) {
        return new ArrayList<>(Arrays.asList(object.getClass().getDeclaredFields()));
    }

    private static List<Field> getInheritedFields(final Object object) {
        List<Field> inheritedFields = new ArrayList<>();
        Class clazz = object.getClass();
        while (clazz.getSuperclass() != null) {
            Class superclass = clazz.getSuperclass();
            inheritedFields.addAll(Arrays.asList(superclass.getDeclaredFields()));
            clazz = superclass;
        }
        return inheritedFields;
    }
}