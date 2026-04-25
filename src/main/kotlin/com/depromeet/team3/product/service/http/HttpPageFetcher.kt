package com.depromeet.team3.product.service.http

import com.depromeet.team3.product.domain.ProductLink
import com.depromeet.team3.product.service.PageContent
import com.depromeet.team3.product.service.PageFetcher
import org.springframework.http.HttpHeaders
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

@Component
class HttpPageFetcher : PageFetcher {
    private val restClient = RestClient
        .builder()
        .requestFactory(
            SimpleClientHttpRequestFactory().apply {
                setConnectTimeout(CONNECT_TIMEOUT_MS)
                setReadTimeout(READ_TIMEOUT_MS)
            },
        )
        .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
        .defaultHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,*/*;q=0.8")
        .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "ko,en;q=0.9")
        .build()

    override fun fetch(link: ProductLink): PageContent {
        // String 오버로드는 URI 템플릿으로 해석되어 {q} 같은 쿼리가 변수로 치환될 수 있으므로 URI 로 명시 전달.
        val body = try {
            restClient
                .get()
                .uri(link.value)
                .retrieve()
                .body(String::class.java)
        } catch (e: RestClientResponseException) {
            throw when {
                e.statusCode.is5xxServerError -> PageFetchException.upstreamError(link, e)
                else -> PageFetchException.clientError(link, e)
            }
        } catch (e: ResourceAccessException) {
            throw PageFetchException.upstreamError(link, e)
        } ?: throw PageFetchException.emptyBody(link)

        val truncated = if (body.length > MAX_HTML_CHARS) body.substring(0, MAX_HTML_CHARS) else body
        return PageContent(link = link, html = truncated)
    }

    companion object {
        private const val CONNECT_TIMEOUT_MS = 5_000
        private const val READ_TIMEOUT_MS = 15_000

        // LLM 토큰 비용 상한. 대형 쇼핑몰(쿠팡 등)은 1MB 가 넘는 경우도 있음.
        private const val MAX_HTML_CHARS = 200_000

        // 기본 RestClient UA 는 일부 사이트에서 차단되므로 실제 브라우저 UA 로 위장.
        private const val USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36"
    }
}
