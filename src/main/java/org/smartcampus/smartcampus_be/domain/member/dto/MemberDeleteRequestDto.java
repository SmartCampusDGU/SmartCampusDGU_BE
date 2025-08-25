package org.smartcampus.smartcampus_be.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemberDeleteRequestDto {

    @NotEmpty(message = "삭제할 계정 리스트는 필수입니다.")
    private List<Long> ids;
}
