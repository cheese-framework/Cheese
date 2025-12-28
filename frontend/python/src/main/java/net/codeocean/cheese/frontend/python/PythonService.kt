package net.codeocean.cheese.frontend.python

import android.app.Service
import android.content.Intent

import android.os.IBinder
import com.elvishew.xlog.XLog
import com.hjq.window.EasyWindow
import net.codeocean.cheese.backend.impl.CanvasImpl.act
import net.codeocean.cheese.backend.impl.PathImpl
import net.codeocean.cheese.core.CoreEnv
import net.codeocean.cheese.core.CoreFactory

import net.codeocean.cheese.core.IPython
import net.codeocean.cheese.core.IPythonCallback

import net.codeocean.cheese.core.IRecordScreen
import net.codeocean.cheese.core.IRecordScreenCallback
import net.codeocean.cheese.core.Misc
import net.codeocean.cheese.core.MultiIBinder
import net.codeocean.cheese.core.aidl.AIDLService
import java.io.File
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class PythonService : AIDLService() {

    override fun onCreate() {
        super.onCreate()
    }


    private val multiIBinder = object : MultiIBinder.Stub()  {
        override fun getIRecordScreen(): IBinder {
            return object : IRecordScreen.Stub() {
                override fun registerCallback(callback: IRecordScreenCallback?) {
                    iRecordScreenCallback = callback

                    thread {
//                        println(">>>>>>>>>>" +  iRecordScreenCallback?.requestPermission(3000))
                        println("ct:"+CoreEnv.envContext.context)
                    }

                }

                override fun unregisterCallback() {
                    iRecordScreenCallback = null
                }

            }
        }


        override fun getIPython(): IBinder {
            return object : IPython.Stub() {
                override fun start() {
                    CoreEnv.runTime.isRemoteMode=true
                    val jvm = File(PathImpl.ASSETS_DIRECTORY,"pip")
                    if (jvm.exists()){
                       ResourcesManages().load(jvm.absolutePath,this@PythonService)
                    }
                    Python(CoreEnv.envContext.context).start()
                    sleep(2000)
                    exit()
                }

                override fun exit() {
                    try {
                        // 先执行必要的清理逻辑
                        stopSelf()
                    } finally {
                        // 强制杀死进程（兜底）
                        android.os.Process.killProcess(android.os.Process.myPid())
                        System.exit(0) // 确保退出
                    }
//                    XLog.e("牛逼1112222222211:"+ Misc.getProcessName())
//                    android.os.Process.killProcess(android.os.Process.myPid())
//                    System.exit(0)
                }

                override fun registerCallback(callback: IPythonCallback?) {
                    iPythonCallback=callback
                }

                override fun unregisterCallback() {
                    iPythonCallback=null
                }
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent): IBinder = multiIBinder


}