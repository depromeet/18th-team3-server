package com.depromeet.team3.common.domain

/**
 * 원본 이미지의 실제 픽셀로 변환하려면:
 *   pixelX = xmin / 1000.0 * imageWidth
 *   pixelY = ymin / 1000.0 * imageHeight
 *   pixelWidth = (xmax - xmin) / 1000.0 * imageWidth
 *   pixelHeight = (ymax - ymin) / 1000.0 * imageHeight
 */
data class BoundingBox(
    val yMin: Int,
    val xMin: Int,
    val yMax: Int,
    val xMax: Int,
)
