package me.shouheng.startupsample

import android.app.Application
import android.support.multidex.MultiDexApplication
import me.shouheng.startup.AndroidStartup
import me.shouheng.startupsample.jobs.BlockingBackgroundJob
import me.shouheng.startupsample.jobs.CrashHelperInitializeJob
import me.shouheng.startupsample.jobs.DependentBlockingBackgroundJob
import me.shouheng.startupsample.jobs.ThirdPartLibrariesInitializeJob

class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        application = this
//        AndroidStartup.newInstance(this).jobs(
//            CrashHelperInitializeJob,
//            ThirdPartLibrariesInitializeJob,
//            DependentBlockingBackgroundJob,
//            BlockingBackgroundJob
//        ).launch()
    }

    companion object {

        private lateinit var application: Application

        fun app() = application
    }
}
