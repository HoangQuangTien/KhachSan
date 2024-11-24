//package com.example.DuAnTotNghiepKs.rest;
//
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class LanguageController {
//
//    @RequestMapping("/detect-language")
//    public String detectLanguage(@RequestHeader(value = "Accept-Language", defaultValue = "en") String language) {
//        // Ngôn ngữ mặc định là tiếng Anh nếu không có header
//        return "Ngôn ngữ của bạn là: " + language;
//    }
//}
////