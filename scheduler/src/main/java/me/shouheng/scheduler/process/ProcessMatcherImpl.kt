package me.shouheng.scheduler.process

import android.text.TextUtils
import me.shouheng.scheduler.utils.ProcessUtils

/** The default matcher implementation for scheduler. */
object ProcessMatcherImpl : IProcessMatcher {

    /** Name of current process. */
    private val currentProcess: String by lazy {
        try {
            ProcessUtils.getProcessName() ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * If the process is not specified or the process is same to current process,
     * the true will be returned else false.
     */
    override fun match(target: String): Boolean {
        return TextUtils.isEmpty(target) || TextUtils.equals(target, currentProcess)
    }
}
