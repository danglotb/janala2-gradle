import shutil
import os
import subprocess
import sys

from datetime import datetime

catg_tmp_dir = "catg_tmp"

def concolic(fullQualifiedName, classpath, iters=100):
    cmd = "java -Xmx4096M -Xms2048M " \
          "-Djanala.loggerClass=janala.logger.DirectConcolicExecution " \
          "-Djanala.conf=../lib/catg.conf " \
          "-javaagent:../lib/janala2-gradle-0.2.jar " \
          "-cp ../" + classpath + ":../lib/janala2-gradle-0.2.jar:../lib/asm-all-5.0.4.jar:../lib/automaton-1.11-8.jar " \
                               "-ea " + fullQualifiedName
    print cmd
    try:
        shutil.rmtree(catg_tmp_dir)
    except:
        pass
    os.mkdir(catg_tmp_dir)
    os.chdir(catg_tmp_dir)

    i = 1
    while i <= iters:
        try:
            try:
                with open("isRealInput", "r") as myfile:
                    data = myfile.read().replace('\n', '')
                print data
            except:
                data = "true"
            if data != "false":
                shutil.copy("inputs", "inputs{}".format(i))
            shutil.copy("inputs", "inputs.old")
        except:
            pass
        try:
            shutil.copy("history", "history.old")
        except:
            pass
        dt = datetime.now()

        print "[Input {} at ({}, {}, {}, {}, {})]".format(i, dt.day, dt.hour, dt.minute, dt.second, dt.microsecond)
        sys.stdout.flush()
        subprocess.call("pwd", shell=True)
        subprocess.call(cmd, shell=True)
        i = i + 1
        if os.path.isfile("history") or os.path.isfile("backtrackFlag"):
            pass
        else:
            return

if __name__ == '__main__':
    if len(sys.argv) < 3:
        print "usage: python concolic.py <classpathProject> <fullqualifiedName>"
    concolic(fullQualifiedName=sys.argv[2], classpath=sys.argv[1])