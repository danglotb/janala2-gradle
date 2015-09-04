package janala.interpreters

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when

import janala.solvers.History
import janala.solvers.Solver
import janala.solvers.BranchElement
import janala.logger.ClassNames
import janala.logger.ObjectInfo
import janala.logger.inst.*
import janala.instrument.Coverage

import org.junit.Before
import org.junit.Test
import groovy.transform.CompileStatic

@CompileStatic
class ConcolicInterpreterTest {
  private ClassDepot classDepot
  private ClassNames classNames
  private ConcolicInterpreter interpreter
  private Solver solver
  private History history
  private Coverage coverage
 
  @Before
  void setup() {
    classDepot = mock(ClassDepot.class)
    classNames = new ClassNames(classDepot)
    solver = mock(Solver.class)
    history = new History(solver)
    coverage = mock(Coverage.class)
    interpreter = new ConcolicInterpreter(classNames, history, coverage)
  }

  @Test
  void testICONST0() {
    interpreter.visitICONST_0(new ICONST_0(0, 0))
    Frame frame = interpreter.getCurrentFrame()
    assertEquals(new IntValue(0), frame.peek())
  }

  @Test
  void testICONST1() {
    interpreter.visitICONST_1(new ICONST_1(0, 0))
    Frame frame = interpreter.getCurrentFrame()
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testICONST2() {
    interpreter.visitICONST_2(new ICONST_2(0, 0))
    Frame frame = interpreter.getCurrentFrame()
    assertEquals(new IntValue(2), frame.peek())
  }

  @Test
  void testICONST3() {
    interpreter.visitICONST_3(new ICONST_3(0, 0))
    Frame frame = interpreter.getCurrentFrame()
    assertEquals(new IntValue(3), frame.peek())
  }

  @Test
  void testICONST4() {
    interpreter.visitICONST_4(new ICONST_4(0, 0))
    Frame frame = interpreter.getCurrentFrame()
    assertEquals(new IntValue(4), frame.peek())
  }

  @Test
  void testICONST5() {
    interpreter.visitICONST_5(new ICONST_5(0, 0))
    Frame frame = interpreter.getCurrentFrame()
    assertEquals(new IntValue(5), frame.peek())
  }

  @Test
  void testIADD() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push(new IntValue(1))
    interpreter.visitIADD(new IADD(0, 0))
    assertEquals(new IntValue(2), frame.peek())
  }

  @Test
  void testISUB() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(2))
    frame.push(new IntValue(1))
    interpreter.visitISUB(new ISUB(0, 0))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testIFICMPLT_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(0))
    frame.push(new IntValue(1))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIF_ICMPLT(new IF_ICMPLT(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFICMPLT_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push(new IntValue(1))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIF_ICMPLT(new IF_ICMPLT(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testIFEQ_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(0))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIFEQ(new IFEQ(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFEQ_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIFEQ(new IFEQ(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }  

  @Test
  void testIFNE_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIFNE(new IFNE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFNE_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(0))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIFNE(new IFNE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testIFGE_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIFGE(new IFGE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFGE_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(-1))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIFGE(new IFGE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testIFGT_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIFGT(new IFGT(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFGT_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(0))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIFGT(new IFGT(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testIFLE_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(0))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIFLE(new IFLE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFLE_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(-1))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIFLE(new IFLE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }       
  
  @Test
  void testIFLT_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(-1))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIFLT(new IFLT(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFLT_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(0))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIFLT(new IFLT(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  } 

  @Test
  void testIFICMEQ_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push(new IntValue(1))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIF_ICMPEQ(new IF_ICMPEQ(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFICMPEQ_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(0))
    frame.push(new IntValue(1))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIF_ICMPEQ(new IF_ICMPEQ(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  } 

  @Test
  void testIFICMNE_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push(new IntValue(0))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIF_ICMPNE(new IF_ICMPNE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFICMPNE_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push(new IntValue(1))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIF_ICMPNE(new IF_ICMPNE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testIFICMLE_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push(new IntValue(1))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIF_ICMPLE(new IF_ICMPLE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFICMPLE_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push(new IntValue(0))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIF_ICMPLE(new IF_ICMPLE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testIFICMGT_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push(new IntValue(0))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIF_ICMPGT(new IF_ICMPGT(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFICMPGT_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push(new IntValue(1))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIF_ICMPGT(new IF_ICMPGT(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testIFICMGE_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push(new IntValue(0))
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIF_ICMPGE(new IF_ICMPGE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFICMPGE_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(0))
    frame.push(new IntValue(1))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIF_ICMPGE(new IF_ICMPGE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testIFACMPEQ_true() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    frame.push(v)
    frame.push(v)
    interpreter.setNext(new SPECIAL(1)) 
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIF_ACMPEQ(new IF_ACMPEQ(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFACMPEQ_false() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new ObjectValue(1))
    frame.push(new ObjectValue(1))
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIF_ACMPEQ(new IF_ACMPEQ(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testIFACMPNE_true() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new ObjectValue(1))
    frame.push(new ObjectValue(1))
    interpreter.setNext(new SPECIAL(1)) 
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIF_ACMPNE(new IF_ACMPNE(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFACMPNE_false() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    frame.push(v)
    frame.push(v)
    // If evaluate to false, the next should not be SPECIAL
    interpreter.visitIF_ACMPEQ(new IF_ACMPEQ(0, 0, 1))
    // For the true branch, see SnoopInstructionMethodAdapter
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testGetValueInt_pass() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.visitGETVALUE_int(new GETVALUE_int(1))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testGetValueInt_fail() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(PlaceHolder.instance)
    interpreter.visitGETVALUE_int(new GETVALUE_int(1))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testGetValueLong_pass() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new LongValue(1L))
    interpreter.visitGETVALUE_long(new GETVALUE_long(1L))
    assertEquals(new LongValue(1L), frame.peek2())
  }

  @Test
  void testGetValueLong_fail() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(PlaceHolder.instance)
    interpreter.visitGETVALUE_long(new GETVALUE_long(1L))
    assertEquals(new LongValue(1L), frame.peek2())
  }


  @Test
  void testGetValueShort_pass() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.visitGETVALUE_short(new GETVALUE_short((short)1))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testGetValueShort_fail() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(PlaceHolder.instance)
    interpreter.visitGETVALUE_short(new GETVALUE_short((short)1))
    assertEquals(new IntValue(1), frame.peek())
  }  

  @Test
  void testGetValueChar_pass() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.visitGETVALUE_char(new GETVALUE_char((char)1))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testGetValueChar_fail() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(PlaceHolder.instance)
    interpreter.visitGETVALUE_char(new GETVALUE_char((char)1))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testGetValueBoolean_pass() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.visitGETVALUE_boolean(new GETVALUE_boolean(true))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testGetValueBoolean_fail() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.visitGETVALUE_boolean(new GETVALUE_boolean(false))
    assertEquals(new IntValue(0), frame.peek())
  }

  @Test
  void testGetValueByte_fail() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(PlaceHolder.instance)
    interpreter.visitGETVALUE_byte(new GETVALUE_byte((byte)1))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testGetValueByte_pass() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.visitGETVALUE_byte(new GETVALUE_byte((byte)1))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testIFNULL_pass() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1, 0)
    frame.push(v)
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIFNULL( new IFNULL(0, 0, 1))
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFNULL_fail() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1, 0)
    v.setAddress(10)
    frame.push(v)
    interpreter.visitIFNULL( new IFNULL(0, 0, 1))
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testIFNONNULL_pass() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1, 0)
    v.setAddress(10)
    frame.push(v)
    interpreter.setNext(new SPECIAL(1)) 
    interpreter.visitIFNONNULL( new IFNONNULL(0, 0, 1))
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
    verify(coverage).visitBranch(0, true)
  }

  @Test
  void testIFNONNULL_fail() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1, 0)
    frame.push(v)
    interpreter.visitIFNONNULL( new IFNONNULL(0, 0, 1))
    assertEquals(1, history.getHistory().size())
    def branch = (BranchElement) history.getHistory().get(0)
    assertFalse(branch.branch)
    verify(coverage).visitBranch(0, false)
  }

  @Test
  void testCASTORE() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    frame.push(v)
    frame.push(new IntValue(0)) // index
    frame.push(new IntValue(1)) // value
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitCASTORE(new CASTORE(0, 0))
    assertEquals(new IntValue(1), v.getFields()[0])
  }

  @Test
  void testCALOAD() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    v.setField(0, new IntValue(2))
    frame.push(v)
    frame.push(new IntValue(0)) // index
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitCALOAD(new CALOAD(0, 0))
    assertEquals(new IntValue(2), frame.peek())
  }

  @Test
  void testBASTORE() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    frame.push(v)
    frame.push(new IntValue(0)) // index
    frame.push(new IntValue(1)) // value
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitBASTORE(new BASTORE(0, 0))
    assertEquals(new IntValue(1), v.getFields()[0])
  }

  @Test
  void testBALOAD() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    v.setField(0, new IntValue(2))
    frame.push(v)
    frame.push(new IntValue(0)) // index
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitBALOAD(new BALOAD(0, 0))
    assertEquals(new IntValue(2), frame.peek())
  }

  @Test
  void testCALOAD_Symbol() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    v.setField(0, new IntValue(2))
    frame.push(v)

    SymbolicInt x = new SymbolicInt(1)
    IntValue idx = new IntValue(0, x)
    frame.push(idx) // index
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitCALOAD(new CALOAD(0, 0))

    IntValue result = (IntValue) frame.peek()
    assertEquals(2, result.concrete)
    println(result.getSymbolic()) // TODO: how to test this?

    assertEquals(1, history.getHistory().size())
    BranchElement branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
  }

  @Test
  void testLALOAD() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    v.setField(0, new LongValue(2))
    frame.push(v)
    frame.push(new IntValue(0)) // index
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitLALOAD(new LALOAD(0, 0))
    assertEquals(new LongValue(2), frame.peek2())
  }

  @Test
  void testLASTORE() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    frame.push(v)
    frame.push(new IntValue(0)) // index
    frame.push2(new LongValue(2)) // value
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitLASTORE(new LASTORE(0, 0))
    assertEquals(new LongValue(2), v.getFields()[0])
  }

  @Test
  void testLALOAD_Symbol() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    v.setField(0, new LongValue(2))
    frame.push(v)

    SymbolicInt x = new SymbolicInt(1)
    IntValue idx = new IntValue(0, x)
    frame.push(idx) // index
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitLALOAD(new LALOAD(0, 0))

    LongValue result = (LongValue) frame.peek2()
    assertEquals(2L, result.concrete)
    println(result.getSymbolic()) // TODO: how to test this?

    assertEquals(1, history.getHistory().size())
    BranchElement branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
  }
  @Test
  void testIASTORE() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    frame.push(v)
    frame.push(new IntValue(0)) // index
    frame.push(new IntValue(1)) // value
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitIASTORE(new IASTORE(0, 0))
    assertEquals(new IntValue(1), v.getFields()[0])
  }

  @Test
  void testIALOAD() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    v.setField(0, new IntValue(2))
    frame.push(v)
    frame.push(new IntValue(0)) // index
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitIALOAD(new IALOAD(0, 0))
    assertEquals(new IntValue(2), frame.peek())
  }

  @Test
  void testIALOAD_Symbol() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    v.setField(0, new IntValue(2))
    frame.push(v)

    SymbolicInt x = new SymbolicInt(1)
    IntValue idx = new IntValue(0, x)
    frame.push(idx) // index
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitIALOAD(new IALOAD(0, 0))

    IntValue result = (IntValue) frame.peek()
    assertEquals(2, result.concrete)
    println(result.getSymbolic()) // TODO: how to test this?

    assertEquals(1, history.getHistory().size())
    BranchElement branch = (BranchElement) history.getHistory().get(0)
    assertTrue(branch.branch)
  }

  @Test
  void testAASTORE() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    frame.push(v)
    frame.push(new IntValue(0)) // index
    frame.push(new IntValue(1)) // value
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitAASTORE(new AASTORE(0, 0))
    assertEquals(new IntValue(1), v.getFields()[0])
  }

  @Test
  void testAALOAD() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    ObjectValue val = new ObjectValue(0, 1)
    v.setField(0, val)
    frame.push(v)
    frame.push(new IntValue(0)) // index
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitAALOAD(new AALOAD(0, 0))
    assertEquals(val, frame.peek())
  }

  @Test
  void testAALOAD_Symbol() {
    Frame frame = interpreter.getCurrentFrame()
    def v = new ObjectValue(1)
    v.setAddress(1) // Arbitrary address
    ObjectValue val = new ObjectValue(0, 1)
    v.setField(0, val)
    frame.push(v)

    SymbolicInt x = new SymbolicInt(1)
    IntValue idx = new IntValue(0, x)
    frame.push(idx) // index
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitAALOAD(new AALOAD(0, 0))

    ObjectValue result = (ObjectValue) frame.peek()
    assertEquals(val.address, result.address)
    println(result.getSymbolic()) // TODO: how to test this?
  }  

  @Test
  void testLDC_int() {
    IntValue x = new IntValue(1)
    interpreter.visitLDC_int(new LDC_int(0, 0, 1))
    assertEquals(x, interpreter.getCurrentFrame().peek())
  }

  @Test
  void testLDC_long() {
    LongValue x = new LongValue(1L)
    interpreter.visitLDC_long(new LDC_long(0, 0, 1L))
    assertEquals(x, interpreter.getCurrentFrame().peek2())
  }

  @Test
  void testILOAD() {
    IntValue e = new IntValue(1)
    Frame frame = interpreter.getCurrentFrame()
    frame.setLocal(1, e)
    interpreter.visitILOAD(new ILOAD(0, 0, 1))
    assertEquals(e, frame.peek())
  }

  @Test
  void testISTORE() {
    IntValue e = new IntValue(1)
    Frame frame = interpreter.getCurrentFrame()
    frame.push(e)
    interpreter.visitISTORE(new ISTORE(0, 0, 1))
    assertEquals(e, frame.getLocal(1))
  }

  @Test
  void testACONST_NULL() {
    interpreter.visitACONST_NULL(new ACONST_NULL(0, 0))
    Frame frame = interpreter.getCurrentFrame()
    assertEquals(ObjectValue.NULL, frame.peek())
  }

  @Test
  void testALOAD() {
    ObjectValue obj = new ObjectValue(1)
    obj.setAddress(10)
    Frame frame = interpreter.getCurrentFrame()
    frame.setLocal(0, obj)

    interpreter.visitALOAD(new ALOAD(0, 0, 0))
    assertEquals(obj, frame.peek())
  }

  @Test
  void testANEWARRAY() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.visitANEWARRAY(new ANEWARRAY(0, 0, "MyClass"))

    def obj = frame.peek()
    assertTrue(obj instanceof ObjectValue)
    ObjectValue o = (ObjectValue)obj
    assertEquals(1, o.fields.length)
  }

  @Test
  void testARETURN() {
    Frame frame = interpreter.getCurrentFrame()
    ObjectValue obj = new ObjectValue(1)
    frame.push(obj)
    interpreter.visitARETURN(new ARETURN(0, 0))
    assertEquals(obj, frame.ret)
  }

  @Test
  void testARRAYLENGTH() {
    Frame frame = interpreter.getCurrentFrame()
    ObjectValue obj = new ObjectValue(2)
    frame.push(obj)
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitARRAYLENGTH(new ARRAYLENGTH(0, 0))
    assertEquals(new IntValue(2), frame.peek())
  }

  @Test
  void testD2F() {
    Frame frame = interpreter.getCurrentFrame()
    Value obj = new DoubleValue(2.0D)
    frame.push2(obj)
    interpreter.visitD2F(new D2F(0, 0))
    assertEquals(new FloatValue(2.0F), frame.peek())
  }

  @Test
  void testD2I() {
    Frame frame = interpreter.getCurrentFrame()
    Value obj = new DoubleValue(2.0D)
    frame.push2(obj)
    interpreter.visitD2I(new D2I(0, 0))
    assertEquals(new IntValue(2), frame.peek())
  }

  @Test
  void testD2L() {
    Frame frame = interpreter.getCurrentFrame()
    Value obj = new DoubleValue(2.0D)
    frame.push2(obj)
    interpreter.visitD2L(new D2L(0, 0))
    assertEquals(new LongValue(2), frame.peek2())
  }

  @Test
  void testASTORE() {
    Frame frame = interpreter.getCurrentFrame()
    ObjectValue obj = new ObjectValue(1)
    frame.push(obj)
    interpreter.visitASTORE(new ASTORE(0, 0, 0))
    assertEquals(obj, frame.getLocal(0))
  }

  @Test
  void testDADD() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(1.0D))
    frame.push2(new DoubleValue(1.0D))
    interpreter.visitDADD(new DADD(0, 0))
    assertEquals(new DoubleValue(1.0D + 1.0D), frame.peek2())
  }

  @Test
  void testDALOAD() {
    ObjectValue obj = new ObjectValue(1)
    obj.setAddress(10)
    obj.setField(0, new DoubleValue(1.0D))
    Frame frame = interpreter.getCurrentFrame()
    frame.push(obj)
    frame.push(new IntValue(0))
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitDALOAD(new DALOAD(0, 0))
    assertEquals(new DoubleValue(1.0D), frame.peek2())
  }

  @Test
  void testDASTORE() {
    ObjectValue obj = new ObjectValue(1)
    obj.setAddress(10)
    obj.setField(0, new DoubleValue(0.0D))
    Frame frame = interpreter.getCurrentFrame()
    frame.push(obj)
    frame.push(new IntValue(0))
    frame.push2(new DoubleValue(2.0D))
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitDASTORE(new DASTORE(0, 0))
    assertEquals(new DoubleValue(2.0D), obj.getField(0))
  }

  @Test
  void testDCMPG() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(2.0D))
    frame.push2(new DoubleValue(3.0D))
    interpreter.visitDCMPG(new DCMPG(0, 0))
    assertEquals(new IntValue(-1), frame.peek())
  }

  @Test
  void testDCMPL() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(2.0D))
    frame.push2(new DoubleValue(3.0D))
    interpreter.visitDCMPL(new DCMPL(0, 0))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testDCONST_0() {
    Frame frame = interpreter.getCurrentFrame()
    interpreter.visitDCONST_0(new DCONST_0(0, 0))
    assertEquals(new DoubleValue(0.0D), frame.peek2())
  }

  @Test
  void testDCONST_1() {
    Frame frame = interpreter.getCurrentFrame()
    interpreter.visitDCONST_1(new DCONST_1(0, 0))
    assertEquals(new DoubleValue(1.0D), frame.peek2())
  }

  @Test
  void testDDIV() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(2.0D))
    frame.push2(new DoubleValue(1.0D))
    interpreter.visitDDIV(new DDIV(0, 0))
    assertEquals(new DoubleValue(2.0D), frame.peek2())
  }

  @Test
  void testDLOAD() {
    Frame frame = interpreter.getCurrentFrame()
    frame.setLocal2(0 , new DoubleValue(2.0D))
    interpreter.visitDLOAD(new DLOAD(0, 0, 0))
    assertEquals(new DoubleValue(2.0D), frame.peek2())
  }

  @Test
  void testDSTORE() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(2.0D))
    interpreter.visitDSTORE(new DSTORE(0, 0, 0))
    assertEquals(new DoubleValue(2.0D), frame.getLocal2(0))
  }

  @Test
  void testDMUL() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(2.0D))
    frame.push2(new DoubleValue(1.0D))
    interpreter.visitDMUL(new DMUL(0, 0))
    assertEquals(new DoubleValue(2.0D), frame.peek2())
  }

  @Test
  void testDNEG() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(2.0D))
    interpreter.visitDNEG(new DNEG(0, 0))
    assertEquals(new DoubleValue(-2.0D), frame.peek2())
  }

  @Test
  void testDREM() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(1.0D))
    frame.push2(new DoubleValue(2.0D))
    interpreter.visitDREM(new DREM(0, 0))
    assertEquals(new DoubleValue(1.0D), frame.peek2())
  }

  @Test
  void testDRET() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(2.0D))
    interpreter.visitDRETURN(new DRETURN(0, 0))
    assertEquals(new DoubleValue(2.0D), frame.ret)
  }

  @Test
  void testDSUB() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(2.0D))
    frame.push2(new DoubleValue(1.0D))
    interpreter.visitDSUB(new DSUB(0, 0))
    assertEquals(new DoubleValue(1.0D), frame.peek2())
  }

  @Test
  void testDUP() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    interpreter.visitDUP(new DUP(0, 0))
    assertEquals(new IntValue(1), frame.peek())
    frame.pop()
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testDUP2() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(1.0D))
    interpreter.visitDUP2(new DUP2(0, 0))
    assertEquals(new DoubleValue(1.0D), frame.peek2())
    frame.pop2()
    assertEquals(new DoubleValue(1.0D), frame.peek2())
  }

  @Test
  void testDUP2_X1() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(1))
    frame.push2(new DoubleValue(1.0D))
    interpreter.visitDUP2_X1(new DUP2_X1(0, 0))
    assertEquals(new DoubleValue(1.0D), frame.pop2())
    assertEquals(new IntValue(1), frame.pop())
    assertEquals(new DoubleValue(1.0D), frame.peek2())
  }

  @Test
  void testDUP2_X2() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(2.0D))
    frame.push2(new DoubleValue(1.0D))
    interpreter.visitDUP2_X2(new DUP2_X2(0, 0))
    assertEquals(new DoubleValue(1.0D), frame.pop2())
    assertEquals(new DoubleValue(2.0D), frame.pop2())
    assertEquals(new DoubleValue(1.0D), frame.peek2())
  }

  @Test
  void testDUP_X1() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new IntValue(2))
    frame.push(new IntValue(1))
    interpreter.visitDUP_X1(new DUP_X1(0, 0))
    assertEquals(new IntValue(1), frame.pop())
    assertEquals(new IntValue(2), frame.pop())
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testDUP_X2() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push2(new DoubleValue(1.0D))
    frame.push(new IntValue(1))
    interpreter.visitDUP_X2(new DUP_X2(0, 0))
    assertEquals(new IntValue(1), frame.pop())
    assertEquals(new DoubleValue(1.0D), frame.pop2())
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testF2D() {
    Frame frame = interpreter.getCurrentFrame()
    Value obj = new FloatValue(2.0F)
    frame.push(obj)
    interpreter.visitF2D(new F2D(0, 0))
    assertEquals(new DoubleValue(2.0D), frame.peek2())
  }

  @Test
  void testF2I() {
    Frame frame = interpreter.getCurrentFrame()
    Value obj = new FloatValue(2.0F)
    frame.push(obj)
    interpreter.visitF2I(new F2I(0, 0))
    assertEquals(new IntValue(2), frame.peek())
  }

  @Test
  void testF2L() {
    Frame frame = interpreter.getCurrentFrame()
    Value obj = new FloatValue(2.0F)
    frame.push(obj)
    interpreter.visitF2L(new F2L(0, 0))
    assertEquals(new LongValue(2), frame.peek2())
  }

  @Test
  void testFADD() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new FloatValue(1.0F))
    frame.push(new FloatValue(1.0F))
    interpreter.visitFADD(new FADD(0, 0))
    assertEquals(new FloatValue(2.0F), frame.peek())
  }

  @Test
  void testFALOAD() {
    ObjectValue obj = new ObjectValue(1)
    obj.setAddress(10)
    obj.setField(0, new FloatValue(1.0F))
    Frame frame = interpreter.getCurrentFrame()
    frame.push(obj)
    frame.push(new IntValue(0))
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitFALOAD(new FALOAD(0, 0))
    assertEquals(new FloatValue(1.0F), frame.peek())
  }

  @Test
  void testFASTORE() {
    ObjectValue obj = new ObjectValue(1)
    obj.setAddress(10)
    obj.setField(0, new FloatValue(0.0F))
    Frame frame = interpreter.getCurrentFrame()
    frame.push(obj)
    frame.push(new IntValue(0))
    frame.push(new FloatValue(2.0F))
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitFASTORE(new FASTORE(0, 0))
    assertEquals(new FloatValue(2.0F), obj.getField(0))
  }

  @Test
  void testFCMPG() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new FloatValue(2.0F))
    frame.push(new FloatValue(3.0F))
    interpreter.visitFCMPG(new FCMPG(0, 0))
    assertEquals(new IntValue(-1), frame.peek())
  }

  @Test
  void testFCMPL() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new FloatValue(2.0F))
    frame.push(new FloatValue(3.0F))
    interpreter.visitFCMPL(new FCMPL(0, 0))
    assertEquals(new IntValue(1), frame.peek())
  }

  @Test
  void testFCONST_0() {
    Frame frame = interpreter.getCurrentFrame()
    interpreter.visitFCONST_0(new FCONST_0(0, 0))
    assertEquals(new FloatValue(0.0F), frame.peek())
  }

  @Test
  void testFCONST_1() {
    Frame frame = interpreter.getCurrentFrame()
    interpreter.visitFCONST_1(new FCONST_1(0, 0))
    assertEquals(new FloatValue(1.0F), frame.peek())
  }

  @Test
  void testFCONST_2() {
    Frame frame = interpreter.getCurrentFrame()
    interpreter.visitFCONST_2(new FCONST_2(0, 0))
    assertEquals(new FloatValue(2.0F), frame.peek())
  }

  @Test
  void testFDIV() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new FloatValue(2.0F))
    frame.push(new FloatValue(1.0F))
    interpreter.visitFDIV(new FDIV(0, 0))
    assertEquals(new FloatValue(2.0F), frame.peek())
  }

  @Test
  void testFLOAD() {
    Frame frame = interpreter.getCurrentFrame()
    frame.setLocal(0 , new FloatValue(2.0F))
    interpreter.visitFLOAD(new FLOAD(0, 0, 0))
    assertEquals(new FloatValue(2.0F), frame.peek())
  }

  @Test
  void testFSTORE() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new FloatValue(2.0F))
    interpreter.visitFSTORE(new FSTORE(0, 0, 0))
    assertEquals(new FloatValue(2.0F), frame.getLocal(0))
  }

  @Test
  void testFMUL() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new FloatValue(2.0F))
    frame.push(new FloatValue(1.0F))
    interpreter.visitFMUL(new FMUL(0, 0))
    assertEquals(new FloatValue(2.0F), frame.peek())
  }

  @Test
  void testFNEG() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new FloatValue(2.0F))
    interpreter.visitFNEG(new FNEG(0, 0))
    assertEquals(new FloatValue(-2.0F), frame.peek())
  }

  @Test
  void testFREM() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new FloatValue(1.0F))
    frame.push(new FloatValue(2.0F))
    interpreter.visitFREM(new FREM(0, 0))
    assertEquals(new FloatValue(1.0F), frame.peek())
  }

  @Test
  void testFRET() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new FloatValue(2.0F))
    interpreter.visitFRETURN(new FRETURN(0, 0))
    assertEquals(new FloatValue(2.0F), frame.ret)
  }

  @Test
  void testFSUB() {
    Frame frame = interpreter.getCurrentFrame()
    frame.push(new FloatValue(2.0F))
    frame.push(new FloatValue(1.0F))
    interpreter.visitFSUB(new FSUB(0, 0))
    assertEquals(new FloatValue(1.0F), frame.peek())
  }

  @Test
  void testGETFIELD() {
    when(classDepot.getFieldIndex("MyClass", "myField")).thenReturn(0)
    when(classDepot.nFields("MyClass")).thenReturn(1)
    int classIdx = classNames.get("MyClass")
    ObjectInfo oi = classNames.get(classIdx)
    int fIdx = oi.getIdx("myField", false)
    ObjectValue obj = new ObjectValue(1)
    obj.setAddress(10)
    obj.setField(0, new FloatValue(1.0F))
    Frame frame = interpreter.getCurrentFrame()
    frame.push(obj)
    interpreter.setNext(new SPECIAL(0)) // exception handling
    interpreter.visitGETFIELD(new GETFIELD(0, 0, classIdx, fIdx, "F"))
    assertEquals(new FloatValue(1.0F), frame.peek())
  }
}