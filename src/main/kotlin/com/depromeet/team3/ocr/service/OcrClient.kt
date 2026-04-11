package com.depromeet.team3.ocr.service

import com.depromeet.team3.common.domain.Product

interface OcrClient {
    fun analyzeImage(imageBytes: ByteArray, mimeType: String): Product
}
