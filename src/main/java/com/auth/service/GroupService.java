package com.auth.service;

import com.auth.model.dto.AddMemberRequestDto;
import com.auth.model.entity.Group;
import com.auth.model.entity.User;

import java.util.List;

public interface GroupService {
    List<Group> findAll();

    Group save(Group group) throws Exception;

    int deleteById(long id);

    int saveAll(List<Group> groups);

    Group initMember(AddMemberRequestDto request) throws Exception;
    List<User> addMember(AddMemberRequestDto request) throws Exception;

    List<User> getMembersByGroupId(long groupId);
}
