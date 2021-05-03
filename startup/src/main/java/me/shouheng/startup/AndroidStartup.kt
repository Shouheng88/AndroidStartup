package me.shouheng.startup

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.Logger
import me.shouheng.scheduler.Scheduler
import java.util.concurrent.Executor

/** The Android startup. */
class AndroidStartup constructor(val context: Context) {

    /** The job scheduler. */
    private val scheduler = Scheduler()

    /** Set the custom executor. */
    fun setExecutor(executor: Executor): AndroidStartup {
        scheduler.setExecutor(executor)
        return this
    }

    /** Set the logger. */
    fun setLogger(logger: Logger): AndroidStartup {
        scheduler.setLogger(logger)
        return this
    }

    /** To specify jobs for the scheduler. */
    fun jobs(vararg jobs: ISchedulerJob): AndroidStartup {
        scheduler.jobs(*jobs)
        return this
    }

    /** Scan components. */
    fun scanComponents(): AndroidStartup {
        try {
            val provider = ComponentName(context.packageName, AndroidStartupProvider::class.java.name)
            val providerInfo: ProviderInfo = context.packageManager
                .getProviderInfo(provider, PackageManager.GET_META_DATA)
            val metadata = providerInfo.metaData
            val startup: String = context.getString(R.string.android_startup)
            if (metadata != null) {
                val jobs = mutableListOf<ISchedulerJob>()
                val keys = metadata.keySet()
                for (key in keys) {
                    val value = metadata.getString(key, null)
                    if (startup == value) {
                        val clazz = Class.forName(key)
                        if (ISchedulerJob::class.java.isAssignableFrom(clazz)) {
                            val instance = clazz.getDeclaredConstructor().newInstance()
                            jobs.add(instance as ISchedulerJob)
                        }
                    }
                }
                scheduler.jobs(*jobs.toTypedArray())
            }
        } catch (exception: PackageManager.NameNotFoundException) {
            throw AndroidStartupException(exception)
        } catch (exception: ClassNotFoundException) {
            throw AndroidStartupException(exception)
        }
        return this
    }

    /** Launch the startup. */
    fun launch() {
        scheduler.launch(context)
    }

    companion object {

        /** Get a new instance of Android startup. */
        fun newInstance(context: Context) = AndroidStartup(context)
    }
}
