package com.depromeet.team3

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class Team3Application

fun main(args: Array<String>) {
    runApplication<Team3Application>(*args)
}
