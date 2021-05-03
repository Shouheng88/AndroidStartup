package me.shouheng.startupsample.jobs

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.ThreadMode
import me.shouheng.startup.annotation.StartupJob
import me.shouheng.startupsample.MainActivity
import me.shouheng.startupsample.R
import me.shouheng.uix.common.bean.TextStyleBean
import me.shouheng.uix.pages.CrashReportActivity
import me.shouheng.utils.ktx.stringOf
import me.shouheng.utils.stability.CrashHelper
import me.shouheng.utils.stability.L
import me.shouheng.utils.store.PathUtils
import java.io.File

@StartupJob
class CrashHelperInitializeJob : ISchedulerJob {

    override fun threadMode(): ThreadMode = ThreadMode.MAIN

    override fun dependencies(): List<Class<out ISchedulerJob>> = listOf(ThirdPartLibrariesInitializeJob::class.java)

    @SuppressLint("MissingPermission")
    override fun run(context: Context) {
        val dir = File(PathUtils.getExternalAppFilesPath(), "crash")
        CrashHelper.init(context as Application, dir) { crashInfo, _ ->
            CrashReportActivity.Companion.Builder(context)
                .setRestartActivity(MainActivity::class.java)
                .setMessage(crashInfo)
                .setImage(R.drawable.uix_crash_error_image)
                .setThemeStyle(R.style.Theme_AndroidStartup)
                .setTitle(stringOf(R.string.crash_title))
                .setButtonStyle(TextStyleBean().apply { textSize = 18f })
                .launch()
        }
        L.d("CrashHelperInitializeJob done! ${Thread.currentThread()} ${System.currentTimeMillis()}")
    }

}
