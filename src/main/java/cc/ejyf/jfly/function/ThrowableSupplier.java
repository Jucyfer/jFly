package cc.ejyf.jfly.function;

@FunctionalInterface
public interface ThrowableSupplier<T> {
    T get() throws Exception;
}
