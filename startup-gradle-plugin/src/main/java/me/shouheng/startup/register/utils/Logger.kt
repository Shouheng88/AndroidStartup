package me.shouheng.startup.register.utils

import org.gradle.api.Project
import org.gradle.api.logging.Logger

object Logger {

    var logger: Logger? = null

    fun make(project: Project) {
        logger = project.logger
    }

    fun i(info: String) {
        logger?.info("Startup::Register >>> $info")
    }

    fun e(error: String) {
        logger?.error("Startup::Register >>> $error")
    }

    fun w(warning: String) {
        logger?.warn("Startup::Register >>> $warning")
    }
}
