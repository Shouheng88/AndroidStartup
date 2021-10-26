package me.shouheng.startup.compiler.utils;

/** Constants for project. */
public class Consts {

    public static final String PROJECT = "Startup";
    public static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler ";

    public static final String GENERATED_FILES_PACKAGE = "me.shouheng.startup.hunter";

    public static final String KEY_MODULE_NAME = "STARTUP_MODULE_NAME";
    public static final String NO_MODULE_NAME_TIPS = "These no module name, at 'build.gradle', like :\n" +
            "android {\n" +
            "    defaultConfig {\n" +
            "        ...\n" +
            "        javaCompileOptions {\n" +
            "            annotationProcessorOptions {\n" +
            "                arguments = [STARTUP_MODULE_NAME: project.getName()]\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
}
