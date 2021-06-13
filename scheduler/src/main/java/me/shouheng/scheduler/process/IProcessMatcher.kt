package me.shouheng.scheduler.process

/** The process matcher interface. */
interface IProcessMatcher {

    /**
     * Is the target process match the current process.
     * True if the target is the same as current else not.
     * The scheduler will only invoke the task match current process.
     * Even the task may be skipped, its dependencies will still be notified.
     */
    fun match(target: String): Boolean
}
