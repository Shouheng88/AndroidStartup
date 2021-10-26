package me.shouheng.startup.register.transform

import me.shouheng.startup.register.ADD_HUNTER_METHOD_NAME
import me.shouheng.startup.register.GENERATE_TO_CLASS_NAME
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/** Generator method visitor. */
class GeneratorMethodVisitor(
    api: Int,
    mv: MethodVisitor,
    private val classes: List<String>
): MethodVisitor(api, mv) {

    override fun visitInsn(opcode: Int) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)) {
            classes.forEach {
                val name = it.replace("/", ".")
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitLdcInsn(name) // Class name
                // generate invoke register method into AndroidStartupBuilder.gatherHunters()
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, GENERATE_TO_CLASS_NAME,
                    ADD_HUNTER_METHOD_NAME, "(Ljava/lang/String;)V", false)
            }
        }
        super.visitInsn(opcode)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack+4, maxLocals)
    }
}