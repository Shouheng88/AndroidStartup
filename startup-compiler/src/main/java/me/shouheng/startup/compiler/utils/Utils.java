package me.shouheng.startup.compiler.utils;

/** Utils pack. */
public class Utils {

    /** Is char sequence empty. */
    public static boolean isEmpty(CharSequence sequence) {
        return sequence == null || sequence.length() == 0;
    }

    /** Is char sequence not empty. */
    public static boolean isNotEmpty(CharSequence sequence) {
        return sequence != null && sequence.length() > 0;
    }
}
