package cc.ejyf.jfly.function;

import java.util.function.Consumer;

public abstract class Functions {
    public static <T> Consumer<T> noThrowConsumer(ThrowableConsumer<? super T> consumer) {
        return e -> {
            try {
                consumer.accept(e);
            } catch (Exception ignored) {
            }
        };
    }

    public static <T> T tryOrElse(ThrowableSupplier<? extends T> supplier, T obj) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return obj;
        }
    }

    public static <T> T tryOrElse(ThrowableSupplier<? extends T> supplier, T obj, org.apache.logging.log4j.Logger logger) {
        try {
            return supplier.get();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return obj;
        }
    }

    public static <T> T tryOrElse(ThrowableSupplier<? extends T> supplier, T obj, org.slf4j.Logger logger) {
        try {
            return supplier.get();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return obj;
        }
    }
}
