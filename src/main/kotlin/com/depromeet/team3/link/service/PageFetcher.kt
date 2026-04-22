package com.depromeet.team3.link.service

interface PageFetcher {
    fun fetch(url: String): PageContent
}
