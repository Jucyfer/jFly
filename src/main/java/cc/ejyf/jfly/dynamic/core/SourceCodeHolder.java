package cc.ejyf.jfly.dynamic.core;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

public class SourceCodeHolder extends SimpleJavaFileObject {
    private String code;

    public SourceCodeHolder(String javaFullClassName, String code) {
        super(URI.create("string:///" + javaFullClassName.replace('.', '/') + ".java"), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return code;
    }
}