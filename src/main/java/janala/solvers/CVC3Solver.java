package janala.solvers;

import janala.config.Config;
import janala.interpreters.*;
import janala.interpreters.StringValue;
import janala.utils.FileUtil;
import janala.utils.MyLogger;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CVC3Solver implements Solver {
  LinkedList<InputElement> inputs;
  ArrayList<Constraint> constraints;
  int pathConstraintIndex;
  private final static Logger logger = MyLogger.getLogger(CVC3Solver.class.getName());
  private final static Logger tester =
      MyLogger.getTestLogger(Config.mainClass + "." + Config.iteration);

  public void setInputs(LinkedList<InputElement> inputs) {
    this.inputs = inputs;
  }

  public void setPathConstraint(ArrayList<Constraint> pathConstraint) {
    this.constraints = pathConstraint;
  }

  public void setPathConstraintIndex(int pathConstraintIndex) {
    this.pathConstraintIndex = pathConstraintIndex;
  }

  public void visitSymbolicInt(SymbolicInt c) {}

  public void visitSymbolicIntCompare(SymbolicIntCompareConstraint c) {}

  public void visitSymbolicOr(SymbolicOrConstraint c) {}

  public void visitSymbolicAnd(SymbolicAndConstraint c) {}

  public void visitSymbolicNot(SymbolicNotConstraint c) {}

  public void visitSymbolicTrue(SymbolicTrueConstraint c) {}

  public void visitSymbolicFalse(SymbolicFalseConstraint c) {}

  public void visitSymbolicStringPredicate(SymbolicStringPredicate c) {}

  private void print(
      Constraint con,
      PrintStream out,
      LinkedHashSet<String> freeVars,
      CONSTRAINT_TYPE type,
      TreeMap<String, Long> soln) {
    if (con instanceof SymbolicInt) {
      SymbolicInt c = (SymbolicInt) con;
      boolean first2 = true;
      for (Map.Entry<Integer, Long> it : c.getLinear().entrySet() ) {
        int integer = it.getKey();
        freeVars.add("x" + integer);
        long l = it.getValue();
        if (first2) {
          first2 = false;
        } else {
          out.print(" + ");
        }
        out.print('x');
        out.print(integer);
        out.print("*(");
        out.print(l);
        out.print(')');
      }
      if (c.getConstant() != 0) {
        out.print("+(");
        out.print(c.getConstant());
        out.print(')');
      }
      if (c.getOp() == SymbolicInt.COMPARISON_OPS.EQ) {
        out.print(" = ");
      } else if (c.getOp() == SymbolicInt.COMPARISON_OPS.NE) {
        out.print(" /= ");
      } else if (c.getOp() == SymbolicInt.COMPARISON_OPS.LE) {
        out.print(" <= ");
      } else if (c.getOp() == SymbolicInt.COMPARISON_OPS.LT) {
        out.print(" < ");
      } else if (c.getOp() == SymbolicInt.COMPARISON_OPS.GE) {
        out.print(" >= ");
      } else if (c.getOp() == SymbolicInt.COMPARISON_OPS.GT) {
        out.print(" > ");
      }
      out.print("0");
    } else if (con instanceof SymbolicIntCompareConstraint) {
      SymbolicIntCompareConstraint c = (SymbolicIntCompareConstraint) con;
      out.print('(');
      if (c.left.isSym) {
        freeVars.add(c.left.getSym());
      } 
      out.print(c.left);
      out.print(')');
      out.print('-');
      out.print('(');
      if (c.right.isSym) {
        freeVars.add(c.right.getSym());
      }
      out.print(c.right);
      out.print(')');
      if (c.op == SymbolicIntCompareConstraint.COMPARISON_OPS.EQ) {
        out.print(" = ");
      } else if (c.op == SymbolicIntCompareConstraint.COMPARISON_OPS.NE) {
        out.print(" /= ");
      } else if (c.op == SymbolicIntCompareConstraint.COMPARISON_OPS.LE) {
        out.print(" <= ");
      } else if (c.op == SymbolicIntCompareConstraint.COMPARISON_OPS.LT) {
        out.print(" < ");
      } else if (c.op == SymbolicIntCompareConstraint.COMPARISON_OPS.GE) {
        out.print(" >= ");
      } else if (c.op == SymbolicIntCompareConstraint.COMPARISON_OPS.GT) {
        out.print(" > ");
      }
      out.print("0");
    } else if (con instanceof SymbolicOrConstraint) {
      SymbolicOrConstraint or = (SymbolicOrConstraint) con;

      boolean first2 = true;
      for (Constraint c : or.constraints) {
        if (first2) {
          first2 = false;
        } else {
          out.print(" OR ");
        }
        out.print("(");
        print(c, out, freeVars, type, soln);
        out.print(")");
      }
      if (or.constraints.isEmpty()) {
        out.print(" TRUE ");
      }
    } else if (con instanceof SymbolicAndConstraint) {
      SymbolicAndConstraint and = (SymbolicAndConstraint) con;

      boolean first2 = true;
      for (Constraint c : and.constraints) {
        if (first2) {
          first2 = false;
        } else {
          out.print(" AND ");
        }
        out.print("(");
        print(c, out, freeVars, type, soln);
        out.print(")");
      }
      if (and.constraints.isEmpty()) {
        out.print(" FALSE ");
      }
    } else if (con instanceof SymbolicNotConstraint) {
      SymbolicNotConstraint not = (SymbolicNotConstraint) con;
      out.print(" NOT ");
      out.print("(");
      print(not.constraint, out, freeVars, type, soln);
      out.print(")");
    } else if (con instanceof SymbolicTrueConstraint) {
      out.print(" TRUE ");
    } else if (con instanceof SymbolicFalseConstraint) {
      out.print(" FALSE ");
    } else if (con instanceof SymbolicStringPredicate) {
      SymbolicStringPredicate str = (SymbolicStringPredicate) con;
      Constraint intConstraint = str.getFormula(freeVars, type, soln);
      print(intConstraint, out, freeVars, type, soln);
    } else {
      throw new RuntimeException("Unimplemented constraint type " + con);
    }
  }

  static public void concatFile(
      LinkedHashSet<String> freeVars, String from, String to, boolean cvc4)
      throws java.io.IOException {
    PrintStream pw = new PrintStream(new BufferedOutputStream(new FileOutputStream(to)));

    //        System.out.println("Concat:");
    if (Config.instance.printFormulaAndSolutions) {
      System.out.println("-----------Formula-------------");
    }
    if (cvc4) {
      pw.println("OPTION \"produce-models\";");
      if (Config.instance.printFormulaAndSolutions) {
        System.out.println("OPTION \"produce-models\";");
      }
    }
    for (String var : freeVars) {
      pw.print(var);
      pw.println(" :INT;");
      if (Config.instance.printFormulaAndSolutions) {
        System.out.print(var);
        System.out.println(" :INT;");
      }
    }

    BufferedReader br = new BufferedReader(new FileReader(from));
    String line = br.readLine();
    while (line != null) {
      pw.println(line);
      if (Config.instance.printFormulaAndSolutions) {
        System.out.println(line);
      }
      //            System.out.println(line);
      line = br.readLine();
    }
    br.close();
    pw.close();
  }

  public enum RESULT_TYPE {
    TRUE,
    FALSE,
    UNKNOWN
  };

  private boolean quickUnsatCheck(CONSTRAINT_TYPE type) {
    if (type == CONSTRAINT_TYPE.INT) {
      Constraint last = constraints.get(pathConstraintIndex);
      if (last instanceof SymbolicStringPredicate) {
        for (int i = 0; i < pathConstraintIndex; i++) {
          Constraint tmp = constraints.get(i);
          if (tmp.equals(last)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private RESULT_TYPE writeFormula(String extra, CONSTRAINT_TYPE type, TreeMap<String, Long> soln) {
    try {
      boolean allTrue = true;
      Constraint tmp;
      PrintStream out =
          new PrintStream(
              new BufferedOutputStream(new FileOutputStream(Config.instance.formulaFile + ".tmp")));
      if (quickUnsatCheck(type)) {
        return RESULT_TYPE.FALSE;
      }

      LinkedHashSet<String> freeVars = new LinkedHashSet<String>();
      for (int i = 0; i < pathConstraintIndex; i++) {
        out.print("ASSERT ");
        //System.out.println("Constraint "+i+":" + constraints.get(i));
        tmp = constraints.get(i).substitute(soln);
        if (tmp != SymbolicTrueConstraint.instance) {
          allTrue = false;
        }
        print(tmp, out, freeVars, type, soln);
        out.println(";");
      }
      if (extra != null) {
        out.print("ASSERT ");
        out.print(extra);
        out.println(";");
      }

      out.print("CHECKSAT ");

      //System.out.println("Constraint "+pathConstraintIndex+": !" + constraints.get(pathConstraintIndex));
      tmp = constraints.get(pathConstraintIndex).not().substitute(soln);
      if (tmp != SymbolicTrueConstraint.instance) {
        allTrue = false;
      }
      print(tmp, out, freeVars, type, soln);
      out.println(";");
      out.println("COUNTERMODEL;");
      out.close();

      concatFile(
          freeVars, Config.instance.formulaFile + ".tmp", Config.instance.formulaFile, false);
      return allTrue ? RESULT_TYPE.TRUE : RESULT_TYPE.UNKNOWN;
    } catch (IOException ioe) {
      ioe.printStackTrace();
      logger.log(Level.SEVERE, "{0}", ioe);
      Runtime.getRuntime().halt(1);
      return RESULT_TYPE.UNKNOWN;
    }
  }

  private void writeInputs(TreeMap<String, Long> soln) {
    try {
      FileUtil.moveFile(Config.instance.inputs, Config.instance.inputs + ".bak");
      PrintStream out =
          new PrintStream(new BufferedOutputStream(new FileOutputStream(Config.instance.inputs)));

      for (InputElement ielem : inputs) {
        Integer sym = ielem.symbol;
        Value val = ielem.value;
        if (sym.intValue() == Config.instance.scopeBeginSymbol) {
          out.println(Config.instance.scopeBeginMarker);
        } else if (sym.intValue() == Config.instance.scopeEndSymbol) {
          out.println(Config.instance.scopeEndMarker);
        } else {
          //System.out.println("sym "+sym);
          Long l = soln.get("x" + sym);
          if (l != null) {
            out.println(l);
            //System.out.println("l = " + l);
          } else {
            if (val instanceof StringValue) {
              StringValue sval = (StringValue) val;
              String old = sval.getConcrete();
              IntValue tmp = sval.getSymbolic().getField("length");
              int len = (int) (long) tmp.substituteInLinear(soln);
              StringBuilder ret = new StringBuilder();
              for (int i = 0; i < len; i++) {
                Long v = soln.get("x" + sym + "__" + i);

                char c;
                if (v != null) {
                  c = (char) (long) v;
                  //System.out.println("--1");
                } else if (i < old.length()) {
                  //System.out.println("--2"+old+" "+old.length());
                  c = old.charAt(i);
                } else {
                  //System.out.println("--3");
                  c = 'a';
                }
                ret.append(c);
                //System.out.println("~~~~~~~~~~~~~~~\"" + ret + "\"");
              }
              out.println(ret);
            } else {
              out.println(val.getConcrete());
            }
          }
        }
      }

      out.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
      logger.log(Level.SEVERE, "{0}", ioe);
      Runtime.getRuntime().halt(1);
    }
  }

  private String processInputs(BufferedReader br, TreeMap<String, Long> soln) {
    String line = null;
    String negatedSolution = null;

    try {
      if (Config.instance.printFormulaAndSolutions) {
        System.out.println("-----------Solution-------------");
      }

      line = br.readLine();
      if (Config.instance.printFormulaAndSolutions) {
        System.out.println(line);
      }
      if (!line.startsWith("Satisfiable")) {
        if (!line.contains("Unsat")) {
          logger.log(Level.SEVERE, line);
          logger.log(
              Level.SEVERE,
              "Call to CVC3 failed (concolic.cvc3 = " + Config.instance.cvc3Command + ")");
          Runtime.getRuntime().halt(1);
        }
        logger.log(Level.INFO, "-- Infeasible");
        while ((line = br.readLine()) != null) {
          if (Config.instance.printFormulaAndSolutions) {
            System.out.println(line);
          }
        }
        br.close();
        return null;
      } else {
        while ((line = br.readLine()) != null) {
          if (Config.instance.printFormulaAndSolutions) {
            System.out.println(line);
          }

          if (line.startsWith("ASSERT")) {
            if (negatedSolution != null) {
              negatedSolution += " AND " + line.substring(line.indexOf("("), line.indexOf(")") + 1);
            } else {
              negatedSolution = line.substring(line.indexOf("("), line.indexOf(")") + 1);
            }

            String[] tokens = line.split(" ");
            //                        int xi = Integer.parseInt(tokens[1].substring(2));
            String tmp = tokens[3].trim();
            long val = Long.parseLong(tmp.substring(0, tmp.indexOf(")")));
            soln.put(tokens[1].substring(1), val);
          }
        }
        br.close();

        negatedSolution = "(NOT (" + negatedSolution + "))";
        return negatedSolution;
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
      logger.log(Level.SEVERE, "{0}", ioe);
      Runtime.getRuntime().halt(1);
      return null;
    }
  }

  public boolean solve() {
    int count = 0, MAX_COUNT = 100;
    String extra = null, negatedSolution, negatedSolution2;
    //console.log("Doing search at "+i+ " with tail "+(tail+1));
    while (count < MAX_COUNT) {
      TreeMap<String, Long> soln = new TreeMap<String, Long>();
      //System.out.println("Solving int constraint: "+count);
      negatedSolution = solve(extra, CONSTRAINT_TYPE.INT, soln);
      if (negatedSolution != null) {
        //System.out.println("Solving string constraint: "+count);
        negatedSolution2 = solve(null, CONSTRAINT_TYPE.STR, soln);
        if (negatedSolution2 != null) {
          writeInputs(soln);
          tester.log(Level.INFO, "Feasible = true at " + pathConstraintIndex);
          return true;
        } else {
          if (extra != null) {
            extra = extra + " AND " + negatedSolution;
          } else {
            extra = negatedSolution;
          }
        }
      } else {
        tester.log(Level.INFO, "Feasible = false at " + pathConstraintIndex);
        return false;
      }
      count++;
    }
    tester.log(Level.INFO, "Feasible = false at " + pathConstraintIndex);
    return false;
  }

  public enum CONSTRAINT_TYPE {
    INT,
    STR
  };

  public String solve(String extra, CONSTRAINT_TYPE type, TreeMap<String, Long> soln) {
    try {
      RESULT_TYPE res;
      String negatedSolution;
      if ((res = writeFormula(extra, type, soln)) == RESULT_TYPE.TRUE) {
        return "";
      } else if (res == RESULT_TYPE.FALSE) {
        return null;
      }

      ProcessBuilder builder =
          new ProcessBuilder(
              new String[] {Config.instance.cvc3Command, Config.instance.formulaFile});
      builder.redirectErrorStream(true);
      Process process = builder.start();

      (new StreamGobbler(process.getErrorStream(), "ERROR")).start();

      InputStream is = process.getInputStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);

      negatedSolution = processInputs(br, soln);
      process.waitFor();
      return negatedSolution;

    } catch (IOException ioe) {
      ioe.printStackTrace();
      logger.log(Level.SEVERE, "{0}", ioe);
      Runtime.getRuntime().halt(1);
      return null;
    } catch (InterruptedException ie) {
      ie.printStackTrace();
      logger.log(Level.SEVERE, "{0}", ie);
      Runtime.getRuntime().halt(1);
      return null;
    }
  }
}
