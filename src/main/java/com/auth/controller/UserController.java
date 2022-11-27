package com.auth.controller;

import com.auth.model.entity.User;
import com.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUser() {
        LOGGER.info("----------------------- function getAllUser() HERE -----------------------");
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/{userId}/profile", method = RequestMethod.GET)
    public ResponseEntity<?> getUserProfile(@PathVariable("userId") long userId) {
        LOGGER.info("----------------------- function getUserProfile() HERE -----------------------");
        return new ResponseEntity<>(userService.getProfile(userId), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{userId}/groups")
    public ResponseEntity<?> getGroupsOfUser(@PathVariable(value = "userId") long userId) throws Exception {
        LOGGER.info("----------------------- function getGroupsOfUser() HERE -----------------------");
        User user = User.builder().userId(userId).build();
        return new ResponseEntity<>(userService.findAllGroupsByUser(user), HttpStatus.OK);
    }
}
