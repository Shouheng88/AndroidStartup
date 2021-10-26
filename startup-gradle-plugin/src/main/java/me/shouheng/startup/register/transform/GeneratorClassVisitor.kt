package me.shouheng.startup.register.transform

import me.shouheng.startup.register.GENERATE_TO_METHOD_NAME
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/** Class visitor for generator. */
class GeneratorClassVisitor(
    api: Int,
    cv: ClassVisitor,
    private val classes: List<String>
): ClassVisitor(api, cv) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == GENERATE_TO_METHOD_NAME) {
            mv = GeneratorMethodVisitor(Opcodes.ASM5, mv, classes)
        }
        return mv
    }
}
