package org.smartcampus.smartcampus_be.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false, unique = true)
    private String username;  // 아이디

    @Column(nullable = false)
    private String password;  // 암호화된 비밀번호

    @Column(nullable = false)
    private String name; // 계정명

    private String description; // 설명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public void update(String password, String name, String description) {
        this.password = password;
        this.name = name;
        this.description = description;
    }
}
