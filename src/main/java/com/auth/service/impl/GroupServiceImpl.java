package com.auth.service.impl;

import com.auth.exception.GroupNotFoundException;
import com.auth.exception.UserNotFoundException;
import com.auth.model.dto.AddMemberRequestDto;
import com.auth.model.dto.UserDto;
import com.auth.model.entity.Group;
import com.auth.model.entity.User;
import com.auth.repository.GroupRepository;
import com.auth.service.GroupService;
import com.auth.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Set;

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

    @Override
    public int deleteById(long id) {
        return 0;
    }

    @Override
    public int saveAll(List<Group> groups) {
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Group addMember(AddMemberRequestDto request) throws Exception {
        try {
            // Validate group
            Group group = groupRepository.findById(request.getGroupId()).orElseThrow(() -> {
                throw new GroupNotFoundException("Group not found!!!");
            });

            // Validate user
            Set<User> members = group.getGroupUsers();
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
}
