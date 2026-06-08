package com.example.atv3_associacoes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        // Redireciona a raiz do site automaticamente para o catálogo público de produtos
        return "redirect:/produtos/lista";
    }
}