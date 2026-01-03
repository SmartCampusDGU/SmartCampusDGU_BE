package org.smartcampus.smartcampus_be.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberListResponseDto {
    private Long id;
    private String username;
    private String name;
    private String description;
    private String phoneNumber;
    private Boolean notificationEnabled;
}