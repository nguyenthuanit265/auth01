package com.auth.service;

import com.auth.model.dto.AuthRequest;
import com.auth.model.dto.UserDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> register(UserDto userDto) throws Exception;
    ResponseEntity<?> login(AuthRequest request) throws Exception;
}
