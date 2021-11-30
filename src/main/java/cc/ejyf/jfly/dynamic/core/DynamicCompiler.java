package cc.ejyf.jfly.dynamic.core;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DynamicCompiler {
    private ArrayList<SourceCodeHolder> sourceCodes;
    private ArrayList<CompiledHolder> compiledClass;
    private String errorMsg = "";

    public DynamicCompiler(ArrayList<SourceCodeHolder> sourceCodes) {
        this.sourceCodes = sourceCodes;
    }

    public static ArrayList<CompiledHolder> compileAndReturn(ArrayList<SourceCodeHolder> sourceCodes) throws ClassFormatError {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        CustomJavaFileManager customJavaFileManager = new CustomJavaFileManager();
        Writer writer = new OutputStreamWriter(new ByteArrayOutputStream());
        if (
                compiler.getTask(writer, customJavaFileManager, customJavaFileManager.getDiagnostics(), null, null, sourceCodes).call()
        ) {
            return customJavaFileManager.getOutFiles();
        }
        throw new ClassFormatError(customJavaFileManager.getDiagnostics().getDiagnostics().stream()
                .map(diagnostic ->
                        diagnostic.getMessage(Locale.US)
                ).collect(Collectors.joining("\n")));
    }

    public static List<Class<?>> compileAndReturnClass(ArrayList<SourceCodeHolder> sourceCodes, AbstractCustomClassLoader classLoader) {
        return classLoader.loadClassFromSpecificBytes(compileAndReturn(sourceCodes));
    }

    public ArrayList<SourceCodeHolder> getSourceCodes() {
        return sourceCodes;
    }

    public void setSourceCodes(ArrayList<SourceCodeHolder> sourceCodes) {
        this.sourceCodes = sourceCodes;
    }

    public ArrayList<CompiledHolder> getCompiledClass() {
        return compiledClass;
    }

    public void setCompiledClass(ArrayList<CompiledHolder> compiledClass) {
        this.compiledClass = compiledClass;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    private boolean compile() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        CustomJavaFileManager customJavaFileManager = new CustomJavaFileManager();
        Writer writer = new OutputStreamWriter(new ByteArrayOutputStream());
        if (
                compiler.getTask(writer, customJavaFileManager, customJavaFileManager.getDiagnostics(), null, null, sourceCodes).call()
        ) {
            this.compiledClass = customJavaFileManager.getOutFiles();
            return true;
        }
        this.errorMsg = customJavaFileManager.getDiagnostics().getDiagnostics().stream()
                .map(diagnostic ->
                        diagnostic.getMessage(Locale.US)
                ).collect(Collectors.joining("\n"));
        return false;
    }
}
