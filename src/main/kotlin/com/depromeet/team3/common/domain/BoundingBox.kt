package com.depromeet.team3.common.domain

/**
 * 원본 이미지의 실제 픽셀로 변환하려면:
 *   pixelX = xMin / 1000.0 * imageWidth
 *   pixelY = yMin / 1000.0 * imageHeight
 *   pixelWidth = (xMax - xMin) / 1000.0 * imageWidth
 *   pixelHeight = (yMax - yMin) / 1000.0 * imageHeight
 */
data class BoundingBox(
    val yMin: Int,
    val xMin: Int,
    val yMax: Int,
    val xMax: Int,
)
