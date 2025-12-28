package net.codeocean.cheese


import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import com.elvishew.xlog.XLog
import com.umeng.commonsdk.UMConfigure

import net.codeocean.cheese.bottomnav.BottomNav
import net.codeocean.cheese.core.CoreEnv
import net.codeocean.cheese.core.Misc.showTermsAndConditionsDialog
import net.codeocean.cheese.ui.theme.CheeseTheme
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import kotlin.concurrent.thread


class ProfileViewModel : ViewModel() {
    val appLogger = mutableStateListOf<String>()
    val connectData = mutableStateOf(false)
}

val appLogger = ProfileViewModel().appLogger
val connectData = ProfileViewModel().connectData

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {





//    private var bookManager: IBookManager? = null
//
//    // 显式声明死亡监听器
//    private val deathRecipient = IBinder.DeathRecipient {
//        runOnUiThread {
//            Toast.makeText(this, "服务已终止", Toast.LENGTH_SHORT).show()
//            unbindService(connection)
//        }
//    }
//
//    // 显式声明ServiceConnection类型
//    private val connection: ServiceConnection = object : ServiceConnection {
//
//
//        @SuppressLint("DiscouragedPrivateApi")
//        @Throws(IOException::class)
//        private fun getParcelFileDescriptor(sharedMemory: SharedMemory): ParcelFileDescriptor {
//            return try {
//                // 使用反射获取 FileDescriptor
//                val getFdMethod = SharedMemory::class.java.getDeclaredMethod("getFd")
//                val fd = getFdMethod.invoke(sharedMemory) as Int
//
//                // 创建新的 FileDescriptor
//                val fileDescriptor = FileDescriptor()
//                val setIntMethod = FileDescriptor::class.java.getDeclaredMethod(
//                    "setInt$",
//                    Int::class.javaPrimitiveType
//                )
//                setIntMethod.invoke(fileDescriptor, fd)
//                ParcelFileDescriptor.dup(fileDescriptor)
//            } catch (e: Exception) {
//                throw IOException("Failed to get ParcelFileDescriptor from SharedMemory", e)
//            }
//        }
//
//        @SuppressLint("NewApi")
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            bookManager = IBookManager.Stub.asInterface(service).also {
//                service?.linkToDeath(deathRecipient, 0)
//            }
//
//             val callback = object : IBookManagerCallback.Stub() {
//                override fun onBookAdded(book: Book) {
//                   println("成功")
//                }
//
//                override fun onTransferComplete(resultCode: Int) {
//
//                }
//            }
//
//
//            bookManager?.registerCallback(callback)
//
//            val imp =
//                CoreFactory.getConverters().sdToStream("/storage/emulated/0/Pictures/ccccc.png")!!
//            val bitmap = CoreFactory.getConverters().streamToBitmap(imp)!!
//
//            val bufferSize = bitmap.allocationByteCount
//            val time = measureTimeMillis {
//                // 创建共享内存
//                val sharedMemory = SharedMemory.create("bitmap_shared_mem", bufferSize)
//
//                // 映射内存并写入数据（修复 use 问题）
//                val buffer = sharedMemory.mapReadWrite()
//                try {
//                    bitmap.copyPixelsToBuffer(buffer)
//                    buffer.rewind()
//                } finally {
//                    SharedMemory.unmap(buffer) // 手动解除映射
//                    sharedMemory.setProtect(OsConstants.PROT_READ);
//                }
//                bookManager?.transferBitmap(sharedMemory, bitmap.width, bitmap.height)
//
//            }
//            println("耗时: ${time} ms")
//        }
//
//        override fun onServiceDisconnected(name: ComponentName?) {
//            bookManager = null
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
////        unbindService(connection)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        println("xxxxxxx"+this.applicationContext.assets)
//        thread {
////            sleep(5000)
//////            // 绑定服务
//////            Intent(this, BookManagerService::class.java).apply {
//////                action = "com.example.aidl.IBookManager"
//////                bindService(this, connection, Context.BIND_AUTO_CREATE)
//////            }
////
////
////        println("ct1:"+CoreEnv.envContext.context)
////            println("结果："+AIDLClient(this).start())
////
////
//        }


        enableEdgeToEdge()
        setContent {
            CheeseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BottomNav()
                }
            }
        }

        showTermsAndConditionsDialog{
            println("初始化一些东西")
            CoreEnv.envContext.activity?.let{
                UMConfigure.init(
                    it,
                    "66b81b19192e0574e75c567a",
                    "git",
                    UMConfigure.DEVICE_TYPE_PHONE,
                    null
                )
            }

        }
        captureLogcatToFile(this)
//        Python().start(this)
    }

    private fun captureLogcatToFile(context: Context) {
        try {
            // 清空旧日志（可选）
            Runtime.getRuntime().exec("logcat -c").waitFor()

            // 获取logcat进程
            val process = Runtime.getRuntime().exec("logcat -v threadtime")

            // 获取存储路径
            val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            if (documentsDir == null) {
                Log.e("LogcatCapture", "Documents directory is not available.")
                return
            }

            // 创建日志文件
            val logFile = File(documentsDir, "writelog_${System.currentTimeMillis()}.txt")

            // 确保目录存在
            if (!documentsDir.exists()) {
                documentsDir.mkdirs() // 创建所有必要的父目录
            }

            // 创建文件（如果不存在）
            if (!logFile.exists()) {
                logFile.createNewFile()
            }

            // 捕获日志
            thread {
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    FileWriter(logFile, true).use { writer -> // true for appending logs
                        val logLines = mutableListOf<String>()
                        var line: String?
                        var isStackTrace: Boolean
                        while (reader.readLine().also { line = it } != null) {
                            // 如果是堆栈，加入到当前堆栈信息
                            isStackTrace =
                                line?.contains("Exception") == true || line?.contains("at ") == true
                            if (isStackTrace) {
                                logLines.add(line!!)
                            } else {
                                // 不是堆栈的下一行，先保存之前的堆栈信息
                                if (logLines.isNotEmpty()) {
                                    // 合并堆栈信息并写入文件
                                    val fullLog = logLines.joinToString("\n")
                                    writer.write("$fullLog\n")
                                    writer.flush() // 确保写入文件
                                    // 添加到内存日志（可选，建议限制数量）
                                    if (appLogger.size > 1000) { // 防止内存溢出
                                        appLogger.clear()
                                    }
                                    appLogger.add(fullLog)
                                    logLines.clear()
                                } else {
                                    writer.write("$line\n")
                                    writer.flush() // 确保写入文件
                                    if (appLogger.size > 1000) { // 防止内存溢出
                                        appLogger.clear()
                                    }
                                    appLogger.add(line!!)
                                }
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            XLog.e("LogcatCapture", "Error: ${e.message}", e)
        }
    }


}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CheeseTheme {
        Greeting("Android")
    }
}