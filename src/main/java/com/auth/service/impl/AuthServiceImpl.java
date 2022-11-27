package com.auth.service.impl;

import com.auth.exception.ArgumentException;
import com.auth.exception.UserNotFoundException;
import com.auth.model.dto.AuthRequest;
import com.auth.model.dto.UserDto;
import com.auth.model.entity.User;
import com.auth.service.AuthService;
import com.auth.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    private final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<?> register(UserDto userDto) throws Exception {
        if (ObjectUtils.isEmpty(userDto)) {
            throw new ArgumentException("User register is empty !!!");
        }
        if (userDto.getEmail().isEmpty()) {
            throw new ArgumentException("Email is not acceptable !!!");
        }
        if (userDto.getPassword().isEmpty()) {
            throw new ArgumentException("Password is not empty !!!");
        }

        Optional<UserDto> user = userService.findByEmail(userDto.getEmail());
        if (user.isPresent() && !ObjectUtils.isEmpty(user.get().getUserId())) {
            return new ResponseEntity<>("User is exist in database", HttpStatus.BAD_REQUEST);
        }

        String encodePass = "abcdef".concat(userDto.getPassword()).concat("abcdef");
        userDto.setPassword(encodePass);
        UserDto dto = userService.save(userDto);
        if (dto.getUserId() > 0) {
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Cannot sign up !!!", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> login(AuthRequest request) throws Exception {
        UserDto user = userService.findByEmail(request.getEmail()).orElseThrow(() -> {
            throw new UserNotFoundException("User not found!!!");
        });
        if (!ObjectUtils.isEmpty(user) && !ObjectUtils.isEmpty(user.getUserId()) && user.getUserId() > 0) {
            String encodePass = "abcdef".concat(request.getPassword()).concat("abcdef");
            if (encodePass.equals(user.getPassword())) {
                return new ResponseEntity<>(UserDto.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .username(user.getUsername()).build(), HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(UserDto.builder().build(), HttpStatus.BAD_REQUEST);
    }
}
