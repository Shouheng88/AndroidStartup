package me.shouheng.startupsample

import android.annotation.SuppressLint
import android.app.Application
import android.support.multidex.MultiDexApplication
import me.shouheng.uix.common.UIX
import me.shouheng.uix.common.bean.TextStyleBean
import me.shouheng.uix.pages.CrashReportActivity
import me.shouheng.utils.ktx.stringOf
import me.shouheng.utils.stability.CrashHelper
import me.shouheng.utils.stability.L
import me.shouheng.utils.store.PathUtils
import me.shouheng.vmlib.VMLib
import java.io.File

class Application : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        application = this
        VMLib.onCreate(this)
        UIX.init(this)
        UIX.setDebug(BuildConfig.DEBUG)
        L.getConfig().setLogSwitch(BuildConfig.DEBUG)
        configCrashHelper(this)
    }

    companion object {

        private lateinit var application: Application

        fun app() = application

        @SuppressLint("MissingPermission")
        fun configCrashHelper(application: Application) {
            val dir = File(PathUtils.getExternalAppFilesPath(), "crash")
            CrashHelper.init(application, dir) { crashInfo, _ ->
                CrashReportActivity.Companion.Builder(application)
                    .setRestartActivity(MainActivity::class.java)
                    .setMessage(crashInfo)
                    .setImage(R.drawable.uix_crash_error_image)
                    .setThemeStyle(R.style.Theme_AndroidStartup)
                    .setTitle(stringOf(R.string.crash_title))
                    .setButtonStyle(TextStyleBean().apply { textSize = 18f })
                    .launch()
            }
        }
    }
}
