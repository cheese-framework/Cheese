package net.codeocean.cheese.frontend.python

import android.os.DeadObjectException
import android.os.Handler
import android.os.Looper
import android.os.RemoteException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.elvishew.xlog.XLog
import net.codeocean.cheese.core.CoreEnv
import net.codeocean.cheese.core.Script
import net.codeocean.cheese.core.aidl.AIDLClient
import java.io.File

class PythonIPC(private val androidContext: android.content.Context):Script {

    override fun run(workingDir: File) {
        AIDLClient.Companion.iPython?.start()
//        Handler(Looper.getMainLooper()).postDelayed({
//            try {
//
//                // 5. 启动新进程
//                AIDLClient.Companion.iPython?.start()
//            } catch (e: Exception) {
//                XLog.e("Restart failed: ${e.stackTraceToString()}")
//            }
//        }, 500)
//        AIDLClient.Companion.iPython?.start()
    }

    override fun exit() {
        try {
            if (AIDLClient.Companion.iPython?.asBinder()?.isBinderAlive == true) {
                AIDLClient.Companion.iPython?.exit()
            }
        } catch (e: DeadObjectException) {
            // 服务已死，清理无效对象
            AIDLClient.Companion.iPython = null
            XLog.e( "Python service is dead"+e.message)
        } catch (e: RemoteException) {
            XLog.e( "Remote call failed"+e.message)
        }
    }

    override fun convertNativeObjectToMap(nativeObj: Any): HashMap<Any, Any> {
        TODO("Not yet implemented")
    }

    override fun convertNativeArrayToList(value: Any): ArrayList<Any> {
        TODO("Not yet implemented")
    }
}