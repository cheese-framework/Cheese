// IRecordScreenCallback.aidl
package net.codeocean.cheese.core;

// Declare any non-default types here with import statements

interface IRecordScreenCallback {
    boolean requestPermission(int timeout);
    boolean checkPermission();
    Bitmap captureScreen(int timeout, int x, int y, int ex, int ey);
}