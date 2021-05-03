package me.shouheng.startup.compiler.utils;

/** The logger interface. */
public interface ILogger {

    /** Log for info level. */
    void info(CharSequence info);

    /** Log for warning level. */
    void warning(CharSequence warning);

    /** Log for error level. */
    void error(CharSequence error);

    /** Log for error level. */
    void error(Throwable error);
}
