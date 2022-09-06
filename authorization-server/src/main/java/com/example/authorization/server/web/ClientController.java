package com.example.authorization.server.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientController {

    @GetMapping("client")
    public String createPage() {
        return "client/create";
    }

}
