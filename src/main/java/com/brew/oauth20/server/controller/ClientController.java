package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.model.ClientModel;
import com.brew.oauth20.server.model.UpdateClientLogoRequestModel;
import com.brew.oauth20.server.model.UpdateClientLogoResponseModel;
import com.brew.oauth20.server.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
                                                   @Valid @RequestBody UpdateClientLogoRequestModel requestModel,
                                                   BindingResult validationResult) {
        try {
            if (validationResult.hasErrors()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid_request");
            }

            ClientModel client = clientService.getClient(clientId);
            if (client == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("client_not_found");
            }

            String logoUrl = clientService.setClientLogo(clientId, requestModel.logoFile());
            return ResponseEntity.ok(UpdateClientLogoResponseModel.builder().client_logo_url(logoUrl).client_id(clientId).build());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("server_error");
        }
    }
}