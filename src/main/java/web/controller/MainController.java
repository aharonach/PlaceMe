package web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MainController {
    @GetMapping("/")
    public String main() {
        return "server running";
    }
}