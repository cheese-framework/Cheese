package net.codeocean.cheese.backend.impl

import android.graphics.Bitmap
import com.elvishew.xlog.XLog
import com.tencent.yolov8ncnn.Yolov8Ncnn
import net.codeocean.cheese.core.CoreEnv
import net.codeocean.cheese.core.Script
import net.codeocean.cheese.core.api.Yolo
import net.codeocean.cheese.core.utils.ConvertersUtils
import net.codeocean.cheese.yolo.YoloHandler

object YoloImpl:Yolo {
    override fun detect(
        bitmap: Bitmap,
        path: String,
        list: Any,
        cpugpu: Number
    ): Array<Yolov8Ncnn.Obj?> {
        val script = CoreEnv.globalVM.get<Script>()
        val l2=script?.convertNativeArrayToList(list)
        return YoloHandler.detect(bitmap,path,(ConvertersUtils.arrayToArrayList(l2!!).filterIsInstance<String>().toTypedArray()), cpugpu.toInt() )
    }

//    override fun detect(
//        bitmap: Bitmap,
//        path: Any,      // 接受任意类型（包括 ConsString）
//        list: Any,      // 不强制要求 ArrayList
//        cpugpu: Number  // 接受任何数字类型
//    ): Array<Yolov8Ncnn.Obj?> {
//        // 打印每个参数的真实类型
//        XLog.d("bitmap 类型: ${bitmap.javaClass.name}", "bitmap 类型: ${bitmap.javaClass.name}")
//        XLog.d("path 类型: ${path.javaClass.name}", "path 类型: ${path.javaClass.name}")
//        XLog.d("list 类型: ${list.javaClass.name}", "list 类型: ${list.javaClass.name}")
//        XLog.d("cpugpu 类型: ${cpugpu.javaClass.name}", "cpugpu 类型: ${cpugpu.javaClass.name}")
//
//
//
//
//        // 在内部进行类型转换
//        val stringPath = path.toString() // 处理 ConsString 转 String
//        val arrayList = list as? ArrayList<*>  // 尝试转换或默认值
//        val intCpugpu = cpugpu.toInt()   // Number 转 Int
//
//        return emptyArray()
//    }

    override fun getSpeed(): Double {
        return  YoloHandler.getSpeed()
    }

    override fun draw(objects: Array<Yolov8Ncnn.Obj>?, b: Bitmap): Bitmap {
        return   YoloHandler.draw(objects,b)
    }
}