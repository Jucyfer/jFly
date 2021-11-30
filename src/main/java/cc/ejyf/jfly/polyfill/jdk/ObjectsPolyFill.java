package cc.ejyf.jfly.polyfill.jdk;

import java.util.Arrays;
import java.util.Objects;
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
    public static <T> T requireNonNullElse(T... objects) {
        return Arrays.stream(objects).filter(Objects::nonNull).findFirst().orElse(null);
    }

    /**
     * {@link ObjectsPolyFill#requireNonNullElse(Object[]) 本PolyFill的requireNonNullElse}的懒加载版本
     *
     * @param suppliers
     * @param <T>
     * @return
     */
    public static <T> T requireNonNullElse(Supplier<T>... suppliers) {
        return Arrays.stream(suppliers).map(Supplier::get).filter(Objects::nonNull).findFirst().orElse(null);
    }
}
