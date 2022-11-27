package com.auth.service;

import com.auth.model.dto.AddMemberRequestDto;
import com.auth.model.dto.UserDto;
import com.auth.model.dto.UserResponseDto;
import com.auth.model.entity.Group;
import com.auth.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface GroupService {
    List<Group> findAll();

    Group save(Group group) throws Exception;

    int deleteById(long id);

    int saveAll(List<Group> groups);

    Group addMember(AddMemberRequestDto request) throws Exception;
}
