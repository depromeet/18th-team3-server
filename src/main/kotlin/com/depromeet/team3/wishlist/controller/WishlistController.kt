package com.depromeet.team3.wishlist.controller

import com.depromeet.team3.common.response.ApiResponse
import com.depromeet.team3.wishlist.controller.dto.WishlistRegisterRequest
import com.depromeet.team3.wishlist.controller.dto.WishlistRegisterResponse
import com.depromeet.team3.wishlist.service.WishlistService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@Tag(name = "Wishlist", description = "위시리스트 등록/조회 API")
@RestController
@RequestMapping("/api/v1/wishlists")
class WishlistController(
    private val wishlistService: WishlistService,
) {
    @Operation(
        summary = "위시리스트 등록",
        description = """
            상품 페이지 URL 을 받아 메타데이터(이름/가격/이미지 등)를 추출한 뒤 게스트의 위시리스트에 등록한다.
            동일 게스트가 동일 상품 링크를 중복 등록하는 경우 409 가 반환된다.
        """,
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "201",
                description = "위시리스트 등록 성공",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = SwaggerApiResponse::class),
                        examples = [
                            ExampleObject(
                                name = "등록 성공",
                                value = """
                                    {
                                      "status": 201,
                                      "data": {
                                        "wishId": 1024,
                                        "name": "에어 조던 1 미드",
                                        "regularPrice": 159000,
                                        "discountedPrice": 119000,
                                        "discountRate": 25,
                                        "currency": "KRW",
                                        "imageUrl": "https://cdn.example.com/p/1024.jpg"
                                      },
                                      "detail": "위시가 등록되었습니다.",
                                      "code": "WISH_CREATED"
                                    }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "잘못된 요청 (URL 형식 오류 등)",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = SwaggerApiResponse::class),
                        examples = [
                            ExampleObject(
                                name = "URL 형식 오류",
                                value = """
                                    {
                                      "status": 400,
                                      "data": null,
                                      "detail": "지원하지 않는 URL 형식입니다.",
                                      "code": "INVALID_INPUT"
                                    }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
            SwaggerApiResponse(
                responseCode = "409",
                description = "이미 위시리스트에 등록된 상품",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = SwaggerApiResponse::class),
                        examples = [
                            ExampleObject(
                                name = "중복 등록",
                                value = """
                                    {
                                      "status": 409,
                                      "data": null,
                                      "detail": "이미 위시리스트에 등록된 상품입니다.",
                                      "code": "WISH_DUPLICATED"
                                    }
                                """,
                            ),
                        ],
                    ),
                ],
            ),
        ],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: WishlistRegisterRequest): ApiResponse<WishlistRegisterResponse> {
        val result = wishlistService.register(rawUrl = request.url, guestId = request.guestId)
        return ApiResponse.created(WishlistRegisterResponse.from(result.wish))
    }
}
