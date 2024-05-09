package com.example.boardsserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SimpleController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Welcome to the Home Page!";
    }

    @GetMapping("/favicon.ico")
    @ResponseBody
    public void favicon() {
        // Пустой метод, чтобы просто избежать 404 ошибки на запрос favicon.ico
    }
}
