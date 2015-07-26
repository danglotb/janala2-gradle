package janala.interpreters;

import janala.solvers.History;

/**
 * Author: Koushik Sen (ksen@cs.berkeley.edu)
 * Date: 7/8/12
 * Time: 9:10 AM
 */
public class LongObjectValue extends ObjectValue {
  LongValue longValue;

  public LongObjectValue() {
    super(100, -1);
  }

  @Override
  public Value invokeMethod(String name, Value[] args, History history) {
    if (name.equals("<init>")) {
      if (args[0] instanceof LongValue) this.longValue = (LongValue) args[0];
      else this.longValue = new LongValue(Long.parseLong(((StringValue) args[0]).getConcrete()));
      return PlaceHolder.instance;
    } else if (name.equals("intValue")) {
      return new IntValue((int) longValue.concrete, longValue.symbolic);
    } else if (name.equals("longValue")) {
      return new LongValue((long) longValue.concrete, longValue.symbolic);
    } else if (name.equals("shortValue")) {
      return new IntValue((short) longValue.concrete, longValue.symbolic);
    } else if (name.equals("byteValue")) {
      return new IntValue((byte) longValue.concrete, longValue.symbolic);
    } else if (name.equals("equals")) {
      if (args[0] instanceof LongObjectValue) {
        LongObjectValue i2 = (LongObjectValue) args[0];
        LongValue ret = longValue.LSUB(i2.longValue);
        ret.concrete = ret.concrete == 0 ? 1 : 0;
        if (ret.symbolic != null) {
          ret.symbolic = ret.symbolic.setop(SymbolicInt.COMPARISON_OPS.EQ);
          IntValue ret2 = new IntValue((int) ret.concrete, ret.symbolic);
          return ret2;
        }
      }
    } else if (name.equals("compareTo")) {
      if (args[0] instanceof LongObjectValue) {
        LongObjectValue i2 = (LongObjectValue) args[0];
        LongValue ret = longValue.LSUB(i2.longValue);
        if (ret.concrete > 0) ret.concrete = 1;
        else if (ret.concrete == 0) ret.concrete = 0;
        else ret.concrete = -1;
        IntValue ret2 = new IntValue((int) ret.concrete, ret.symbolic);
        return ret2;
      }
    }
    return PlaceHolder.instance;
  }
}
