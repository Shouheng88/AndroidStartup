package me.shouheng.startupsample

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDexApplication
import me.shouheng.scheduler.process.IProcessMatcher
import me.shouheng.scheduler.utils.ProcessUtils
import me.shouheng.startup.launchStartup
import me.shouheng.utils.UtilsApp
import me.shouheng.utils.app.AppUtils
import me.shouheng.utils.stability.L

/**
 * todo
注解扫描组件化问题修复，获取实例的时候通过 constructor 进行反射而不是 new 的形式
 */
class Application : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        UtilsApp.init(this)
    }

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

                override fun match(target: List<String>): Boolean {
                    return target.isEmpty() || target.contains(currentProcess)
                }
            }
        }
        L.d("Current process: ${ProcessUtils.getProcessName()}")
    }

    companion object {

        private lateinit var application: Application
    }
}
