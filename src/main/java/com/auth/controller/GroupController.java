package com.auth.controller;

import com.auth.model.dto.AddMemberRequestDto;
import com.auth.model.dto.RequestDeleteGroupDto;
import com.auth.model.dto.RequestDeleteMemberDto;
import com.auth.model.entity.Group;
import com.auth.service.GroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

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

    @CrossOrigin(origins = "*")
    @PostMapping("/init-members")
    public ResponseEntity<?> initCreateGroup(@RequestBody AddMemberRequestDto requestAddMember) throws Exception {
        LOGGER.info("----------------------- function initCreateGroup() HERE -----------------------");
        return new ResponseEntity<>(groupService.initMember(requestAddMember), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getMembersByGroupId(@PathVariable("groupId") Long groupId) throws Exception {
        LOGGER.info("----------------------- function addMember() HERE -----------------------");
        return new ResponseEntity<>(groupService.getMembersByGroupId(groupId), HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/{groupId}/delete")
    public ResponseEntity<?> deleteGroupByGroupId(@PathVariable("groupId") Long groupId, @RequestBody RequestDeleteGroupDto request) throws Exception {
        LOGGER.info("----------------------- function deleteGroupByGroupId() HERE -----------------------");
        request.setGroupId(groupId);
        return groupService.deleteById(request);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/{groupId}/members/{memberId}/delete")
    public ResponseEntity<?> deleteGroupByGroupId(@PathVariable("groupId") Long groupId, @PathVariable("memberId") Long memberId, @RequestBody RequestDeleteMemberDto request) throws Exception {
        LOGGER.info("----------------------- function deleteGroupByGroupId() HERE -----------------------");
        request.setGroupId(groupId);
        request.setMemberId(memberId);
        return groupService.deleteMemberOfGroup(request);
    }

//    @CrossOrigin(origins = "*")
//    @DeleteMapping("/delete")
//    public ResponseEntity<?> deleteAllGroup(@PathVariable("groupId") Long groupId) throws Exception {
//        LOGGER.info("----------------------- function deleteGroupByGroupId() HERE -----------------------");
//        return new ResponseEntity<>(groupService.deleteById(groupId), HttpStatus.OK);
//    }
}
