package me.shouheng.startupsample

import me.shouheng.scheduler.process.IProcessMatcher
import me.shouheng.scheduler.utils.ProcessUtils
import me.shouheng.utils.app.AppUtils

/** Custom process matcher. */
object CustomMatcher : IProcessMatcher {

    private val currentProcess by lazy {
        val pkgName = AppUtils.getPackageName()
        ProcessUtils.getProcessName()?.replace(pkgName, "")
    }

    override fun match(target: List<String>): Boolean {
        return target.isEmpty() || target.contains(currentProcess)
    }
}
