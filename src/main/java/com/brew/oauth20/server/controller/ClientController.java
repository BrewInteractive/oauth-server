package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.UpdateClientLogoResponseModel;
import com.brew.oauth20.server.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/client/logo")
    public ResponseEntity<Object> updateClientLogo(@RequestParam("client_id") String clientId,
                                                   @RequestBody String logoFile) {
        ClientModel client = clientService.getClient(clientId);
        if (client == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        }

        try {
            String logoUrl = clientService.setClientLogo(clientId, logoFile);
            return ResponseEntity.ok(UpdateClientLogoResponseModel.builder().client_logo_url(logoUrl).client_id(clientId).build());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload client logo");
        }
    }
}