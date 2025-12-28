package net.codeocean.cheese.core.api

import android.graphics.Bitmap
import coco.cheese.mlkit.MlkitCore

import net.codeocean.cheese.paddleocr.PaddleOCRHandler
import net.codeocean.cheese.ppocr.PPOCR

interface OCR {
    fun mlkitOcr(
        bitmap: Bitmap,
        recognizer: Int,
    ): MlkitCore.ResultType?
    fun paddleOcrv4(
    ): PaddleOCRHandler
    fun paddleOcrv5(
    ): PPOCR
}