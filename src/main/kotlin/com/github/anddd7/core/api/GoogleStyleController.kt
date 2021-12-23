package com.github.anddd7.core.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URLDecoder
import java.nio.charset.Charset

@RestController
@RequestMapping("/google")
class GoogleStyleController {
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): String = URLDecoder.decode(id, Charset.defaultCharset())

    @GetMapping("/{id}:print")
    fun print(@PathVariable id: String) = "@<${URLDecoder.decode(id, Charset.defaultCharset())}>@"
}
