package com.nagarro.si.pba.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/testpath")
public class DummyController {
    @GetMapping("/hello")
    public String helloWorld() {
        return "hello world!";
    }
}
