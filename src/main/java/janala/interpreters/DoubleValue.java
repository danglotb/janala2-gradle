package janala.interpreters;

public class DoubleValue extends Value {
  double concrete;

  @Override
  public Object getConcrete() {
    return concrete;
  }

  public DoubleValue(double concrete) {
    this.concrete = concrete;
  }

  public DoubleValue DADD(DoubleValue d) {
    return new DoubleValue(concrete + d.concrete);
  }

  public DoubleValue DSUB(DoubleValue d) {
    return new DoubleValue(concrete - d.concrete);
  }

  public DoubleValue DMUL(DoubleValue d) {
    return new DoubleValue(concrete * d.concrete);
  }

  public DoubleValue DDIV(DoubleValue d) {
    return new DoubleValue(concrete / d.concrete);
  }

  public DoubleValue DREM(DoubleValue d) {
    return new DoubleValue(concrete % d.concrete);
  }

  public DoubleValue DNEG() {
    return new DoubleValue(-concrete);
  }

  public IntValue D2I() {
    return new IntValue((int) concrete);
  }

  public LongValue D2L() {
    return new LongValue((long) concrete);
  }

  public FloatValue D2F() {
    return new FloatValue((float) concrete);
  }

  public IntValue DCMPL(DoubleValue d) {
    if (Double.isNaN(concrete) || Double.isNaN(d.concrete)) {
      return new IntValue(-1);
    } else if (concrete == d.concrete) {
      return new IntValue(0);
    } else if (concrete > d.concrete) {
      return new IntValue(1);
    } else {
      return new IntValue(-1);
    }
  }

  public IntValue DCMPG(DoubleValue d) {
    if (Double.isNaN(concrete) || Double.isNaN(d.concrete)) {
      return new IntValue(1);
    } else if (concrete == d.concrete) {
      return new IntValue(0);
    } else if (concrete > d.concrete) {
      return new IntValue(1);
    } else {
      return new IntValue(-1);
    }
  }
}
