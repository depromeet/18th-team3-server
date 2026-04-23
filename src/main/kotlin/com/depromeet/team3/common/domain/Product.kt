package com.depromeet.team3.common.domain

data class Product(
    val name: String? = null,
    val regularPrice: Int? = null,
    val discountedPrice: Int? = null,
    val currency: String? = null,
    val imageUrl: String? = null,
) {
    // discountRate 는 원가와 할인가에서 파생되는 결정적 값이므로 LLM 이 아닌 서버에서 계산한다.
    val discountRate: Int?
        get() {
            val regular = regularPrice ?: return null
            val discounted = discountedPrice ?: return null
            if (regular <= 0 || discounted >= regular) return null
            return ((regular - discounted) * 100.0 / regular).toInt()
        }
}
