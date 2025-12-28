package net.codeocean.cheese.core.aidl

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Toast
import androidx.annotation.GuardedBy
import net.codeocean.cheese.core.CoreEnv
import net.codeocean.cheese.core.CoreFactory
import net.codeocean.cheese.core.IPython
import net.codeocean.cheese.core.IPythonCallback

import net.codeocean.cheese.core.IRecordScreen
import net.codeocean.cheese.core.IRecordScreenCallback
import net.codeocean.cheese.core.MultiIBinder
import net.codeocean.cheese.core.utils.ScriptLogger
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class AIDLClient(private val activity: Activity) {

    companion object {
        @GuardedBy("lock")
         var iRecordScreen: IRecordScreen? = null
         var iPython: IPython? = null
    }
    // region 成员变量

    private val lock = Any()

    // 服务绑定状态
    private val serviceBound = AtomicBoolean(false)
    private val connectLatch = CountDownLatch(1) // 用于同步等待

    private val deathRecipient = IBinder.DeathRecipient {
        activity.runOnUiThread {
            Toast.makeText(activity, "服务已终止", Toast.LENGTH_SHORT).show()
            release()
            thread {        CoreEnv.envContext.activity?.let { AIDLClient(it).start() } }

        }
    }

    @GuardedBy("lock")
    private var registeredCallback: IRecordScreenCallback? = null
    private var iPythonCallback: IPythonCallback? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            synchronized(lock) {
                try {
                    val multiIBinder = MultiIBinder.Stub.asInterface(service)
                    iRecordScreen =
                        IRecordScreen.Stub.asInterface(multiIBinder.iRecordScreen).apply {
//                            service?.linkToDeath(deathRecipient, 0)
                            registeredCallback = createCallback().also { callback ->
                                registerCallback(callback)
                                Log.d("AIDLClient", "注册回调成功")
                            }
                        }
                    iPython =
                        IPython.Stub.asInterface(multiIBinder.iPython).apply {
                            service?.linkToDeath(deathRecipient, 0)
                            iPythonCallback = createPythonCallback().also { callback ->
                                registerCallback(callback)
                                Log.d("AIDLClient", "注册回调成功")
                            }
                        }
//                    iPython?.start()
//                    iRecordScreen = IRecordScreen.Stub.asInterface(service).apply {
//                        service?.linkToDeath(deathRecipient, 0)
//                        registeredCallback = createCallback().also { callback ->
//                            registerCallback(callback)
//                            Log.d("AIDLClient", "注册回调成功")
//                        }
//                    }
                    serviceBound.set(true)
                } catch (e: RemoteException) {
                    Log.e("AIDLClient", "服务连接异常", e)
                } finally {
                    connectLatch.countDown() // 释放等待锁
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            synchronized(lock) {
                serviceBound.set(false)
                iRecordScreen = null
            }
        }
    }
    // endregion

    // region 公共方法
    /**
     * 启动服务并等待绑定完成
     * @param timeoutMs 最大等待时间（毫秒）
     * @return true表示服务绑定成功
     */
    fun start(timeoutMs: Long = 3000): Boolean {
        if (serviceBound.get()) return true

        try {
            Intent().apply {
                component = ComponentName(
                    "net.codeocean.cheese",  // 目标应用的包名
                    "net.codeocean.cheese.frontend.python.PythonService"  // 完整服务类名
                )
                action = "cheese.AIDLService"
                if (!activity.bindService(this, connection, Context.BIND_AUTO_CREATE)) {
                    Log.e("AIDLClient", "绑定服务失败")
                    return false
                }
            }

//            // 等待绑定完成或超时
//            println( connectLatch.await(timeoutMs, TimeUnit.MILLISECONDS) )
//            println(serviceBound.get())
            return connectLatch.await(timeoutMs, TimeUnit.MILLISECONDS) &&  serviceBound.get()
        } catch (e: Exception) {
            Log.e("AIDLClient", "启动服务异常", e)
            return false
        }
    }

    /**
     * 检查服务是否已绑定
     */
    fun isServiceBound(): Boolean = serviceBound.get()

    /**
     * 释放所有资源
     */
    fun release() {
        synchronized(lock) {
            try {
                // 先尝试取消注册回调
                try {
                    iRecordScreen?.unregisterCallback()
                } catch (e: RemoteException) {
                    Log.w("AIDLClient", "取消注册回调失败", e)
                }

                // 解除死亡监听
                iRecordScreen?.asBinder()?.unlinkToDeath(deathRecipient, 0)
            } catch (e: Exception) {
                Log.e("AIDLClient", "释放资源异常", e)
            } finally {
                safeUnbindService()
                iRecordScreen = null
                registeredCallback = null
                serviceBound.set(false)

                // 重置等待锁
                connectLatch.countDown()
            }
        }
    }
    // endregion
    // region 私有方法
    private fun safeUnbindService() {
        try {
            activity.unbindService(connection)
        } catch (e: IllegalArgumentException) {
            Log.w("AIDLClient", "解绑服务异常: ${e.message}")
        }
    }
    private fun createCallback() = object : IRecordScreenCallback.Stub() {
        override fun requestPermission(timeout: Int): Boolean {
            return CoreFactory.getRecordScreen().requestPermission(timeout)

        }

        override fun checkPermission(): Boolean {
            return CoreFactory.getRecordScreen().checkPermission()
        }

        override fun captureScreen(timeout: Int, x: Int, y: Int, ex: Int, ey: Int): Bitmap? {
            return CoreFactory.getRecordScreen().captureScreen(timeout,x,y,ex,ey)
        }
    }
    private fun createPythonCallback() = object : IPythonCallback.Stub() {
        override fun i(msg: String) {
            ScriptLogger.i(msg)
        }

        override fun d(msg: String) {
            ScriptLogger.d(msg)
        }

        override fun w(msg: String) {
            ScriptLogger.w(msg)
        }

        override fun e(msg: String) {
            ScriptLogger.e(msg)
        }

    }
}