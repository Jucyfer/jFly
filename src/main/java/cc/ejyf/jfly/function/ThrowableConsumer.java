package cc.ejyf.jfly.function;

import java.util.Objects;

@FunctionalInterface
public interface ThrowableConsumer<T> {
    void accept(T t) throws Exception;

    default ThrowableConsumer<T> andThen(ThrowableConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
