package com.brew.oauth20.server.controller;

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


@RestController
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/client/logo")
    public ResponseEntity<Object> updateClientLogo(@RequestParam("client_id") String clientId,
                                                   @Valid @RequestBody UpdateClientLogoRequestModel requestModel,
                                                   BindingResult validationResult) {
        try {
            if (validationResult.hasErrors() || clientId.isEmpty() || clientId.isBlank()) {
                return new ResponseEntity<>("invalid_request", HttpStatus.BAD_REQUEST);
            }

            if (!clientService.existsByClientId(clientId)) {
                return new ResponseEntity<>("client_not_found", HttpStatus.NOT_FOUND);
            }

            String logoUrl = clientService.setClientLogo(clientId, requestModel.logoFile());
            var response = UpdateClientLogoResponseModel.builder().client_logo_url(logoUrl).client_id(clientId).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("server_error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}