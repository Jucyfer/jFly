package cc.ejyf.jfly.polyfill.jdk;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ObjectsPolyFill {
    /**
     * {@link java.util.Objects#requireNonNullElse(Object, Object) Objects.requireNonNullElse}
     * 的变长参数版本，可以提供多个备选。
     *
     * @param objects
     * @param <T>
     * @return
     */
    @SafeVarargs
    public static <T> T requireNonNullElse(T defaultValue, T... objects) {
        return Arrays.stream(objects).filter(Objects::nonNull).findFirst().orElse(defaultValue);
    }

    @SafeVarargs
    public static <T> T requireNonElse(T defaultValue, Predicate<T> predicate, T... objects) {
        return Arrays.stream(objects).filter(Objects::nonNull).findFirst().orElse(defaultValue);
    }

    /**
     * {@link ObjectsPolyFill#requireNonNullElse(Object, Object[]) 本PolyFill的requireNonNullElse}的懒加载版本
     *
     * @param suppliers
     * @param <T>
     * @return
     */
    public static <T> T requireNonNullElse(T defaultValue, Supplier<T>... suppliers) {
        return Arrays.stream(suppliers).filter(Objects::nonNull).map(Supplier::get).filter(Objects::nonNull).findFirst().orElse(defaultValue);
    }
}
