package cc.ejyf.jfly;


import cc.ejyf.jfly.function.Functions;
import cc.ejyf.jfly.tuple.Tuple2;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Reflector {

    /**
     * 模仿js的assign，将map里的键值对，混入给定的object中。
     * <br/>
     * <b>注意，此方法对于map中的值类型执行isAssignableFrom类型检查。</b>
     *
     * @param object
     * @param properties
     * @param remove
     * @throws IllegalAccessException
     */
    public static void assign(Object object, Map<String, Object> properties, boolean remove) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        HashMap<String, Field> fields = Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toMap(
                Field::getName,
                field -> field,
                (field1, field2) -> field2,
                HashMap::new
        ));
        List<String> proceededProps = properties.keySet().parallelStream()
                .filter(properties::containsKey).filter(fields::containsKey)
                .filter(property -> fields.get(property).getType().isAssignableFrom(properties.get(property).getClass()))
                .map(fields::get)
                .filter(AccessibleObject::trySetAccessible)
                .map(field -> Functions.tryOrElse(() -> {
                    field.set(object, properties.get(field.getName()));
                    return field.getName();
                }, null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (remove) {
            proceededProps.forEach(properties::remove);
        }
    }

    public static Map<String, Object> toMap(Object object, String... specificFields) {
        Class<?> clz = object.getClass();
        //无视是否为私有变量，根据过滤条件全部转换
        Set<String> filter = Arrays.stream(specificFields).collect(Collectors.toSet());

        return Arrays.stream(clz.getDeclaredFields()).parallel().filter(field -> filter.contains(field.getName()))
                .peek(Field::trySetAccessible)
                .map(field -> new Tuple2<>(field.getName(), Functions.tryOrElse(() -> field.get(object), null)))
                .filter(t -> t.e2 != null)
                .collect(Collectors.toMap(
                        t -> t.e1,
                        t -> t.e2,
                        (v1, v2) -> v1
                ))
                ;
    }

    public static Map<String, Object> toMap(Object object) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .parallel()
                .filter(AccessibleObject::trySetAccessible)
                .map(field -> Functions.tryOrElse(() -> new AbstractMap.SimpleEntry<>(field.getName(), field.get(object)), null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        HashMap<String, Object> map = new HashMap<>();
//        Class<?> clazz = object.getClass();
//        for (Field field : clazz.getDeclaredFields()) {
//            if (field.trySetAccessible()) {
//                map.put(field.getName(), field.get(object));
//            }
//        }
//        return map;
    }

    public static Map<String, Object> toMap(Object object, Map<String, String> keyMapping) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .parallel()
                .filter(AccessibleObject::trySetAccessible)
                .map(field -> Functions.tryOrElse(() -> new AbstractMap.SimpleEntry<>(keyMapping.getOrDefault(field.getName(), field.getName()), Objects.requireNonNullElse(field.get(object), "")), null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Object> toMap(Object object, Map<String, String> keyMapping, java.util.function.Predicate<String> predicate) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .parallel()
                .filter(AccessibleObject::trySetAccessible)
                .map(field -> Functions.tryOrElse(() -> new AbstractMap.SimpleEntry<>(keyMapping.getOrDefault(field.getName(), field.getName()), Objects.requireNonNullElse(field.get(object), "")), null))
                .filter(Objects::nonNull)
                .filter(entry -> predicate.test(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
//    public static <T> T getMethodArgByName(JoinPoint joinPoint, Class<T> clz, String paramName) {
//        Object[] args = joinPoint.getArgs();
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String[] names = signature.getParameterNames();
//        for (int i = 0; i < args.length; i++) {
//            if (names[i].equals(paramName) && names[i] != null) {
//                if (clz.isInstance(args[i])) {
//                    return clz.cast(args[i]);
//                } else {
//                    logger.error("捕获参数中的名称，但是类型不匹配或者为null");
//                    return null;
//                }
//            }
//        }
//        return null;
//    }
//
//    public static Object getMethodArgByName(JoinPoint joinPoint, String paramName) {
//        Object[] args = joinPoint.getArgs();
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String[] names = signature.getParameterNames();
//        for (int i = 0; i < args.length; i++) {
//            if (names[i].equals(paramName)) {
//                return args[i];
//            }
//        }
//        return null;
//    }
}
