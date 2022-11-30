package com.auth.service.impl;

import com.auth.exception.UserNotFoundException;
import com.auth.model.dto.UserDto;
import com.auth.model.dto.UserResponseDto;
import com.auth.model.entity.Group;
import com.auth.model.entity.User;
import com.auth.repository.UserRepository;
import com.auth.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final ModelMapper modelMapper = new ModelMapper();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserResponseDto.class)).collect(Collectors.toList());
    }

    @Override
    public UserDto save(UserDto userDto) throws Exception {
        try {
            if (ObjectUtils.isEmpty(userDto)) {
                throw new IllegalArgumentException("User register is empty !!!");
            }
            if (userDto.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Email is not acceptable !!!");
            }
            if (userDto.getPassword().isEmpty()) {
                throw new IllegalArgumentException("Password is not acceptable !!!");
            }

            User entity = modelMapper.map(userDto, User.class);
            return modelMapper.map(userRepository.save(entity), UserDto.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.error("Exception save user. Error: {}, {}", ex.getMessage(), ex);
            throw new Exception("Internal Server");
        }
    }

    @Override
    public UserDto update(UserDto user) {
        return null;
    }

    @Override
    public Optional<UserDto> findById(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User is not exist !!!"));
        return Optional.ofNullable(modelMapper.map(user, UserDto.class));
    }

    @Override
    public Optional<UserDto> findByEmail(String email) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isPresent()) {
            return Optional.ofNullable(modelMapper.map(optUser.get(), UserDto.class));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserDto> findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User is not exist !!!"));
        return Optional.ofNullable(modelMapper.map(user, UserDto.class));
    }

    @Override
    public int deleteById(long id) {
        userRepository.deleteById(id);
        return 1;
    }

    @Override
    public int saveAll(List<UserDto> users) {
        return 0;
    }

    @Override
    public UserDto getProfile(long userId) {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication();
//        if (!ObjectUtils.isEmpty(userDetails)) {
//            return modelMapper.map(userRepository.findByEmail(userDetails.getUsername()), UserDto.class);
//        } else {
//            Optional<User> optionalUser = userRepository.findById(userId);
//            if (optionalUser.isEmpty()) {
//                return null;
//            }
//            return modelMapper.map(optionalUser.get(), UserDto.class);
//        }
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return null;
        }
        return modelMapper.map(optionalUser.get(), UserDto.class);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Group> findAllGroupsByUser(User user) {
        User userEntity = userRepository.findById(user.getUserId()).orElseThrow(() -> {
            throw new UserNotFoundException("User not found!!!");
        });
        if (!ObjectUtils.isEmpty(userEntity.getGroups())) {
            Comparator<Group> comparatorGroupId = Comparator.comparing(Group::getGroupId);
            List<Group> groups = userEntity.getGroups().stream().sorted(comparatorGroupId).collect(Collectors.toList());
            return groups;
        }
        return Collections.emptyList();
    }
}
