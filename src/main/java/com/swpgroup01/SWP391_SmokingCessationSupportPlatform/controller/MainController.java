package com.swpgroup01.SWP391_SmokingCessationSupportPlatform.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    String helloWorld() {
        return "hello world";
    }
}
