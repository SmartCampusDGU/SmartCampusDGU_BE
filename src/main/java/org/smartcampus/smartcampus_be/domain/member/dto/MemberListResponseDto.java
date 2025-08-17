package org.smartcampus.smartcampus_be.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberListResponseDto {
    private String username;
    private String password;
    private String name;
    private String description;
}