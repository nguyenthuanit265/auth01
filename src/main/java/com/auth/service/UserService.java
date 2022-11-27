package com.auth.service;

import com.auth.model.dto.UserDto;
import com.auth.model.dto.UserResponseDto;
import com.auth.model.entity.Group;
import com.auth.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserResponseDto> findAll();

    UserDto save(UserDto userDto) throws Exception;

    UserDto update(UserDto userDto);

    Optional<UserDto> findById(long id);

    Optional<UserDto> findByEmail(String email);

    Optional<UserDto> findByUsername(String username);

    int deleteById(long id);

    int saveAll(List<UserDto> userDtos);

    UserDto getProfile(long userId);

    List<Group> findAllGroupsByUser(User user);
}
