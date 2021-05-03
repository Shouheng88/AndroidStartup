package me.shouheng.startup.compiler.utils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

/** Type definitions. */
public class TypeUtils {

    public static final ClassName CLASS_NAME_I_SCHEDULER_JOB =
            ClassName.get("me.shouheng.scheduler", "ISchedulerJob");

    public static final ClassName CLASS_NAME_LIST =
            ClassName.get("java.util", "List");

    public static final ClassName CLASS_NAME_ARRAY_LIST =
            ClassName.get("java.util", "ArrayList");

    public static final ClassName CLASS_NAME_JOB_HUNTER =
            ClassName.get("me.shouheng.startup", "JobHunter");

    public static final TypeName TYPE_NAME_LIST_OF_SCHEDULER_JOB =
            ParameterizedTypeName.get(CLASS_NAME_LIST, CLASS_NAME_I_SCHEDULER_JOB);

    public static final String JOB_HUNTER_FULL_NAME =
            "me.shouheng.startup.JobHunter";

    public static final String I_SCHEDULER_JOB_FULL_NAME =
            "me.shouheng.scheduler.ISchedulerJob";

}
