package com.brew.oauth20.server.controller;

import com.brew.oauth20.server.model.AuthorizeRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
public class AuthController {

    @RequestMapping(value = {"/oauth/authorize"},
            method = RequestMethod.GET)
    public ResponseEntity authorizePost(@Valid @ModelAttribute AuthorizeRequest authorizeRequest,
                                        BindingResult result,
                                        ModelMap model) {
        HttpHeaders responseHeaders = new HttpHeaders();

        if (result.hasErrors()) {
            responseHeaders.set("Location", "https://mydomain.com/callback?" +
                    "error=invalid_request");
            return ResponseEntity.badRequest()
                    .headers(responseHeaders)
                    .body("invalid_request");
        } else {

            responseHeaders.set("Location", "https://mydomain.com/callback?" +
                    "code=getgetget" +
                    "&locale=fr" +
                    "&state=abc123" +
                    "&userState=Authenticated");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body("");
        }

    }

    @RequestMapping(value = {"/oauth/authorize"},
            method = RequestMethod.POST)
    public ResponseEntity authorizeGet(@Valid @RequestBody AuthorizeRequest authorizeRequest,
                                       BindingResult result,
                                       ModelMap model) {
        HttpHeaders responseHeaders = new HttpHeaders();

        if (result.hasErrors()) {
            responseHeaders.set("Location", "https://mydomain.com/callback?" +
                    "error=invalid_request");
            return ResponseEntity.badRequest()
                    .headers(responseHeaders)
                    .body("invalid_request");
        } else {

            responseHeaders.set("Location", "https://mydomain.com/callback?" +
                    "code=postpostpost" +
                    "&locale=fr" +
                    "&state=abc123" +
                    "&userState=Authenticated");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body("");
        }

    }
}
