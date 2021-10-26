package me.shouheng.startup.register.transform

import me.shouheng.startup.register.GENERATE_TO_CLASS_FILE_NAME
import me.shouheng.startup.register.HUNTER_CLASS_PACKAGE_NAME
import me.shouheng.startup.register.utils.Logger
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

import java.util.jar.JarEntry
import java.util.jar.JarFile

/** Scanner for transform. */
object Scanner {

    fun scanJar(jarFile: File, destFile: File?) {
        val file = JarFile(jarFile)
        val enumeration = file.entries()
        while (enumeration.hasMoreElements()) {
            val jarEntry = enumeration.nextElement() as JarEntry
            val entryName = jarEntry.name
            if (entryName.startsWith(HUNTER_CLASS_PACKAGE_NAME)) {
                val inputStream = file.getInputStream(jarEntry)
                scanClass(inputStream)
                inputStream.close()
            } else if (GENERATE_TO_CLASS_FILE_NAME == entryName) {
                RegisterTransform.fileContainsInitClass = destFile
            }
        }
        file.close()
    }

    fun shouldProcessPreDexJar(path: String): Boolean {
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    fun shouldProcessClass(entryName: String): Boolean {
        return entryName.startsWith(HUNTER_CLASS_PACKAGE_NAME)
    }

    fun scanClass(file: File) {
        scanClass(FileInputStream(file))
    }

    private fun scanClass(inputStream: InputStream) {
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, 0)
        val cv = ScanClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }
}