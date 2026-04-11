package com.depromeet.team3.ocr.controller

import com.depromeet.team3.ocr.controller.dto.OcrResponse
import com.depromeet.team3.ocr.service.OcrService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/ocr")
class OcrController(
    private val ocrService: OcrService,
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun extractProduct(
        @RequestParam("image") image: MultipartFile,
    ): OcrResponse = OcrResponse.from(ocrService.extract(image))
}
