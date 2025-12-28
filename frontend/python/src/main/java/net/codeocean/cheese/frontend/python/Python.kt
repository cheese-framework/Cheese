package net.codeocean.cheese.frontend.python

import android.content.Context
import android.os.Environment
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import net.codeocean.cheese.core.CoreEnv
import net.codeocean.cheese.core.IPython
import net.codeocean.cheese.core.Script
import java.io.File
import java.io.FileOutputStream
import java.lang.Thread.sleep
import kotlin.concurrent.thread
class Python(private val androidContext: android.content.Context) : Script {

    init {
        CoreEnv.globalVM.add<Script>(this)
    }


    fun start() {
        try {

            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(androidContext));
            }
            val py = Python.getInstance()
            py.getModule("init").callAttr("init")
            val result = py.getModule("main")
                .callAttr("main")
        }catch (e:Exception){
            val errorMsg = buildString {
                append("‼️ 异常详情 ‼️\n")
                append("类型: ${e.javaClass.name}\n")
                append("消息: ${e.message ?: "无"}\n")
                append("堆栈轨迹:\n${e.stackTraceToString()}\n")
                e.cause?.takeIf { cause ->
                    cause.javaClass != e.javaClass || cause.message != e.message
                }?.let { cause ->
                    append("根本原因:\n")
                    append("类型: ${cause.javaClass.name}\n")
                    append("消息: ${cause.message ?: "无"}\n")
                }
            }
            Log.e( errorMsg)
        }

    }



    override fun run(workingDir: File) {

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(CoreEnv.envContext.context));
        }
        val py = Python.getInstance()
         py.getModule("main")
            .callAttr("main")
    }

    override fun exit() {

    }

    override fun convertNativeObjectToMap(nativeObj: Any): HashMap<Any, Any> {
        val map = HashMap<Any, Any>()
        if (nativeObj is Array<*>) {
            for (i in 0 until nativeObj.size step 2) {
                if (i + 1 < nativeObj.size) {
                    val key = nativeObj[i]?.toString() ?: continue
                    val value = nativeObj[i + 1]
                    map[key] = when {
                        value == null -> null
                        value is Array<*> -> convertNativeObjectToMap(value)
                        else -> value
                    } as Any  // 确保返回类型是 Any
                }
            }
        }
        return map
    }

    override fun convertNativeArrayToList(value: Any): ArrayList<Any> {
        TODO("Not yet implemented")
    }

}