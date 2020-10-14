package com.lmjssjj.annotation_compiler;

import com.google.auto.service.AutoService;
import com.lmjssjj.annotation.BindPath;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

//@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
//@SupportedAnnotationTypes("com.lmjssjj.annotation.BindPath")
public class AnnotationCompiler extends AbstractProcessor {

    private Filer mFiler;
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindPath.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        mMessager.printMessage(Diagnostic.Kind.NOTE, "process");

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindPath.class);
        Map<String, String> routerMaps = new HashMap<>();
        for (Element element : elements) {
            TypeElement e = (TypeElement) element;
            String value = e.getQualifiedName().toString();
            String key = e.getAnnotation(BindPath.class).value();
            routerMaps.put(key, value + ".class");
        }

        if (routerMaps.size() > 0) {
            Writer writer = null;
            String className = "ActivityUtil" + System.currentTimeMillis();
            try {
                JavaFileObject sourcefile = mFiler.createSourceFile("com.lmjssjj.utils." + className);
                writer = sourcefile.openWriter();
                StringBuilder sb = new StringBuilder();

                sb.append("package com.lmjssjj.utils;\n");
                sb.append("import android.app.Activity;\n");
                sb.append("import com.lmjssjj.arouter.ARouter;\n");
                sb.append("import com.lmjssjj.arouter.IARouter;\n");
                sb.append("public class "+className+" implements IARouter {\n");
                sb.append("@Override\n");
                sb.append("public void putActivity() {\n");
                Iterator<String> iterator = routerMaps.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String value = routerMaps.get(key);
                    sb.append("ARouter.getInstance().putActivity(\"" + key + "\"," + value + ");\n");
                }
                sb.append("}\n");
                sb.append("}\n");


                writer.write(sb.toString());
                writer.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return true;
    }
}
