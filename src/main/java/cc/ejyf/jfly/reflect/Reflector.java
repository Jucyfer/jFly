package cc.ejyf.jfly.reflect;


import cc.ejyf.jfly.function.Functions;

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
     * @param object     目标对象
     * @param properties 需要混入的 {属性名:属性值..}
     * @param remove     是否从properties中移除已混入的键值对（针对某些特殊原因设计）
     * @return 当remove被设定为false时，返回properties参数。当remove被设定为true时，返回的是移除了成功混入的键值对之后的map（此时与properties无关）
     * @throws IllegalAccessException
     */
    public static Map<String, Object> assign(Object object, Map<String, Object> properties, boolean remove) throws IllegalAccessException {
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
            return properties.entrySet().parallelStream().filter(e -> !proceededProps.contains(e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
        return properties;
    }

    /**
     * 将对象根据限定字段转换成Map
     *
     * @param object         对象
     * @param specificFields 限定字段
     * @return Map
     */
    public static Map<String, Object> toMap(Object object, String... specificFields) {
        Set<String> limited = Set.of(specificFields);
        return toMap(object, Map.of(), limited::contains, false);
    }

    /**
     * 将对象根据限定字段转换成Map
     *
     * @param object         对象
     * @param reverse        是否反转过滤
     * @param specificFields 限定字段
     * @return Map
     */
    public static Map<String, Object> toMap(Object object, boolean reverse, String... specificFields) {
        Set<String> limited = Set.of(specificFields);
        return toMap(object, Map.of(), limited::contains, reverse);
    }

    /**
     * 将对象转换成Map
     *
     * @param object 对象
     * @return map
     */
    public static Map<String, Object> toMap(Object object) {
        return toMap(object, Map.of(), s -> true, false);
    }

    /**
     * 将对象转换成Map
     *
     * @param object     对象
     * @param keyMapping 键映射
     * @return map
     */
    public static Map<String, Object> toMap(Object object, Map<String, String> keyMapping) {
        return toMap(object, keyMapping, s -> true, false);
    }

    /**
     * 将对象转换成Map
     *
     * @param object     对象
     * @param keyMapping 键映射
     * @param predicate  过滤器
     * @return map
     */
    public static Map<String, Object> toMap(Object object, Map<String, String> keyMapping, java.util.function.Predicate<String> predicate) {
        return toMap(object, keyMapping, predicate, false);
    }

    /**
     * 将对象通过一系列复杂操作转换成Map
     *
     * @param object       对象
     * @param keyMapping   键映射
     * @param keyPredicate 键过滤器
     * @param reverse      过滤器反转开关
     * @return
     */
    public static Map<String, Object> toMap(Object object, Map<String, String> keyMapping, java.util.function.Predicate<String> keyPredicate, boolean reverse) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .parallel()
                .filter(AccessibleObject::trySetAccessible)
                .map(field -> Functions.tryOrElse(() -> new AbstractMap.SimpleEntry<>(keyMapping.getOrDefault(field.getName(), field.getName()), Objects.requireNonNullElse(field.get(object), "")), null))
                .filter(Objects::nonNull)
                .filter(entry -> keyPredicate.test(entry.getKey()) != reverse)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
