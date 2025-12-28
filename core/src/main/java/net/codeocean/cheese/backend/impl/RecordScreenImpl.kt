package net.codeocean.cheese.backend.impl

import android.graphics.Bitmap
import com.elvishew.xlog.XLog
import net.codeocean.cheese.core.Misc.isMainProcess
import net.codeocean.cheese.core.aidl.AIDLService.Companion.iRecordScreenCallback
import net.codeocean.cheese.core.api.RecordScreen
import net.codeocean.cheese.core.utils.RecordScreenUtils

object RecordScreenImpl: RecordScreen {
    override fun requestPermission(timeout: Int): Boolean {
//        if (!isMainProcess()){
//            return iRecordScreenCallback?.requestPermission(timeout) ?: false
//        }
       return RecordScreenUtils.requestPermission(timeout)
    }

    override fun checkPermission(): Boolean {
//        if (!isMainProcess()){
//            return iRecordScreenCallback?.checkPermission() ?: false
//        }
        return RecordScreenUtils.checkPermission()
    }

    override fun captureScreen(timeout: Int, x: Int, y: Int, ex: Int, ey: Int): Bitmap? {
//        if (!isMainProcess()){
//            return iRecordScreenCallback?.captureScreen(timeout,x,y,ex,ey) ?: null
//        }
        return RecordScreenUtils.captureScreen(timeout,x,y,ex,ey)
    }
}