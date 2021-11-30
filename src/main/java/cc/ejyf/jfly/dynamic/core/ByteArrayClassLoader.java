package cc.ejyf.jfly.dynamic.core;

import java.util.ArrayList;
import java.util.Collection;

public class ByteArrayClassLoader extends AbstractCustomClassLoader {
    private Collection<CompiledHolder> classes;

    public ByteArrayClassLoader() {
        this(new ArrayList<>());
    }

    public ByteArrayClassLoader(Collection<CompiledHolder> classes) {
        super();
        this.classes = classes;
    }

    public ByteArrayClassLoader(ClassLoader parent) {
        this(parent, new ArrayList<>());
    }

    public ByteArrayClassLoader(ClassLoader parent, Collection<CompiledHolder> classes) {
        super(parent);
        this.classes = classes;
    }


    public void setClasses(Collection<CompiledHolder> classes) {
        this.classes = classes;
    }


    @Override
    protected byte[] findClassBytes(String classFullName) {
        return classes.parallelStream().filter(holder -> holder.getClassFullName().equals(classFullName)).findFirst().map(CompiledHolder::getCode).orElse(null);
    }

}
