package com.auth.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDeleteGroupDto {
    private long groupId;
    private long deletedBy;
}
