package fi.vm.sade.tarjonta.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
@RequestMapping("/")
public class StaticController {
  @GetMapping({"/swagger", "/swagger/**"})
  public String swagger() {
    return "redirect:/swagger-ui/index.html";
  }
}
