package me.shouheng.startup

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import me.shouheng.scheduler.ISchedulerJob
import me.shouheng.scheduler.Logger
import me.shouheng.scheduler.Scheduler
import me.shouheng.scheduler.createScheduler
import me.shouheng.scheduler.process.IProcessMatcher
import me.shouheng.scheduler.process.ProcessMatcherImpl
import java.util.concurrent.Executor

/** The Android startup. */
class AndroidStartup(private var context: Context, builder: AndroidStartupBuilder) {
    /** The job scheduler. */
    private var scheduler: Scheduler = createScheduler {
        jobs = builder.jobs
        executor = builder.executor
        logger = builder.logger
        matcher = builder.matcher
    }

    /** Launch the startup. */
    fun launch() {
        scheduler.launch(context)
    }
}

@StartupDSL
class AndroidStartupBuilder(val context: Context) {
    var executor: Executor? = null
    var logger: Logger? = null
    var matcher: IProcessMatcher = ProcessMatcherImpl
    var jobs: MutableList<ISchedulerJob> = mutableListOf()

    /** The job hunter. */
    private var jobHunter: JobHunter? = null

    /** Scan components. */
    fun scanComponents() {
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
                this.jobs.addAll(jobs)
            }
        } catch (exception: PackageManager.NameNotFoundException) {
            throw AndroidStartupException(exception)
        } catch (exception: ClassNotFoundException) {
            throw AndroidStartupException(exception)
        }
    }

    /** Scan annotations for job by [Job]. */
    fun scanAnnotations() {
        try {
            if (jobHunter == null) {
                val hunterImplClass = Class.forName("${JobHunter::class.java.name}Impl")
                jobHunter = hunterImplClass.newInstance() as JobHunter
            }
            val jobs = jobHunter?.hunt()
            jobs?.let {
                this.jobs.addAll(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/** Get an instance of android startup. */
inline fun createStartup(context: Context, init: AndroidStartupBuilder.() -> Unit): AndroidStartup {
    val builder = AndroidStartupBuilder(context)
    builder.apply(init)
    return AndroidStartup(context, builder)
}

/** Create and launch a startup. */
inline fun launchStartup(context: Context, init: AndroidStartupBuilder.() -> Unit) {
    val builder = AndroidStartupBuilder(context)
    builder.apply(init)
    AndroidStartup(context, builder).launch()
}

@DslMarker
annotation class StartupDSL
