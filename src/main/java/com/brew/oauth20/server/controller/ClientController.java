package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.data.Client;
import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.service.ClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {

        this.clientService = clientService;
    }

    @GetMapping("/client")
    public ClientModel getClient(@RequestParam(value = "id") UUID id) {
        Client client = clientService.getClient(id);
        return new ClientModel(client.getId(), client.getName());
    }
}