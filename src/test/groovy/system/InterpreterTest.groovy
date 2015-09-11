package janala.system

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

import janala.logger.DJVM
import janala.logger.DirectConcolicExecution
import janala.logger.ClassNames
import janala.interpreters.ConcolicInterpreter
import janala.interpreters.IntValue
import janala.interpreters.Value
import janala.instrument.Coverage

import org.junit.Before
import org.junit.Test

import groovy.transform.CompileStatic

// System test.
@CompileStatic
class IntepreterTest {
  private ConcolicInterpreter interpreter
  private ClassNames classNames = ClassNames.getInstance()

  @Before
  void setup() {
    DirectConcolicExecution dc = new DirectConcolicExecution()
    DJVM.setInterpreter(dc)
    interpreter = dc.intp
  }

  private int greaterThanZero(int x) {
    DJVM.ILOAD(0, 0, 1); // x is the 1st local (0 is)
    DJVM.IFLE(1, 0, 6);
    if (x > 0) {
      DJVM.SPECIAL(1); // True branch
      DJVM.ICONST_1(2, 0);
      DJVM.IRETURN(4, 0);
      return 1;
    }
    DJVM.ICONST_M1(5, 0);
    DJVM.IRETURN(6, 0);
    return -1;
  }

  @Test
  void testGreaterThanZero() {
    int classIdx = classNames.get("janala/system/IntepreterTest")

    DJVM.NEW(0, 1, "janala/system/IntepreterTest", classIdx)
    DJVM.ICONST_1(1, 1)
    DJVM.INVOKEVIRTUAL(2, 1, "janala/system/IntepreterTest", "greaterThanZero", "(I)I")
    int x = greaterThanZero(1)
    assertEquals(x, 1)

    DJVM.INVOKEMETHOD_END()
    DJVM.flush()
    Value y = interpreter.getCurrentFrame().peek()
    assertEquals(new IntValue(1), y)
  }
}