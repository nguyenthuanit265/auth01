package com.auth.controller;

import com.auth.model.dto.AddMemberRequestDto;
import com.auth.model.entity.Group;
import com.auth.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/groups")
public class GroupController {
    private final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("")
    public ResponseEntity<?> createGroup(@RequestBody Group group) throws Exception {
        LOGGER.info("----------------------- function getAllUser() HERE -----------------------");
        return new ResponseEntity<>(groupService.save(group), HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/members")
    public ResponseEntity<?> addMember(@RequestBody AddMemberRequestDto requestAddMember) throws Exception {
        LOGGER.info("----------------------- function addMember() HERE -----------------------");
        return new ResponseEntity<>(groupService.addMember(requestAddMember), HttpStatus.OK);
    }

}
