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
) {
    init {
        // Gemini 공식 문서 기준 좌표는 0..1000 범위로 정규화됨.
        // https://ai.google.dev/gemini-api/docs/bounding-boxes
        require(listOf(yMin, xMin, yMax, xMax).all { it in VALID_RANGE }) {
            "BoundingBox 좌표는 $VALID_RANGE 범위여야 합니다: " +
                "(yMin=$yMin, xMin=$xMin, yMax=$yMax, xMax=$xMax)"
        }
        require(yMin <= yMax && xMin <= xMax) {
            "BoundingBox 최소 좌표는 최대 좌표보다 클 수 없습니다: " +
                "(yMin=$yMin, xMin=$xMin, yMax=$yMax, xMax=$xMax)"
        }
    }

    companion object {
        private val VALID_RANGE = 0..1000
    }
}
