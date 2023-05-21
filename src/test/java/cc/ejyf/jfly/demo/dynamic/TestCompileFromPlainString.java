package cc.ejyf.jfly.demo.dynamic;

import cc.ejyf.jfly.dynamic.core.AbstractCustomClassLoader;
import cc.ejyf.jfly.dynamic.core.DynamicCompiler;
import cc.ejyf.jfly.dynamic.core.SourceCodeHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestCompileFromPlainString {
    private static String sourceCode = "package cc.ejyf.jfly.demo.dynamic;\n" +
            "\n" +
            "public class HelloWorld {\n" +
            "    public void hello(){\n" +
            "        System.out.println(\"hello world!\");\n" +
            "    }\n" +
            "}\n";

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ArrayList<SourceCodeHolder> sourceCodes = new ArrayList<>();
        sourceCodes.add(new SourceCodeHolder("cc.ejyf.jfly.demo.dynamic.HelloWorld", sourceCode));
        List<Class<?>> classes = DynamicCompiler.compileAndReturnClass(sourceCodes, AbstractCustomClassLoader.DEFAULT_NOT_SUPPORTED);
        Class clazz = classes.get(0);
        Constructor constructor = clazz.getConstructor();
        Object obj = constructor.newInstance();
        Method method = clazz.getMethod("hello");
        method.invoke(obj);
    }
}
