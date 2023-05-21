package cc.ejyf.jfly.dynamic.core;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class AbstractCustomClassLoader extends ClassLoader {
    public static final AbstractCustomClassLoader DEFAULT_NOT_SUPPORTED = new AbstractCustomClassLoader() {
        @Override
        protected byte[] findClassBytes(String classFullName) {
            throw new UnsupportedOperationException("不支持的寻类方法");
        }
    };

    public AbstractCustomClassLoader(ClassLoader parent) {
        super(parent);
    }

    public AbstractCustomClassLoader() {
    }

    public AbstractCustomClassLoader(String name, ClassLoader parent) {
        super(name, parent);
    }

    /**
     * 根据传入的classFullName属性，去某处获取字节数组，并作为这个类的定义。<br/>
     * 注：这个方法被设定为public是基于某些扩展能力考虑之后的有意为之。
     *
     * @param name
     * @return
     * @throws ClassNotFoundException
     */
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = findClassBytes(name);
        if (bytes != null) {
            return defineClass(name, bytes, 0, bytes.length);
        }
        throw new ClassNotFoundException(name);
    }

    /**
     * 查找字节码方法。<br/>
     * <hr/>
     * 查找范围不限。查找方式不限。<br/>
     * http、jdbc、file、socket等等都可以。<br/>
     * 具体方式由子类实现。
     *
     * @param classFullName 完整类名。见：{@link Class#forName(String)}
     * @return
     */
    protected abstract byte[] findClassBytes(String classFullName);

    protected String convertClass2Path(String name) {
        return "/" + name.replace('.', '/') + ".class";
    }

    public Class<?> loadClassFromSpecificBytes(CompiledHolder compiledHolder) {
        byte[] bytes = compiledHolder.getCode();
        return defineClass(compiledHolder.getClassFullName(), bytes, 0, bytes.length);
    }

    public ArrayList<Class<?>> loadClassFromSpecificBytes(ArrayList<CompiledHolder> compiledHolders) {
        return compiledHolders.stream().map(this::loadClassFromSpecificBytes).collect(Collectors.toCollection(ArrayList::new));
    }

    public Class<?> defineClassForwarding(String name, byte[] b, int off, int len) {
        return defineClass(name, b, off, len);
    }

}
