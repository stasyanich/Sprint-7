package com.example.demo.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/")
class Controller {
    @GetMapping
    fun get() : ResponseEntity<String> =  ResponseEntity.ok(UUID.randomUUID().toString())
}