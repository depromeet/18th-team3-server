package com.depromeet.team3.product.domain

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class ProductLinkConverter : AttributeConverter<ProductLink, String> {
    override fun convertToDatabaseColumn(attribute: ProductLink?): String? = attribute?.toString()

    override fun convertToEntityAttribute(dbData: String?): ProductLink? =
        dbData?.let { ProductLink.parse(it) }
}
