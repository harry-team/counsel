package com.harry.counsel.java.domain.counsel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/counsel")
@RestController
public class CounselController {

    @GetMapping("/test")
    public String test() {
        return "test!!!!";
    }
}
