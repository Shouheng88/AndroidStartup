package me.shouheng.startupsample

import android.util.Log
import me.shouheng.scheduler.Logger

const val TAG = "CustomLogger"

object CustomLogger: Logger {
    override fun v(msg: String) {
        Log.v(TAG, msg)
    }

    override fun d(msg: String) {
        Log.d(TAG, msg)
    }

    override fun i(msg: String) {
        Log.i(TAG, msg)
    }

    override fun w(msg: String) {
        Log.w(TAG, msg)
    }

    override fun e(msg: String, th: Throwable?) {
        Log.e(TAG, msg)
    }
}