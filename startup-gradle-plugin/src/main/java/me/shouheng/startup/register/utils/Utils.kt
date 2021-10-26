package me.shouheng.startup.register.utils

import java.io.File

/** Get all child files under given file. */
fun File.listAll(): List<File> {
    if (this.isFile) return listOf(this)
    val files = mutableListOf<File>()
    val dirs = mutableListOf(this)
    while (dirs.isNotEmpty()) {
        dirs.removeAt(0).listFiles()?.forEach {
            if (it.isDirectory) dirs.add(it) else files.add(it)
        }
    }
    return files
}
