package me.shouheng.scheduler

import android.util.Log

/** The logger. */
interface Logger {

    fun v(msg: String)

    fun d(msg: String)

    fun i(msg: String)

    fun w(msg: String)

    fun e(msg: String, th: Throwable? = null)
}

/** The logger for Android startup. */
object SchedulerLogger: Logger {

    private const val TAG = "Scheduler"

    /** Enable logger or not. */
    var enable = true

    override fun v(msg: String) {
        if (enable) Log.v(TAG, msg)
    }

    override fun d(msg: String) {
        if (enable) Log.d(TAG, msg)
    }

    override fun i(msg: String) {
        if (enable) Log.i(TAG, msg)
    }

    override fun w(msg: String) {
        if (enable) Log.w(TAG, msg)
    }

    override fun e(msg: String, th: Throwable?) {
        if (enable) Log.e(TAG, msg, th)
    }
}
