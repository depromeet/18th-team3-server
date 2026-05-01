package com.depromeet.team3.support

import com.depromeet.team3.product.domain.Product
import com.depromeet.team3.product.domain.ProductLink
import com.depromeet.team3.product.service.ProductExtractor

// 외부 LLM 호출을 통합 테스트에서 격리하기 위한 stub.
// 모든 통합 테스트가 같은 IntegrationTestSupport 컨텍스트를 공유하므로 이 빈도 단일 인스턴스다.
// 매 테스트가 본문에서 build 람다를 명시적으로 세팅한다 (셋업 hook · default 리셋 금지).
class StubProductExtractor : ProductExtractor {
    var build: (ProductLink) -> Product = { Product(link = it, name = "기본 상품") }
    override fun extract(link: ProductLink): Product = build(link)
}
