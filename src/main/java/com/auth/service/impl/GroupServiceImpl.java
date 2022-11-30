package com.auth.service.impl;

import com.auth.exception.GroupNotFoundException;
import com.auth.exception.UserNotFoundException;
import com.auth.model.dto.AddMemberRequestDto;
import com.auth.model.dto.RequestDeleteGroupDto;
import com.auth.model.dto.RequestDeleteMemberDto;
import com.auth.model.dto.UserDto;
import com.auth.model.entity.Group;
import com.auth.model.entity.User;
import com.auth.repository.GroupRepository;
import com.auth.service.GroupService;
import com.auth.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {
    private final Logger LOGGER = LoggerFactory.getLogger(GroupServiceImpl.class);
    private final GroupRepository groupRepository;
    private final UserService userService;

    public GroupServiceImpl(GroupRepository groupRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.userService = userService;
    }

    @Override
    public List<Group> findAll() {
        return null;
    }

    @Override
    public Group save(Group group) throws Exception {
        try {
            // Validate group name
            if (ObjectUtils.isEmpty(group.getGroupName())) {
                throw new IllegalArgumentException("group name is not empty");
            }

            // Default user create group is admin
//            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication();
//            if (!ObjectUtils.isEmpty(userDetails)) {
//                UserDto user = userService.findByEmail(userDetails.getUsername()).orElseThrow(() -> {
//                    throw new UserNotFoundException("User is not exist!!!");
//                });
//                group.setAdminId(user.getUserId());
//            } else if (ObjectUtils.isEmpty(group.getAdminId())) {
//                group.setAdminId(0L);
//            }
            if (ObjectUtils.isEmpty(group.getAdminId())) {
                group.setAdminId(0L);
            }

            // Save entity
            return groupRepository.save(group);
        } catch (Exception ex) {
            LOGGER.error("Exception GroupServiceImpl>save. Error: {}", ex.getMessage(), ex);
            throw new Exception(ex.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteById(long id) throws Exception {
        try {
            if (id > 0) {
                groupRepository.deleteById(id);
                return 1;
            }
            return 0;
        } catch (Exception ex) {
            LOGGER.error("Exception GroupServiceImpl>deleteById. Error: {}", ex.getMessage(), ex);
            throw new Exception(ex.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseEntity<?> deleteById(RequestDeleteGroupDto request) throws Exception {
        Group group = groupRepository.findById(request.getGroupId()).orElseThrow(() -> {
            throw new GroupNotFoundException("Group not found!!!");
        });
        if (group.getAdminId() != 0 && group.getAdminId() != request.getDeletedBy()) {
            return new ResponseEntity<>("User is not admin of group!!!", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(deleteById(request.getGroupId()), HttpStatus.OK);
    }

    @Override
    public int saveAll(List<Group> groups) {
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Group initMember(AddMemberRequestDto request) throws Exception {
        try {
            // Validate group
            Group group = groupRepository.findById(request.getGroupId()).orElseThrow(() -> {
                throw new GroupNotFoundException("Group not found!!!");
            });

            // Validate user
            Set<User> members = group.getMembers();
            UserDto user = userService.findById(request.getUserId()).orElseThrow(() -> {
                throw new UserNotFoundException("User not found!!!");
            });

            // Add member
            ModelMapper modelMapper = new ModelMapper();
            boolean isExist = members.stream().anyMatch(member -> member.getUserId().equals(user.getUserId()));
            if (!isExist) {
                members.add(modelMapper.map(user, User.class));
                groupRepository.save(group);
            }

            // save or update group
            return group;
        } catch (Exception ex) {
            LOGGER.error("Exception GroupServiceImpl>addMember. Error: {}", ex.getMessage(), ex);
            throw new Exception(ex.getMessage());
        }
    }


    @Transactional(readOnly = true)
    @Override
    public List<User> getMembersByGroupId(long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> {
            throw new GroupNotFoundException("Group not found!!!");
        });

        Comparator<User> comparatorUserId = Comparator.comparing(User::getUserId);
        List<User> members = group.getMembers().stream().sorted(comparatorUserId).collect(Collectors.toList());
        return members;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<User> addMember(AddMemberRequestDto request) throws Exception {
        // Validate group
        Group group = groupRepository.findById(request.getGroupId()).orElseThrow(() -> {
            throw new GroupNotFoundException("Group not found!!!");
        });

        // If user is exist in db
        Set<User> members = group.getMembers();
        Optional<UserDto> optUser = userService.findByEmail(request.getEmail());
        Group responseGroup = new Group();
        ModelMapper modelMapper = new ModelMapper();
        if (optUser.isPresent() && optUser.get().getUserId() > 0) {
            // Add member
            modelMapper = new ModelMapper();
            boolean isExist = members.stream().anyMatch(member -> member.getUserId().equals(optUser.get().getUserId()));
            if (!isExist) {
                members.add(modelMapper.map(optUser.get(), User.class));
                responseGroup = groupRepository.save(group);
            }
        } else {
            String encodePass = "abcdef".concat("123456").concat("abcdef");
            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(encodePass)
                    .build();
            UserDto savedUser = userService.save(modelMapper.map(user, UserDto.class));
            if (!ObjectUtils.isEmpty(savedUser) && savedUser.getUserId() > 0) {
                members.add(modelMapper.map(savedUser, User.class));
                responseGroup = groupRepository.save(group);
            }
        }

        return new ArrayList<>(responseGroup.getMembers());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseEntity<?> deleteMemberOfGroup(RequestDeleteMemberDto request) {
        // Check group is exist
        Group group = groupRepository.findById(request.getGroupId()).orElseThrow(() -> {
            throw new GroupNotFoundException("Group not found!!!");
        });

        // Check permission user delete member
        if (group.getAdminId() != 0 && group.getAdminId() != request.getDeletedBy()) {
            return new ResponseEntity<>("User don't have permission delete member!!!", HttpStatus.BAD_REQUEST);
        }

        // Check member is deleted: member or admin of group
        if (group.getAdminId() != 0 && group.getAdminId() == request.getDeletedBy() && request.getDeletedBy() == request.getMemberId()) {
            return new ResponseEntity<>("Admin cannot delete yourself!!!", HttpStatus.BAD_REQUEST);
        }

        // Check member is exists in group
        boolean isExist = group.getMembers().stream().anyMatch(member -> member.getUserId() == request.getMemberId());
        if (isExist) {
            group.getMembers().removeIf(member -> member.getUserId() == request.getMemberId());
            return new ResponseEntity<>(group, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User is not exists in group", HttpStatus.BAD_REQUEST);
        }

    }

}
