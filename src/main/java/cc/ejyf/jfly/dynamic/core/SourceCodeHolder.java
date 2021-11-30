package cc.ejyf.jfly.dynamic.core;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

public class SourceCodeHolder extends SimpleJavaFileObject {
    private String code;

    protected SourceCodeHolder(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + ".java"), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return code;
    }
}