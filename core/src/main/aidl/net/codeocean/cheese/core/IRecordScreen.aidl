// IRecordScreen.aidl
package net.codeocean.cheese.core;

// Declare any non-default types here with import statements
import net.codeocean.cheese.core.IRecordScreenCallback;
interface IRecordScreen {
   void registerCallback(IRecordScreenCallback callback);
   void unregisterCallback();
}
