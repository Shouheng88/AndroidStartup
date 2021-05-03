package me.shouheng.scheduler

import java.lang.RuntimeException

class SchedulerException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
