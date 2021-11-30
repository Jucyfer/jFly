package cc.ejyf.jfly.dynamic.core;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Base64;

public class CompiledHolder extends SimpleJavaFileObject {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private ByteArrayOutputStream out;
    private String classFullName, simpleClassName;
    private byte[] bytecode;

    public CompiledHolder(String classFullName) {
        super(URI.create("bytes:///" + classFullName.replace('.', '/') + ".class"), Kind.CLASS);
        this.classFullName = classFullName;
        this.simpleClassName = classFullName.substring(classFullName.lastIndexOf(".") + 1);
    }

    public CompiledHolder(String classFullName, String base64) {
        this(classFullName);
        this.bytecode = decoder.decode(base64);
    }

    public String getClassFullName() {
        return classFullName;
    }

    public String getSimpleClassName() {
        return simpleClassName;
    }

    public byte[] getCode() {
        return bytecode == null ? (bytecode = out.toByteArray()) : bytecode;
    }

    public String getCodeBase64() {
        return encoder.encodeToString(getCode());
    }

    @Override
    public ByteArrayOutputStream openOutputStream() {
        out = new ByteArrayOutputStream();
        return out;
    }

}
