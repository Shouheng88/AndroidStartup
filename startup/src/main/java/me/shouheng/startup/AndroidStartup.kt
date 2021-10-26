package me.shouheng.startup

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.text.TextUtils
import me.shouheng.scheduler.*
import me.shouheng.scheduler.process.IProcessMatcher
import me.shouheng.scheduler.process.ProcessMatcherImpl
import me.shouheng.startup.utils.ClassUtils
import me.shouheng.startup.utils.PackageUtils
import me.shouheng.startup.utils.STARTUP_SP_CACHE_KEY
import me.shouheng.startup.utils.STARTUP_SP_KEY_HUNTERS
import java.util.concurrent.Executor

@DslMarker annotation class StartupMarker

/** The Android startup. */
class AndroidStartup(private val scheduler: Scheduler) {
    /** Launch the startup. */
    fun launch(context: Context) {
        scheduler.launch(context)
    }
}

@StartupMarker class AndroidStartupBuilder {
    private var executor: Executor? = null
    private var logger: Logger? = null
    private var matcher: IProcessMatcher = ProcessMatcherImpl
    private var jobs: MutableList<ISchedulerJob> = mutableListOf()
    private var debuggable: Boolean = false

    private var registerByPlugin = false

    /** The job hunters. */
    private var jobHunters: List<JobHunter>? = null

    /** Specify debuggable for startup. */
    fun withDebuggable(debuggable: Boolean) {
        this.debuggable = debuggable
    }

    /** Specify the executor. */
    fun withExecutor(executor: Executor) {
        this.executor = executor
    }

    /** Specify the logger. */
    fun withLogger(logger: Logger) {
        this.logger = logger
    }

    /** Specify the process matcher. */
    fun withProcessMatcher(matcher: IProcessMatcher) {
        this.matcher = matcher
    }

    /** Specify jobs. */
    fun withJobs(jobs: List<ISchedulerJob>) {
        val list = mutableListOf<ISchedulerJob>()
        list.addAll(jobs)
        this.jobs = list
    }

    /** Scan components. */
    fun scanComponents(context: Context) {
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

    /** Scan annotations for job by [ISchedulerJob]. */
    fun scanAnnotations(context: Context) {
        try {
            if (jobHunters == null) {
                gatherHunters()
                if (registerByPlugin) {
                    logger?.i("Gathered hunters by startup-register plugin.");
                } else {
                    jobHunters = gatherByPackageScan(context)
                }
            }
            jobHunters?.forEach { jobHunter ->
                val jobs = jobHunter.hunt()
                jobs?.let {
                    this.jobs.addAll(it)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Search hunters by app package scan and cache. */
    private fun gatherByPackageScan(context: Context): List<JobHunter> {
        val hunters = mutableListOf<JobHunter>()
        val hunterImplClasses: Set<String>
        if (debuggable || PackageUtils.isNewVersion(context, logger)) {
            hunterImplClasses = ClassUtils.getFileNameByPackageName(
                context, "me.shouheng.startup.hunter", executor?:DefaultExecutor.INSTANCE)
            if (hunterImplClasses.isNotEmpty()) {
                context.getSharedPreferences(STARTUP_SP_CACHE_KEY, Context.MODE_PRIVATE)
                    .edit().putStringSet(STARTUP_SP_KEY_HUNTERS, hunterImplClasses).apply();
            }
            PackageUtils.updateVersion(context)
        } else {
            hunterImplClasses = HashSet(
                context.getSharedPreferences(STARTUP_SP_CACHE_KEY, Context.MODE_PRIVATE)
                    .getStringSet(STARTUP_SP_KEY_HUNTERS, setOf())
            )
        }
        hunterImplClasses.forEach {
            val hunterImplClass = Class.forName(it)
            hunters.add(hunterImplClass.newInstance() as JobHunter)
        }
        return hunters
    }

    private fun gatherHunters() {
        registerByPlugin = false
        jobHunters = mutableListOf()
    }

    private fun addHunter(className: String) {
        registerByPlugin = true
        if (!TextUtils.isEmpty(className)) {
            try {
                val clazz = Class.forName(className)
                val obj = clazz.getConstructor().newInstance()
                if (obj is JobHunter) {
                    (jobHunters as? MutableList)?.add(obj)
                } else {
                    logger?.i("Register failed, class name: $className should implements one " +
                            "of me/shouheng/startup/JobHunter.")
                }
            } catch (e: java.lang.Exception) {
                logger?.e("Register class error: $className", e)
            }
        }
    }

    internal fun build(): AndroidStartup {
        return AndroidStartup(createScheduler {
            withJobs(jobs)
            executor?.let { withExecutor(it) }
            logger?.let { withLogger(it) }
            withProcessMatcher(matcher)
        })
    }
}

/** Create and launch a startup. */
fun startup(init: AndroidStartupBuilder.() -> Unit): AndroidStartup {
    val builder = AndroidStartupBuilder()
    builder.apply(init)
    return builder.build()
}
