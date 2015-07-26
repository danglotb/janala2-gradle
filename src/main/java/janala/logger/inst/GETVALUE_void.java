package janala.logger.inst;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 6/16/12
 * Time: 9:22 AM
 */
public class GETVALUE_void extends Instruction {

  public GETVALUE_void() {
    super(-1, -1);
  }

  public void visit(IVisitor visitor) {
    visitor.visitGETVALUE_void(this);
  }

  @Override
  public String toString() {
    return "GETVALUE_void";
  }
}
