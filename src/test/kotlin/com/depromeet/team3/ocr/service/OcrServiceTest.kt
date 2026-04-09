package com.depromeet.team3.ocr.service

import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.common.domain.Product.Field
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OcrServiceTest {

    private val stubClient = object : OcrClient {
        var lastMimeType: String? = null
        var lastBytes: ByteArray? = null
        override fun analyzeImage(imageBytes: ByteArray, mimeType: String): Product {
            lastMimeType = mimeType
            lastBytes = imageBytes
            return Product(
                name = Field.Inferred("우유"),
                price = Field.NotFound,
                category = Field.NotFound,
            )
        }
    }

    private val ocrService = OcrService(stubClient)

    @Test
    fun `빈 파일이 들어오면 IllegalArgumentException 을 던진다`() {
        val file = MockMultipartFile(
            "image",
            "empty.png",
            "image/png",
            ByteArray(0),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            ocrService.extract(file)
        }
        assertEquals("빈 이미지 파일은 처리할 수 없습니다.", ex.message)
    }

    @Test
    fun `mimeType 이 지정되지 않으면 IllegalArgumentException 을 던진다`() {
        val file = MockMultipartFile(
            /* name = */ "image",
            /* originalFilename = */ "product.png",
            /* contentType = */ null,
            /* content = */ byteArrayOf(1, 2, 3),
        )

        val ex = assertFailsWith<IllegalArgumentException> {
            ocrService.extract(file)
        }
        assertEquals("이미지 타입이 지정되지 않았습니다.", ex.message)
    }

    @Test
    fun `정상 요청은 OcrClient 에 mimeType 과 bytes 를 그대로 전달한다`() {
        val bytes = byteArrayOf(10, 20, 30)
        val file = MockMultipartFile("image", "product.png", "image/png", bytes)

        val product = ocrService.extract(file)

        assertEquals("image/png", stubClient.lastMimeType)
        assertEquals(bytes.toList(), stubClient.lastBytes?.toList())
        assertEquals(Field.Inferred("우유"), product.name)
    }
}
