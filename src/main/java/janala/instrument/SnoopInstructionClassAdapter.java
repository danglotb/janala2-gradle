
package janala.instrument;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SnoopInstructionClassAdapter extends ClassVisitor {
  public SnoopInstructionClassAdapter(ClassVisitor cv) {
    super(Opcodes.ASM4, cv);
  }

  public MethodVisitor visitMethod(
      int access, String name, String desc, String signature, String[] exceptions) {
    Coverage.instance.setLastMethod(name + ":" + signature);
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    if (mv != null) {
      mv = new SnoopInstructionMethodAdapter(mv, name.equals("<init>"), Coverage.get());
    }

    return mv;
  }
}
