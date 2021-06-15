package forpleuvoir.hiirosakura.client.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author forpleuvoir
 * @belongsProject suikamod
 * @belongsPackage com.forpleuvoir.chatbubbles
 * @className ReflectionUtils
 * @createTime 2020/10/25 13:19
 */
public class ReflectionUtil {


    /**
     * 获取所有属性
     *
     * @return 所有的属性【每一个属性添加到StringBuilder中，最后保存到一个List集合中】
     */
    public static List<String> getFields(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> list = new ArrayList<>();
        for (Field field : fields) {
            list.add(field.getName());
        }
        return list;
    }


    /**
     * 修改对象的属性值
     *
     * @param fieldName 属性名
     * @param object    对象
     * @param value     新的属性值
     */
    public static void setFieldValueByName(String fieldName, Object object, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getFieldValueByName(String fieldName, Object object) throws NoSuchFieldException,IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    public static List<Class<?>> getSuperClass(Class<?> clazz) {
        List<Class<?>> clazzs = new ArrayList<>();
        Class<?> suCl = clazz.getSuperclass();
        clazzs.add(clazz.getSuperclass());
        while (suCl != null) {
            clazzs.add(suCl);
            suCl = suCl.getSuperclass();
        }

        return clazzs;
    }

    public static Object getPrivateFieldValueByType(Object o, Class<?> objectClasstype, Class<?> fieldClasstype) {
        return getPrivateFieldValueByType(o, objectClasstype, fieldClasstype, 0);
    }

    public static Object getPrivateFieldValueByType(Object o, Class<?> objectClasstype, Class<?> fieldClasstype,
                                                    int index
    ) {
        Class<?> objectClass;
        if (o != null) {
            objectClass = o.getClass();
        } else {
            objectClass = objectClasstype;
        }

        while (!objectClass.equals(objectClasstype) && objectClass.getSuperclass() != null) {
            objectClass = objectClass.getSuperclass();
        }

        int counter = 0;
        Field[] fields = objectClass.getDeclaredFields();

        for (Field field : fields) {
            if (fieldClasstype.equals(field.getType())) {
                if (counter == index) {
                    try {
                        field.setAccessible(true);
                        return field.get(o);
                    } catch (IllegalAccessException ignored) {
                    }
                }
                ++counter;
            }
        }

        return null;
    }

    public static void setPrivateFieldValueByType(Object o, Class<?> fieldClassType, Object value, int index) {
        int counter = 0;
        Class<?> clazz = o.getClass();

        List<Field> fieldList = new ArrayList<>();
        Class<?> tempClass = clazz;
        while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }

        //Field[] fields = clazz.getDeclaredFields();
        for (Field field : fieldList) {
            if (field.getType().equals(fieldClassType)) {
                if (counter == index) {
                    try {
                        field.setAccessible(true);
                        field.set(o, value);
                    } catch (Exception ignored) {
                    }
                }
                ++counter;
            }
        }

    }

    public static void setPrivateFieldValueByName(Object o, String fieldName, Object value) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                try {
                    field.setAccessible(true);
                    field.set(o, value);
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }

    public static Object getPrivateFieldValueByName(Object o, String fieldName) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                try {
                    field.setAccessible(true);
                    return field.get(o);
                } catch (IllegalAccessException ignored) {
                }
            }
        }
        return null;
    }

    public static Object getFieldValueByName(Object o, String fieldName) {
        Field[] fields = o.getClass().getFields();

        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                try {
                    field.setAccessible(true);
                    return field.get(o);
                } catch (IllegalAccessException ignored) {
                }
            }
        }

        return null;
    }

    public static ArrayList<Field> getFieldsByType(Object o, Class<?> objectClassBaseType, Class<?> fieldClasstype) {
        ArrayList<Field> matches = new ArrayList<>();

        for (Class<?> objectClass = o.getClass(); !objectClass.equals(objectClassBaseType) && objectClass
                .getSuperclass() != null; objectClass = objectClass.getSuperclass()) {
            Field[] fields = objectClass.getDeclaredFields();

            for (Field field : fields) {
                if (fieldClasstype.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    matches.add(field);
                }
            }
        }

        return matches;
    }

    public static Field getFieldByType(Object o, Class<?> objectClasstype, Class<?> fieldClasstype) {
        return getFieldByType(o, objectClasstype, fieldClasstype, 0);
    }

    public static Field getFieldByType(Object o, Class<?> objectClasstype, Class<?> fieldClasstype, int index) {
        Class<?> objectClass;
        for (objectClass = o.getClass(); !objectClass.equals(objectClasstype) && objectClass
                .getSuperclass() != null; objectClass = objectClass.getSuperclass()) {
        }

        int counter = 0;
        Field[] fields = objectClass.getDeclaredFields();

        for (Field field : fields) {
            if (fieldClasstype.equals(field.getType())) {
                if (counter == index) {
                    field.setAccessible(true);
                    return field;
                }

                ++counter;
            }
        }

        return null;
    }

    public static Method getMethodByType(Class<?> objectType, Class<?> returnType, Class<?>... parameterTypes) {
        return getMethodByType(0, objectType, returnType, parameterTypes);
    }

    public static Method getMethodByType(int index, Class<?> objectType, Class<?> returnType,
                                         Class<?>... parameterTypes
    ) {
        Method[] methods = objectType.getDeclaredMethods();
        int counter = 0;

        for (Method method : methods) {
            if (returnType.equals(method.getReturnType())) {
                Class<?>[] methodParameterTypes = method.getParameterTypes();
                if (parameterTypes.length == methodParameterTypes.length) {
                    boolean match = true;

                    for (int t = 0; t < parameterTypes.length; ++t) {
                        if (parameterTypes[t] != methodParameterTypes[t]) {
                            match = false;
                            break;
                        }
                    }

                    if (counter == index && match) {
                        method.setAccessible(true);
                        return method;
                    }
                }

                ++counter;
            }
        }

        return null;
    }

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException var2) {
            return false;
        }
    }
}
