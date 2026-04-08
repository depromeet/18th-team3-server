package com.depromeet.team3.ocr.controller.dto

import com.depromeet.team3.common.domain.Product
import com.depromeet.team3.common.domain.Product.Field

data class OcrResponse(
    val name: ExtractedFieldResponse<String>?,
    val price: ExtractedFieldResponse<Int>?,
    val category: ExtractedFieldResponse<String>?,
) {
    /**
     * boundingBox는 0~1000 정규화된 좌표(ymin, xmin, ymax, xmax).
     * boundingBox가 null이면 추론된 값(이미지에서 직접 읽은 것이 아님).
     */
    data class ExtractedFieldResponse<T>(
        val value: T,
        val boundingBox: BoundingBoxResponse?,
        val isInferred: Boolean,
    )

    data class BoundingBoxResponse(
        val ymin: Int,
        val xmin: Int,
        val ymax: Int,
        val xmax: Int,
    )

    companion object {
        fun from(product: Product) = OcrResponse(
            name = product.name.toResponse(),
            price = product.price.toResponse(),
            category = product.category.toResponse(),
        )

        private fun <T> Field<T>.toResponse(): ExtractedFieldResponse<T>? = when (this) {
            is Field.NotFound -> null
            is Field.Inferred -> ExtractedFieldResponse(
                value = value,
                boundingBox = null,
                isInferred = true,
            )
            is Field.Extracted -> ExtractedFieldResponse(
                value = value,
                boundingBox = BoundingBoxResponse(
                    ymin = boundingBox.yMin,
                    xmin = boundingBox.xMin,
                    ymax = boundingBox.yMax,
                    xmax = boundingBox.xMax,
                ),
                isInferred = false,
            )
        }
    }
}
