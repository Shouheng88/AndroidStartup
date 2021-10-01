package me.shouheng.startup.compiler.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import me.shouheng.startup.annotation.StartupJob;
import me.shouheng.startup.compiler.entity.AnnotatedClass;
import me.shouheng.startup.compiler.utils.TypeUtils;

import static me.shouheng.startup.compiler.utils.Consts.GENERATED_FILES_PACKAGE;
import static me.shouheng.startup.compiler.utils.TypeUtils.CLASS_NAME_ARRAY_LIST;
import static me.shouheng.startup.compiler.utils.TypeUtils.CLASS_NAME_I_SCHEDULER_JOB;
import static me.shouheng.startup.compiler.utils.TypeUtils.SEPARATOR;
import static me.shouheng.startup.compiler.utils.TypeUtils.TYPE_NAME_LIST_OF_SCHEDULER_JOB;

/** Annotation processor for startup Job annotation. */
@AutoService(Processor.class)
public class StartupProcessor extends BaseProcessor {

    private TypeMirror tmJobHunter;
    private TypeMirror tmISchedulerJob;
    private Map<String, AnnotatedClass> map = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        tmJobHunter = elements.getTypeElement(TypeUtils.JOB_HUNTER_FULL_NAME).asType();
        tmISchedulerJob = elements.getTypeElement(TypeUtils.I_SCHEDULER_JOB_FULL_NAME).asType();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {{
            add(StartupJob.class.getCanonicalName());
        }};
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        map.clear();

        try {
            processJobAnnotation(roundEnvironment, tmJobHunter);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (!map.isEmpty()) {
                generateFinder().writeTo(filer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /** Process for job annotation. */
    private void processJobAnnotation(RoundEnvironment roundEnvironment, TypeMirror jobHunter) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(StartupJob.class)) {
            TypeMirror tm = element.asType();
            // Check if the element is sub type of ISchedulerJob.
            if (types.isSubtype(tm, tmISchedulerJob)) {
                AnnotatedClass annotatedClass = map.get(element.toString());
                if (annotatedClass == null) {
                    annotatedClass = new AnnotatedClass(element);
                    map.put(element.toString(), annotatedClass);
                }
            } else {
                logger.error(String.format("Illegal @StartupJob annotation for element %s", element.toString()));
            }
        }
    }

    /** Generate finder. */
    private JavaFile generateFinder() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("hunt")
                .addModifiers(Modifier.PUBLIC)
                .returns(TYPE_NAME_LIST_OF_SCHEDULER_JOB)
                .addException(NoSuchMethodException.class)
                .addException(IllegalAccessException.class)
                .addException(InvocationTargetException.class)
                .addException(InstantiationException.class)
                .addAnnotation(Override.class);
        builder.addStatement("List<$T> jobs = new $T<>()", CLASS_NAME_I_SCHEDULER_JOB, CLASS_NAME_ARRAY_LIST);
        for (AnnotatedClass annotatedClass : map.values()) {
            builder.addStatement("jobs.add($T.class.getConstructor().newInstance())", annotatedClass.getClassName());
        }
        builder.addStatement("return jobs");
        TypeSpec hunterImplClass = TypeSpec.classBuilder("JobHunter" + SEPARATOR + moduleName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(TypeUtils.CLASS_NAME_JOB_HUNTER)
                .addMethod(builder.build())
                .build();
        return JavaFile.builder(GENERATED_FILES_PACKAGE, hunterImplClass).build();
    }
}
