package janala.instrument;

import janala.config.Config;
import janala.utils.MyLogger;

import java.io.*;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Coverage implements Serializable {
  private HashMap<String, Integer> classNameToCid;
  private TreeMap<Integer, String> cidmidToName;
  private int nBranches;
  private int nCovered;
  private TreeMap<Integer, Integer> covered;
  private TreeMap<Integer, Integer> tmpCovered;
  private boolean isNewClass;

  public static Coverage instance = null;
  
  private static final Logger logger = MyLogger.getLogger(Coverage.class.getName());
  
  private String lastMethod;
  private String lastClassName;

  private Coverage() {
    classNameToCid = new HashMap<String, Integer>();
    nBranches = 0;
    nCovered = 0;
    covered = new TreeMap<Integer, Integer>();
    tmpCovered = new TreeMap<Integer, Integer>();
    cidmidToName = new TreeMap<Integer, String>();
  }

  public static Coverage get() {
    if (instance == null) {
      instance = new Coverage();
    }
    return instance;
  }

  public static void read() {
    if (instance == null) {
      ObjectInputStream inputStream = null;

      try {
        inputStream = new ObjectInputStream(new FileInputStream(Config.instance.coverage));
        Object tmp = inputStream.readObject();
        if (tmp instanceof Coverage) {
          instance = (Coverage) tmp;
        } else {
          instance = new Coverage();
        }
      } catch (Exception e) {
        instance = new Coverage();
      } finally {
        try {
          if (inputStream != null) {
            inputStream.close();
          }
        } catch (IOException ex) {
          logger.log(Level.WARNING, "", ex);
        }
      }
    }
  }

  public void write() {
    ObjectOutputStream outputStream;
    try {
      String outputFile = Config.instance.coverage;
      if (outputFile == null) return;
      
      outputStream = new ObjectOutputStream(new FileOutputStream(Config.instance.coverage));
      instance.tmpCovered.clear();
      outputStream.writeObject(instance);
      outputStream.close();

    } catch (IOException e) {
      logger.log(Level.SEVERE, "", e);
      System.exit(1);
    }
  }

  public int getCid(String cname) {
    int ret = -1; // invalid
    lastClassName = cname;
    if (classNameToCid.containsKey(cname)) {
      isNewClass = false;
      return classNameToCid.get(cname);
    } else {
      classNameToCid.put(cname, ret = classNameToCid.size());
      if (cname.equals("catg/CATG")) {
        isNewClass = false;
      } else {
        isNewClass = true;
      }
      return ret;
    }
  }

  public void setCidmidToName(int mid) {
    int cid = classNameToCid.get(lastClassName);
    int cidmid = GlobalStateForInstrumentation.getCidMid(cid, mid);
    cidmidToName.put(cidmid, lastClassName + "." + lastMethod);
  }

  public void addBranchCount(int iid) {
    if (isNewClass) {
      nBranches += 2;
      covered.put(iid, 0);
    }
  }

  public void visitBranch(int iid, boolean side) {
    if (!tmpCovered.containsKey(iid)) {
      tmpCovered.put(iid, 0);
    }
    tmpCovered.put(iid, tmpCovered.get(iid) | (side ? 1 : 2));
  }

  public void commitBranches() {
    for (int key : tmpCovered.keySet()) {
      int value = tmpCovered.get(key);
      if (covered.containsKey(key)) {
        int oldValue = covered.get(key);
        covered.put(key, oldValue | value);
        if ((value & 2) > (oldValue & 2)) {
          nCovered++;
        }
        if ((value & 1) > (oldValue & 1)) {
          nCovered++;
        }
      }
    }
    printCoverage();
  }

  public void printCoverage() {
    TreeMap<Integer, Integer> methodToTotalBranches = new TreeMap<Integer, Integer>();
    TreeMap<Integer, Integer> methodToCoveredBranches = new TreeMap<Integer, Integer>();
    TreeMap<Integer, Boolean> mcovered = new TreeMap<Integer, Boolean>();
    for (int key : covered.keySet()) {
      int cidmid = GlobalStateForInstrumentation.extractCidMid(key);
      if (!methodToTotalBranches.containsKey(cidmid)) {
        methodToTotalBranches.put(cidmid, 0);
        methodToCoveredBranches.put(cidmid, 0);
        mcovered.put(cidmid, false);
      }
      methodToTotalBranches.put(cidmid, methodToTotalBranches.get(cidmid) + 2);
      int value = covered.get(key);
      if (value > 0) {
        if ((value & 2) > 0)
          methodToCoveredBranches.put(cidmid, methodToCoveredBranches.get(cidmid) + 1);
        if ((value & 1) > 0)
          methodToCoveredBranches.put(cidmid, methodToCoveredBranches.get(cidmid) + 1);
        mcovered.put(cidmid, true);
      }
    }
    int mtotals = 0;
    int nM = 0;
    for (int key : methodToTotalBranches.keySet()) {
      if (mcovered.get(key)) {
        mtotals += methodToTotalBranches.get(key);
        nM++;
      }
    }
    System.out.println(
        "Branch coverage with respect to covered classes = "
            + (100.0 * nCovered / nBranches)
            + "%");
    System.out.println(
        "Branch coverage with respect to covered methods = " + (100.0 * nCovered / mtotals) + "%");
    System.out.println("Total branches in covered methods = " + mtotals);
  }

  public void setLastMethod(String lastMethod) {
    this.lastMethod = lastMethod;
  }

  public String getLastMethod() {
    return lastMethod;
  }
}
