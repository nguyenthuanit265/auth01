package com.auth.controller;

import com.auth.model.dto.AuthRequest;
import com.auth.model.dto.RegisterDto;
import com.auth.model.dto.UserDto;
import com.auth.service.AuthService;
import com.auth.utils.JwtTokenUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    //    private final AuthenticationManager authManager;
    private final JwtTokenUtils jwtUtils;

    private final AuthService authService;

    private final ModelMapper modelMapper = new ModelMapper();

    public AuthController(JwtTokenUtils jwtUtils, AuthService authService) {

        this.jwtUtils = jwtUtils;
        this.authService = authService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) throws Exception {
        LOGGER.info("----------------------- function login() HERE -----------------------");
        return authService.login(request);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/sign-up")
    public ResponseEntity<?> register(@RequestBody RegisterDto request) throws Exception {
        LOGGER.info("----------------------- function register() HERE -----------------------");
        return authService.register(modelMapper.map(request, UserDto.class));

    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> test() {
        LOGGER.info("----------------------- function test() HERE -----------------------");
        try {
            return new ResponseEntity<>("Test", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
