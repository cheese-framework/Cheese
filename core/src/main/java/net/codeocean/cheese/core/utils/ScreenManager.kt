package net.codeocean.cheese.core.utils
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import android.util.DisplayMetrics
import android.util.Log
import android.view.IRotationWatcher
import android.view.Surface
import android.view.WindowManager
import net.codeocean.cheese.core.CoreEnv
import kotlin.concurrent.thread
var Landscape =false
class RotationWatcher : IRotationWatcher.Stub() {
    @Throws(RemoteException::class)
    override fun onRotationChanged(rotation: Int) {
        Log.e("Service", "onRotationChanged = $rotation")
        if (rotation==1){
            Landscape=true
        }else{
            Landscape=false
        }
    }

    override fun asBinder(): IBinder {
        return this
    }
}
object ScreenManager {
    fun getMethodParamTypes(clazz: Class<*>, methodName: String): Array<Class<*>>? {
        return try {
            clazz.declaredMethods.firstOrNull {
                Log.e("Service", "Checking method: ${it.name}")
                it.name == methodName
            }?.parameterTypes?.also {
                Log.e("Service", "Found method $methodName with params: ${it.joinToString()}")
            }
        } catch (e: Exception) {
            Log.e("Service", "Error finding method", e)
            null
        }
    }
    fun initWatchRotation(){
        try {
            // 1. 获取 ServiceManager 类并得到 getService 方法
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getDeclaredMethod("getService", String::class.java)

            // 2. 调用 getService 方法获取 WindowManagerService 的 Binder 对象
            val windowServiceBinder = getServiceMethod.invoke(null, "window")

            // 3. 获取 IWindowManager.Stub 类
            val stubClass = Class.forName("android.view.IWindowManager\$Stub")

            // 4. 获取 asInterface 方法
            val asInterfaceMethod = stubClass.getMethod("asInterface", IBinder::class.java)

            // 5. 获取 IWindowManager 实例
            val iWindowManager = asInterfaceMethod.invoke(null, windowServiceBinder)

            // 现在可以使用 iWindowManager 对象
            println("成功获取 IWindowManager 实例: $iWindowManager")
            val paramTypes = getMethodParamTypes(iWindowManager::class.java, "watchRotation")
            println(paramTypes?.joinToString { it.name })
            paramTypes?.let { nonNullParamTypes ->
                val watchRotation = iWindowManager::class.java.getDeclaredMethod("watchRotation", *nonNullParamTypes)

                Log.e(
                    "Service",
                    "onStartCommand rotation = " + watchRotation.invoke(iWindowManager, RotationWatcher(),0)
                )
                // 在这里使用 watchRotation
            } ?: throw IllegalStateException("paramTypes is null")



        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var deviceScreenHeight: Int = 0
    private var deviceScreenWidth: Int = 0
    private var initialized: Boolean = false
    private var deviceScreenDensity: Int = 0

    fun init(activity: Activity) {
        if (initialized) return

        val metrics = DisplayMetrics().apply {
            activity.windowManager.defaultDisplay.getRealMetrics(this)
        }

        deviceScreenHeight = metrics.heightPixels.takeIf { it != 0 }
            ?: activity.windowManager.defaultDisplay.height.takeIf { it != 0 }
                    ?: activity.resources.displayMetrics.heightPixels

        deviceScreenWidth = metrics.widthPixels.takeIf { it != 0 }
            ?: activity.windowManager.defaultDisplay.width.takeIf { it != 0 }
                    ?: activity.resources.displayMetrics.widthPixels

        if (deviceScreenWidth > deviceScreenHeight) {
            val temp = deviceScreenWidth
            deviceScreenWidth = deviceScreenHeight
            deviceScreenHeight = temp
        }

        deviceScreenDensity = metrics.densityDpi
        initWatchRotation()
        initialized = true
    }

    fun getDeviceScreenHeight() = deviceScreenHeight

    fun getDeviceScreenWidth() = deviceScreenWidth

    fun getDeviceScreenDensity() = deviceScreenDensity

    fun getAutoScreenWidth() = if (isLandscape()) getDeviceScreenHeight() else getDeviceScreenWidth()

    fun getAutoScreenHeight() = if (isLandscape()) getDeviceScreenWidth() else getDeviceScreenHeight()

    private fun scale(value: Int, dimension: Int) =
        if (dimension == 0 || !initialized) value else value * deviceScreenWidth / dimension

    fun scaleX(x: Int, width: Int) = scale(x, width)

    fun scaleY(y: Int, height: Int) = scale(y, height)

    private fun rescale(value: Int, dimension: Int) =
        if (dimension == 0 || !initialized) value else value * dimension / deviceScreenWidth

    fun rescaleX(x: Int, width: Int) = rescale(x, width)

    fun rescaleY(y: Int, height: Int) = rescale(y, height)

    fun isLandscape(): Boolean {
//        println(CoreEnv.envContext.activity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           return  Landscape
        }else{
            val windowManager = CoreEnv.envContext.activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val rotation = windowManager.defaultDisplay.rotation
            return rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270
        }

    }


}

