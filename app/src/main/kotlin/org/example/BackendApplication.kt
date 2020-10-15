package org.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication // (scanBasePackages = ["org.example"])
//@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 7200)
class BackendApplication

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}
