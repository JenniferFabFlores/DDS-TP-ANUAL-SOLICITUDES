package ar.edu.utn.dds.k3003.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BaseController {

    @GetMapping
    public String getBase() {
        return "TP Entrega 2 API is running!";
    }
}