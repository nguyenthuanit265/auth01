package com.auth.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDeleteMemberDto {
    private long memberId;
    private long groupId;
    private long deletedBy;
}
