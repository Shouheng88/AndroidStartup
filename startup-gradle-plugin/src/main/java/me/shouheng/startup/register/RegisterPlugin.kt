package me.shouheng.startup.register

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import me.shouheng.startup.register.transform.RegisterTransform
import me.shouheng.startup.register.utils.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

/** Hunter register plugin. */
class RegisterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val hasApp = project.plugins.hasPlugin(AppPlugin::class.java)
        if (hasApp) {
            Logger.make(project)
            Logger.i("Project enable startup-register plugin")
            val android = project.extensions.getByType(AppExtension::class.java)
            val transform = RegisterTransform(project)
            android.registerTransform(transform)
        }
    }
}
