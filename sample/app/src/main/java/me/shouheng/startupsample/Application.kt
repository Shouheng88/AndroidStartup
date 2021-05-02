package me.shouheng.startupsample

import android.app.Application
import android.support.multidex.MultiDexApplication
import me.shouheng.scheduler.Scheduler
import me.shouheng.startupsample.jobs.BlockingBackgroundJob
import me.shouheng.startupsample.jobs.CrashHelperInitializeJob
import me.shouheng.startupsample.jobs.DependentBlockingBackgroundJob
import me.shouheng.startupsample.jobs.ThirdPartLibrariesInitializeJob

class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        application = this
        Scheduler.newInstance().jobs(
            CrashHelperInitializeJob,
            ThirdPartLibrariesInitializeJob,
            DependentBlockingBackgroundJob,
            BlockingBackgroundJob
        ).launch(this)
    }

    companion object {

        private lateinit var application: Application

        fun app() = application
    }
}
