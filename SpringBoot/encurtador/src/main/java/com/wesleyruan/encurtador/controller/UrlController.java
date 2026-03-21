package com.wesleyruan.encurtador.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wesleyruan.encurtador.model.UrlModel;
import com.wesleyruan.encurtador.service.UrlService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/")
public class UrlController{
    @Autowired
    private UrlService urlService;

    @GetMapping("/shortenUrl")
    public ResponseEntity<UrlModel> shortenUrl(@RequestParam String url,  HttpServletRequest request){
        return urlService.shortenUrl(url, request);
    }

    @GetMapping("/r/{code}")
    public void redirect(@PathVariable String code, HttpServletResponse response) throws IOException {
        // Pega a nova URL do service
        String newUrl = urlService.getRedirectUrl(code);

        if (newUrl != null) {
            response.sendRedirect(newUrl); // Redireciona o usuário
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "URL não encontrada");
        }
    }

}
