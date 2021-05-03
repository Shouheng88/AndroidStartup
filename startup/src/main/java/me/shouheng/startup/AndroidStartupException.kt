package me.shouheng.startup

import java.lang.RuntimeException

/** Android startup exception. */
class AndroidStartupException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}
