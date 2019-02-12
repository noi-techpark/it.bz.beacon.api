package it.bz.beacon.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller //note - this is a spring-boot controller, not @RestController
public class SwaggerController {

    @RequestMapping("/")
    public String swaggerUi() {
        return "redirect:/swagger-ui.html";
    }
}