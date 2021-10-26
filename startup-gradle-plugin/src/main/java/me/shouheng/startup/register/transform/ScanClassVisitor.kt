package me.shouheng.startup.register.transform

import me.shouheng.startup.register.JON_HUNTER_FULL_PATH
import me.shouheng.startup.register.transform.RegisterTransform.Companion.hunterImplClasses
import org.objectweb.asm.ClassVisitor

/** Scan class to find job hunter implementation classes. */
class ScanClassVisitor(api: Int, cv: ClassVisitor): ClassVisitor(api, cv) {

    override fun visit(version: Int, access: Int, name: String?,
                       signature: String?, superName: String?, interfaces: Array<String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
        interfaces?.any { it == JON_HUNTER_FULL_PATH }?.let {
            if (it && name != null && !hunterImplClasses.contains(name)) {
                hunterImplClasses.add(name)
            }
        }
    }
}
