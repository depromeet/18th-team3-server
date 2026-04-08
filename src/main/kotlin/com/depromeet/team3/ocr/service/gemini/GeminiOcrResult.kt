package com.depromeet.team3.ocr.service.gemini

import com.depromeet.team3.common.domain.BoundingBox
import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.common.domain.Product.Field

/**
 * Gemini가 responseSchema에 따라 생성하는 JSON 문자열을 역직렬화한 결과.
 * 단일 상품만 받도록 스키마(PRODUCT_SCHEMA)를 OBJECT 타입으로 정의했음.
 */
data class GeminiOcrResult(
    val name: String?,
    val price: Int?,
    val category: String?,
    val nameBoundingBox: GeminiBoundingBox?,
    val priceBoundingBox: GeminiBoundingBox?,
    val categoryBoundingBox: GeminiBoundingBox?,
) {
    fun toProduct() = Product(
        name = toField(name, nameBoundingBox),
        price = toField(price, priceBoundingBox),
        category = toField(category, categoryBoundingBox),
    )

    private fun <T : Any> toField(value: T?, box: GeminiBoundingBox?): Field<T> = when {
        value == null -> Field.NotFound
        box == null -> Field.Inferred(value)
        else -> Field.Extracted(value, box.toBoundingBox())
    }

    data class GeminiBoundingBox(
        val ymin: Int,
        val xmin: Int,
        val ymax: Int,
        val xmax: Int,
    ) {
        fun toBoundingBox() = BoundingBox(
            yMin = ymin,
            xMin = xmin,
            yMax = ymax,
            xMax = xmax,
        )
    }
}
