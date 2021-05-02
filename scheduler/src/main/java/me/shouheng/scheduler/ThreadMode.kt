package me.shouheng.scheduler

enum class ThreadMode {

    /** Will run the job in main thread. */
    MAIN,

    /** Will run the job in background thread. */
    BACKGROUND
}
