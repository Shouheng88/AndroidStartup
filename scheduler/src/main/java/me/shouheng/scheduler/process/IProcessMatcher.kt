package me.shouheng.scheduler.process

/** The process matcher interface. */
interface IProcessMatcher {

    /**
     * Is the target processes match the current process.
     * True if the target processes contains current process else not.
     * The scheduler will only invoke the task match current processes.
     * Even the task may be skipped, its dependencies will still be notified.
     */
    fun match(target: List<String>): Boolean
}
