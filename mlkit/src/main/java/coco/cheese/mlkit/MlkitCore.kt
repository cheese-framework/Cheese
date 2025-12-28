package coco.cheese.mlkit

import android.graphics.Bitmap
import com.google.mlkit.common.MlKit
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.GlobalScope
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

val CHINESE = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
val LATIN = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

class MlkitCore {

    fun textRecognition(bitmap: Bitmap, recognizer: TextRecognizer): ResultType {

        var resultType: ResultType? = null

        val latch = CountDownLatch(1)
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(inputImage)
            .addOnSuccessListener { result ->

                resultType = ResultType.Success(result) // 成功时存储结果
                latch.countDown()
            }
            .addOnFailureListener { e ->
                resultType = ResultType.Error(e) // 失败时存储异常
                latch.countDown()
            }
        val timeout = 5L // 超时时间
        val timeUnit = TimeUnit.SECONDS
        latch.await(timeout,timeUnit)
        return resultType ?: ResultType.Error(Exception("Unknown error occurred")) // 返回结果
    }


    sealed class ResultType {
        data class Success(val result: Text) : ResultType()
        data class Error(val exception: Exception) : ResultType()
    }
}