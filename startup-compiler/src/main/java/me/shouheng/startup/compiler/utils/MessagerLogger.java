package me.shouheng.startup.compiler.utils;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/** The logger based on Messager. */
public final class MessagerLogger implements ILogger {

    private final Messager msg;

    public MessagerLogger(Messager messager) {
        msg = messager;
    }

    @Override
    public void info(CharSequence info) {
        if (Utils.isNotEmpty(info)) {
            msg.printMessage(Diagnostic.Kind.NOTE, Consts.PREFIX_OF_LOGGER + info);
        }
    }

    @Override
    public void warning(CharSequence warning) {
        if (Utils.isNotEmpty(warning)) {
            msg.printMessage(Diagnostic.Kind.WARNING, Consts.PREFIX_OF_LOGGER + warning);
        }
    }

    @Override
    public void error(CharSequence error) {
        if (Utils.isNotEmpty(error)) {
            msg.printMessage(Diagnostic.Kind.ERROR, Consts.PREFIX_OF_LOGGER + "An exception is encountered, [" + error + "]");
        }
    }

    @Override
    public void error(Throwable error) {
        if (null != error) {
            msg.printMessage(Diagnostic.Kind.ERROR, Consts.PREFIX_OF_LOGGER + "An exception is encountered, [" + error.getMessage() + "]" + "\n" + formatStackTrace(error.getStackTrace()));
        }
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            sb.append("    at ").append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
