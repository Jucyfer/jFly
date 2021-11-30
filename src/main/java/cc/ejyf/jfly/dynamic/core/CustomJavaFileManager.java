package cc.ejyf.jfly.dynamic.core;


import javax.tools.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

public class CustomJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private DiagnosticCollector<JavaFileObject> diagnostics;
    private ArrayList<CompiledHolder> outFiles;


    /**
     * Creates a new instance of ForwardingJavaFileManager.
     *
     * @param fileManager delegate to this file manager
     */
    protected CustomJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    public CustomJavaFileManager() {
        this(new DiagnosticCollector<>(), new ArrayList<>());
    }

    public CustomJavaFileManager(ArrayList<CompiledHolder> outFiles) {
        this(new DiagnosticCollector<>(), outFiles);
    }

    public CustomJavaFileManager(DiagnosticCollector<JavaFileObject> diagnostics) {
        this(diagnostics, new ArrayList<>());
    }

    public CustomJavaFileManager(DiagnosticCollector<JavaFileObject> diagnostics, ArrayList<CompiledHolder> outFiles) {
        this(ToolProvider.getSystemJavaCompiler().getStandardFileManager(diagnostics, Locale.US, StandardCharsets.UTF_8));
        this.diagnostics = diagnostics;
        this.outFiles = outFiles;
    }

    public DiagnosticCollector<JavaFileObject> getDiagnostics() {
        return diagnostics;
    }

    public ArrayList<CompiledHolder> getOutFiles() {
        return outFiles;
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS) {
            CompiledHolder compiledHolder = new CompiledHolder(className);
            outFiles.add(compiledHolder);
            return compiledHolder;
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }
}
