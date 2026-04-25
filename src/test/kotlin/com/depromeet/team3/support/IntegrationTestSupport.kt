package com.depromeet.team3.support

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestcontainersConfig::class)
abstract class IntegrationTestSupport
