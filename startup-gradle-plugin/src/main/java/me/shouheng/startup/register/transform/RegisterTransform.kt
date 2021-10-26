package me.shouheng.startup.register.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import me.shouheng.startup.register.utils.Logger
import me.shouheng.startup.register.utils.listAll
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File

/** Register transform. */
class RegisterTransform(private val project: Project): Transform() {

    companion object {
        var fileContainsInitClass: File? = null
        var hunterImplClasses: MutableList<String> = mutableListOf()
    }

    private val leftSlash = File.pathSeparatorChar == '/'

    override fun getName(): String = "StartupRegister"

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = TransformManager.SCOPE_FULL_PROJECT

    override fun isIncremental(): Boolean = false

    override fun transform(
        context: Context?,
        inputs: MutableCollection<TransformInput>?,
        referencedInputs: MutableCollection<TransformInput>?,
        outputProvider: TransformOutputProvider?,
        isIncremental: Boolean
    ) {
        Logger.i("Start scan register info in jar file.")
        val startTime = System.currentTimeMillis()

        if (!isIncremental) outputProvider?.deleteAll()

        inputs?.forEach { input ->
            scanJarInputs(input.jarInputs, outputProvider)
            scanDirectoryInputs(input.directoryInputs, outputProvider)
        }
        Logger.i("Scan finish, current cost time " + (System.currentTimeMillis() - startTime) + "ms")

        insertImplClasses()
        Logger.i("Generate code finish, current cost time: " + (System.currentTimeMillis() - startTime) + "ms")
    }

    private fun scanJarInputs(jarInputs: Collection<JarInput>, out: TransformOutputProvider?) {
        jarInputs.forEach {
            var destName = it.name
            val hexName = DigestUtils.md5Hex(it.file.absolutePath)
            if (destName.endsWith(".jar")) {
                destName = destName.substring(0, destName.length - 4)
            }
            val dest = out?.getContentLocation(destName + "_" + hexName,
                it.contentTypes, it.scopes, Format.JAR)
            if (Scanner.shouldProcessPreDexJar(it.file.absolutePath)) {
                Scanner.scanJar(it.file, dest)
            }
            FileUtils.copyFile(it.file, dest)
        }
    }

    private fun scanDirectoryInputs(directoryInputs: Collection<DirectoryInput>, out: TransformOutputProvider?) {
        directoryInputs.forEach {
            val dest = out?.getContentLocation(it.name, it.contentTypes, it.scopes, Format.DIRECTORY)
            var root = it.file.absolutePath
            if (!root.endsWith(File.separator))
                root += File.separator
            it.file.listAll().forEach { file ->
                var path = file.absolutePath.replace(root, "")
                if (!leftSlash) {
                    path = path.replace("\\\\", "/")
                }
                if (file.isFile && Scanner.shouldProcessClass(path)) {
                    Scanner.scanClass(file)
                }
            }
            FileUtils.copyDirectory(it.file, dest)
        }
    }

    private fun insertImplClasses() {
        fileContainsInitClass?.let {
            if (hunterImplClasses.isNotEmpty()) {
                hunterImplClasses.forEach { impl ->
                    Logger.i("Found hunter impl: $impl")
                }
                CodeGenerator.generate(it, hunterImplClasses)
            } else {
                Logger.e("No class implementations found for interface JubHunter.")
            }
        }
    }
}
