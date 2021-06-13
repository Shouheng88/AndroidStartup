package me.shouheng.startupsample

import android.app.Application
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import me.shouheng.scheduler.process.IProcessMatcher
import me.shouheng.scheduler.utils.ProcessUtils
import me.shouheng.startup.launchStartup
import me.shouheng.utils.app.AppUtils
import me.shouheng.utils.stability.L

class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        application = this
//        AndroidStartup.newInstance(this).jobs(
//            CrashHelperInitializeJob(),
//            ThirdPartLibrariesInitializeJob(),
//            DependentBlockingBackgroundJob(),
//            BlockingBackgroundJob()
//        ).launch()

        launchStartup(this) {
            scanAnnotations()
            // The custom process match configuration.
            matcher = object : IProcessMatcher {

                private val currentProcess by lazy {
                    val pkgName = AppUtils.getPackageName()
                    ProcessUtils.getProcessName()?.replace(pkgName, "")
                }

                override fun match(target: String): Boolean {
                    return TextUtils.isEmpty(target) || TextUtils.equals(target,
                        currentProcess
                    )
                }
            }
        }
        L.d("Current process: ${ProcessUtils.getProcessName()}")
    }

    companion object {

        private lateinit var application: Application

        fun app() = application
    }
}
