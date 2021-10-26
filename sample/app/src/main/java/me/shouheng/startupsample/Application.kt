package me.shouheng.startupsample

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDexApplication
import me.shouheng.scheduler.utils.ProcessUtils
import me.shouheng.startup.startup
import me.shouheng.utils.UtilsApp
import me.shouheng.utils.stability.L

class Application : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        UtilsApp.init(this)
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        startup {
            withLogger(CustomLogger)
            scanAnnotations(application)
            withProcessMatcher(CustomMatcher)
        }.launch(application)
        L.d("Current process: ${ProcessUtils.getProcessName()}")
    }

    companion object {

        private lateinit var application: Application
    }
}
