package cc.ejyf.jfly.polyfill.jdk;

import cc.ejyf.jfly.tuple.Tuple3;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MapPolyFill {
    /**
     * 集合拉链：把传入的键值混装序列，按传入顺序成对缝合
     *
     * @param kClass        键类
     * @param vClass        值类
     * @param mergeFunction 冲突策略
     * @param kvArgs        [键,值,..]数组
     * @param <K>           键类型
     * @param <V>           值类型
     * @return 缝合后的映射表
     * @throws ClassCastException
     */
    public static <K, V> HashMap<K, V> ofMap(Class<K> kClass, Class<V> vClass, BinaryOperator<V> mergeFunction, Object... kvArgs) throws IllegalArgumentException, ClassCastException {
        if (kvArgs.length % 2 != 0) throw new IllegalArgumentException("Odd elements.");
        return IntStream.range(0, kvArgs.length / 2).parallel()
                .map(i -> i * 2)
                .boxed()
                .collect(Collectors.toMap(
                        i -> kClass.cast(kvArgs[i]),
                        i -> vClass.cast(kvArgs[i + 1]),
                        mergeFunction,
                        LinkedHashMap::new
                ));
    }


    /**
     * 集合拉链：把传入的键值混装序列，按传入顺序成对缝合
     *
     * @param kClass 键类
     * @param vClass 值类
     * @param kvArgs [键,值,..]数组
     * @param <K>    键类型
     * @param <V>    值类型
     * @return 缝合后的映射表
     * @throws ClassCastException
     */
    public static <K, V> Map<K, V> ofMap(Class<K> kClass, Class<V> vClass, Object... kvArgs) {
        return ofMap(kClass, vClass, (v1, v2) -> {
            throw new IllegalArgumentException("dulplicate key.");
        }, kvArgs);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> ofMap(K k1, V v1, Object... remainingKV) {
        return ofMap((Class<K>) k1.getClass(), (Class<V>) v1.getClass(), Stream.concat(Stream.of(k1, v1), Arrays.stream(remainingKV)).toArray());
    }

    /**
     * 这个方法重载没有泛型安全检查，使用时请注意安全。
     *
     * @param kvArgs [键,值,..]数组
     * @return 缝合后的映射表
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> ofMap0(Object... kvArgs) {
        return (Map<K, V>) ofMap(Object.class, Object.class, kvArgs);
    }

    /**
     * 映射表改键函数。该函数返回一个新的映射，不会修改原来的映射表。
     *
     * @param source               原映射
     * @param keyMapping           键映射表
     * @param ignoreConflictingKey 是否忽略键冲突
     * @param <K>                  键类型
     * @param <V>                  值类型
     * @return
     */
    public static <K, V> Map<K, V> renameKey(Map<K, V> source, Map<K, K> keyMapping, boolean ignoreConflictingKey) {
        return source.entrySet().parallelStream().map(e -> new AbstractMap.SimpleEntry<>(keyMapping.getOrDefault(e.getKey(), e.getKey()), e.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> {
                    if (ignoreConflictingKey) return v2;
                    throw new IllegalStateException("Duplicate key detected. Please re-check keyMapping.");
                }));
    }

    /**
     * 用来过滤映射表。该方法会按照匹配条件对原映射进行过滤，然后返回一个新的映射，不会修改原来的映射表。
     *
     * @param source    原映射
     * @param predicate 匹配条件
     * @param reverse   是否反转过滤
     * @param <K>       键类型
     * @param <V>       值类型
     * @return 新映射
     */
    public static <K, V> Map<K, V> filterKV(Map<K, V> source, Predicate<Map.Entry<K, V>> predicate, boolean reverse) {
        return source.entrySet().parallelStream().filter(entry -> reverse != predicate.test(entry)).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        ));
    }

    /**
     * 映射合并。与{@link Map#merge(Object, Object, BiFunction) Map::merge}的效果不同的是，该方法不会修改源映射与目标映射，
     * 从而不受不可变集合的限制。
     *
     * @param from         源映射
     * @param to           目标映射
     * @param specificKeys 过滤键
     * @param <K>          键类型
     * @param <V>          值类型
     * @return 合并后的映射
     */
    @SafeVarargs
    public static <K, V> Map<K, V> mergeMap(Map<K, V> from, Map<K, V> to, K... specificKeys) {
        Stream<Tuple3<K, V, Integer>> stream = Stream.concat(from.entrySet().parallelStream().map(e -> new Tuple3<>(e.getKey(), e.getValue(), 0)), to.entrySet().parallelStream().map(e -> new Tuple3<>(e.getKey(), e.getValue(), 1)));
        Stream<Tuple3<K, V, Integer>> middleStream;
        if (specificKeys.length == 0) {
            middleStream = stream;
        } else {
            Set<K> keySet = Arrays.stream(specificKeys).parallel().collect(Collectors.toSet());
            middleStream = stream.filter(t -> keySet.contains(t.e1));
        }
        return middleStream.collect(Collectors.groupingBy(t -> t.e1, Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(t -> t.e3)), o -> o.map(t -> t.e2).get())));
    }

    /**
     * 集合拉链：把传入的键值序列，按各自的排列顺序成对缝合
     *
     * @param keys                 键列表
     * @param values               值列表
     * @param ignoreConflictingKey 是否忽略键冲突。如果为true，则键冲突的取值结果是无法保证的。
     * @param <K>                  键类型
     * @param <V>                  值类型
     * @return 缝合后的映射表
     */
    public static <K, V> Map<K, V> zipUp(List<K> keys, List<V> values, boolean ignoreConflictingKey) {
        if (keys.size() != values.size()) throw new IllegalArgumentException("Key/Value length not match.");
        return IntStream.range(0, keys.size())
                .parallel().unordered()
                .boxed()
                .collect(Collectors.toMap(
                        keys::get,
                        values::get,
                        (v1, v2) -> {
                            if (ignoreConflictingKey) return v2;
                            throw new IllegalStateException("Duplicate key detected. Please re-check keyMapping.");
                        }
                ));
    }

    /**
     * 集合拉链：把传入的键值序列，按传入顺序成对缝合
     *
     * @param keys                 键数组
     * @param values               值数组
     * @param ignoreConflictingKey 是否忽略键冲突。如果为true，则键冲突的取值结果是无法保证的。
     * @param <K>                  键类型
     * @param <V>                  值类型
     * @return 缝合后的映射表
     */
    public static <K, V> Map<K, V> zipUp(K[] keys, V[] values, boolean ignoreConflictingKey) {
        if (keys.length != values.length) throw new IllegalArgumentException("Key/Value length not match.");
        return IntStream.range(0, keys.length)
                .parallel().unordered()
                .boxed()
                .collect(Collectors.toMap(
                        i -> keys[i],
                        i -> values[i],
                        (v1, v2) -> {
                            if (ignoreConflictingKey) return v2;
                            throw new IllegalStateException("Duplicate key detected. Please re-check keyMapping.");
                        }
                ));
    }

    /**
     * 集合拉链：把传入的键值序列，按传入顺序成对缝合
     *
     * @param keys                 键列表
     * @param values               值数组
     * @param ignoreConflictingKey 是否忽略键冲突。如果为true，则键冲突的取值结果是无法保证的。
     * @param <K>                  键类型
     * @param <V>                  值类型
     * @return 缝合后的映射表
     */
    public static <K, V> Map<K, V> zipUp(List<K> keys, V[] values, boolean ignoreConflictingKey) {
        if (keys.size() != values.length) throw new IllegalArgumentException("Key/Value length not match.");
        return IntStream.range(0, keys.size())
                .parallel().unordered()
                .boxed()
                .collect(Collectors.toMap(
                        keys::get,
                        i -> values[i],
                        (v1, v2) -> {
                            if (ignoreConflictingKey) return v2;
                            throw new IllegalStateException("Duplicate key detected. Please re-check keyMapping.");
                        }
                ));
    }

    /**
     * 集合拉链：把传入的键值序列，按传入顺序成对缝合
     *
     * @param keys                 键数组
     * @param values               值列表
     * @param ignoreConflictingKey 是否忽略键冲突。如果为true，则键冲突的取值结果是无法保证的。
     * @param <K>                  键类型
     * @param <V>                  值类型
     * @return 缝合后的映射表
     */
    public static <K, V> Map<K, V> zipUp(K[] keys, List<V> values, boolean ignoreConflictingKey) {
        if (keys.length != values.size()) throw new IllegalArgumentException("Key/Value length not match.");
        return IntStream.range(0, keys.length)
                .parallel().unordered()
                .boxed()
                .collect(Collectors.toMap(
                        i -> keys[i],
                        values::get,
                        (v1, v2) -> {
                            if (ignoreConflictingKey) return v2;
                            throw new IllegalStateException("Duplicate key detected. Please re-check keyMapping.");
                        }
                ));
    }

}
