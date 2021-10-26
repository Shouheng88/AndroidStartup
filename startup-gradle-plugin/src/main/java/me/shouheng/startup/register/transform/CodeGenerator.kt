package me.shouheng.startup.register.transform

import me.shouheng.startup.register.GENERATE_TO_CLASS_FILE_NAME
import me.shouheng.startup.register.utils.Logger
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.InputStream

/** The hunter class impl code generator. */
object CodeGenerator {

    /** Generate classes to dest file. */
    fun generate(dest: File, classes: List<String>) {
        if (dest.name.endsWith(".jar")) {
            insertInitCodeIntoJarFile(dest, classes)
        }
    }

    private fun insertInitCodeIntoJarFile(dest: File, classes: List<String>) {
        val optJar = File(dest.parent, dest.name + ".opt")
        if (optJar.exists()) optJar.delete()

        val file = JarFile(dest)
        val enumeration = file.entries()
        val jarOutputStream = JarOutputStream(FileOutputStream(optJar))

        while (enumeration.hasMoreElements()) {
            val jarEntry = enumeration.nextElement() as JarEntry
            val entryName = jarEntry.name
            val zipEntry = ZipEntry(entryName)
            val inputStream = file.getInputStream(jarEntry)
            jarOutputStream.putNextEntry(zipEntry)
            if (GENERATE_TO_CLASS_FILE_NAME == entryName) {
                Logger.i("Insert init code to class >> $entryName")
                val bytes = referHackWhenInit(inputStream, classes)
                jarOutputStream.write(bytes)
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()

        if (dest.exists()) dest.delete()
        optJar.renameTo(dest)
    }

    private fun referHackWhenInit(inputStream: InputStream, classes: List<String>): ByteArray {
        val cr = ClassReader(inputStream)
        val cw = ClassWriter(cr, 0)
        val cv = GeneratorClassVisitor(Opcodes.ASM5, cw, classes)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }
}