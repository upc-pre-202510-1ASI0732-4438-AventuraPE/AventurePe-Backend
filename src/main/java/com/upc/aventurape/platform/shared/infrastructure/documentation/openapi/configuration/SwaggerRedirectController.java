package com.upc.aventurape.platform.shared.infrastructure.documentation.openapi.configuration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

  @GetMapping("/")
  public String redirectToSwagger() {
    return "redirect:/swagger-ui/index.html#/";
  }
}