package com.depromeet.team3.ocr.service

import com.depromeet.team3.common.domain.Product
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class OcrService(
    private val ocrClient: OcrClient,
) {
    fun extract(image: MultipartFile): Product {
        val mimeType = image.contentType ?: throw IllegalArgumentException("이미지 타입이 지정되지 않았습니다.")
        return ocrClient.analyzeImage(image.bytes, mimeType)
    }
}
