package me.shouheng.startup.compiler.processor;

import static me.shouheng.startup.compiler.utils.Consts.KEY_MODULE_NAME;
import static me.shouheng.startup.compiler.utils.Consts.NO_MODULE_NAME_TIPS;

import java.util.Map;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import me.shouheng.startup.compiler.utils.ILogger;
import me.shouheng.startup.compiler.utils.MessagerLogger;

/** Base processor. */
public abstract class BaseProcessor extends AbstractProcessor {
    protected Filer filer;
    protected ILogger logger;
    protected Types types;
    protected Elements elements;
    String moduleName = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
        elements = processingEnv.getElementUtils();
        logger = new MessagerLogger(processingEnv.getMessager());

        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (!options.isEmpty()) {
            moduleName = options.get(KEY_MODULE_NAME);
        }

        if (moduleName != null && moduleName.length() > 0) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
            logger.info("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            logger.error(NO_MODULE_NAME_TIPS);
            throw new RuntimeException("AndroidStartup::Compiler >>> No module name, for more information, look at gradle log.");
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}