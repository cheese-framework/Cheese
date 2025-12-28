package net.codeocean.cheese.core.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.elvishew.xlog.XLog
import net.codeocean.cheese.core.CoreEnv
import net.codeocean.cheese.core.IPython
import net.codeocean.cheese.core.IPythonCallback
import net.codeocean.cheese.core.IRecordScreen
import net.codeocean.cheese.core.IRecordScreenCallback
import net.codeocean.cheese.core.MultiIBinder
import kotlin.concurrent.thread

open class AIDLService : Service() {

    override fun onCreate() {
        super.onCreate()
        //看清单加上这个和我们服务端一个进程
        XLog.e("运行进程启动成功")

        val intent = Intent(this, Class.forName("net.codeocean.cheese.core.activity.IPCActivity"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 关键修复
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 关键修复
        startActivity(intent)

    }

    companion object {
        var iRecordScreenCallback: IRecordScreenCallback? = null
        var iPythonCallback: IPythonCallback? = null
    }


    private val multiIBinder = object : MultiIBinder.Stub() {
        override fun getIRecordScreen(): IBinder {
            return object : IRecordScreen.Stub() {
                override fun registerCallback(callback: IRecordScreenCallback?) {


                }

                override fun unregisterCallback() {

                }

            }
        }


        override fun getIPython(): IBinder {
            return object : IPython.Stub() {
                override fun start() {

                }

                override fun exit() {

                }

                override fun registerCallback(callback: IPythonCallback?) {

                }

                override fun unregisterCallback() {

                }
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent): IBinder = multiIBinder
}