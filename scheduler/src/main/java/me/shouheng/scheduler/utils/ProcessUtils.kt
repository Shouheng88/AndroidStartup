package me.shouheng.scheduler.utils

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/** The process utils. */
object ProcessUtils {

    /** Get name of current process. */
    fun getProcessName(): String? {
        return if (Build.VERSION.SDK_INT >= 28)
            Application.getProcessName()
        else try {
            // Using the same technique as Application.getProcessName() for older devices
            // Using reflection since ActivityThread is an internal API
            @SuppressLint("PrivateApi") val activityThread = Class.forName("android.app.ActivityThread")
            // Before API 18, the method was incorrectly named "currentPackageName", but it still returned the process name
            // See https://github.com/aosp-mirror/platform_frameworks_base/commit/b57a50bd16ce25db441da5c1b63d48721bb90687
            val methodName = if (Build.VERSION.SDK_INT >= 18) "currentProcessName" else "currentPackageName"
            val getProcessName: Method = activityThread.getDeclaredMethod(methodName)
            getProcessName.invoke(null) as String
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        }
    }
}