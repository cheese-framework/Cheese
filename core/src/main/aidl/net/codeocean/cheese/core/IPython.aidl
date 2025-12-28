// IPython.aidl
package net.codeocean.cheese.core;

// Declare any non-default types here with import statements
import net.codeocean.cheese.core.IPythonCallback;
interface IPython {
      void start();
      void exit();
      void registerCallback(IPythonCallback callback);
      void unregisterCallback();
}