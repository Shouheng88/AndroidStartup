package me.shouheng.scheduler.process

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
     * If the processes contains current process or the task should
     * run in all processes, this will return true else it return false.
     */
    override fun match(target: List<String>): Boolean {
        return target.isEmpty() || target.contains(currentProcess)
    }
}
