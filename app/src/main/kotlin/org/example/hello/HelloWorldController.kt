package org.example.hello

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {
    data class VersionResponseDto(val version: String)

    @GetMapping(value = ["/"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun index(): VersionResponseDto = VersionResponseDto("0.0.1-example")
}
