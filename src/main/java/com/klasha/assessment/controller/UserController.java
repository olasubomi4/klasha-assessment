package com.klasha.assessment.controller;

import com.klasha.assessment.entity.User;
import com.klasha.assessment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    UserService userService;


    @PostMapping("/register")
	@Operation(summary = "This endpoint is used to register a user to the api")
	public ResponseEntity<HttpStatus> createUser(@Valid @RequestBody User user) {
		userService.saveUser(user);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}