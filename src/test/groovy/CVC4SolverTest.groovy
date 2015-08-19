package janala.solvers

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

import janala.config.Config
import janala.interpreters.Constraint;
import janala.interpreters.SymbolicInt;
import janala.interpreters.IntValue;

import org.junit.Test
import org.junit.Before
import org.junit.Ignore

import groovy.transform.CompileStatic

@CompileStatic
class CVC4SolverTest {
  @Before
  void setup() {
    Config.instance.formulaFile = "formula_test.dat"
    Config.instance.inputs = "inputs.dat"
    Config.instance.printFormulaAndSolutions = true
  }

  @Ignore
  @Test
	void testSolver() {
    SymbolicInt x1 = new SymbolicInt(1)
    
    InputElement input = new InputElement(1, new IntValue(0))
		x1.setOp(SymbolicInt.COMPARISON_OPS.NE)

		List<Constraint> constraints = []
    constraints.add(x1)
    println(constraints)
    List<InputElement> inputs = [input]

    CVC4Solver solver = new CVC4Solver()
    solver.setInputs(inputs)
    solver.setPathConstraint(constraints)
    def x = solver.solve()
    assertTrue(x)
	}
}