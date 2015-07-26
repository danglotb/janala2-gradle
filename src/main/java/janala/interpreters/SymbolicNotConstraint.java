package janala.interpreters;

import java.util.Map;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 1/2/13
 * Time: 10:52 PM
 */
public class SymbolicNotConstraint extends Constraint {
  public Constraint constraint;

  public SymbolicNotConstraint(Constraint c) {
    constraint = c;
  }

  @Override
  public void accept(ConstraintVisitor v) {
    v.visitSymbolicNot(this);
  }

  @Override
  public Constraint not() {
    return constraint;
  }

  @Override
  public Constraint substitute(Map<String, Long> assignments) {
    Constraint constraint = this.constraint.substitute(assignments);

    if (constraint == SymbolicTrueConstraint.instance) {
      return SymbolicFalseConstraint.instance;
    } else if (constraint == SymbolicFalseConstraint.instance) {
      return SymbolicTrueConstraint.instance;
    } else if (constraint == this.constraint) {
      return this;
    } else {
      return new SymbolicNotConstraint(constraint);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(" ! ");
    sb.append(constraint);
    return sb.toString();
  }
}
