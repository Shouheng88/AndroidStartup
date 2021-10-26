package me.shouheng.startup.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.text.TextUtils
import me.shouheng.scheduler.Logger

const val STARTUP_SP_CACHE_KEY      = "SP_STARTUP_CACHE"
const val STARTUP_SP_KEY_HUNTERS    = "HUNTERS"

const val LAST_VERSION_NAME         = "LAST_VERSION_NAME"
const val LAST_VERSION_CODE         = "LAST_VERSION_CODE"

/** Package utils */
object PackageUtils {

    private var NEW_VERSION_NAME: String? = null
    private var NEW_VERSION_CODE = 0

    /** Detect if app version changed. */
    fun isNewVersion(context: Context, logger: Logger?): Boolean {
        val packageInfo = getPackageInfo(context, logger)
        return if (null != packageInfo) {
            val versionName = packageInfo.versionName
            val versionCode = packageInfo.versionCode
            val sp = context.getSharedPreferences(STARTUP_SP_CACHE_KEY, Context.MODE_PRIVATE)
            if (versionName != sp.getString(LAST_VERSION_NAME, null)
                || versionCode != sp.getInt(LAST_VERSION_CODE, -1)) {
                // new version
                NEW_VERSION_NAME = versionName
                NEW_VERSION_CODE = versionCode
                true
            } else {
                false
            }
        } else {
            true
        }
    }

    /** Update app version info. */
    fun updateVersion(context: Context) {
        if (!TextUtils.isEmpty(NEW_VERSION_NAME) && NEW_VERSION_CODE != 0) {
            val sp = context.getSharedPreferences(STARTUP_SP_CACHE_KEY, Context.MODE_PRIVATE)
            sp.edit().putString(LAST_VERSION_NAME, NEW_VERSION_NAME)
                .putInt(LAST_VERSION_CODE, NEW_VERSION_CODE).apply()
        }
    }

    private fun getPackageInfo(context: Context, logger: Logger?): PackageInfo? {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = context.packageManager.getPackageInfo(
                context.packageName, PackageManager.GET_CONFIGURATIONS)
        } catch (ex: Exception) {
            logger?.e("PackageUtils", ex)
        }
        return packageInfo
    }
}
