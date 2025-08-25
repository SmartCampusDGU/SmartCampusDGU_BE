package org.smartcampus.smartcampus_be.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberUpdateRequestDto {

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>,.?/]).{8,20}$",
        message = "비밀번호는 영어, 숫자, 특수기호를 포함해 8~20자여야 합니다."
    )
    private String password;

    @NotBlank(message = "계정명은 필수입니다.")
    private String name;

    private String description;
}
