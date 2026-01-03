package org.smartcampus.smartcampus_be.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;
import org.smartcampus.smartcampus_be.global.common.domain.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String username;  // 아이디

    @Column(nullable = false)
    private String password;  // 암호화된 비밀번호

    @Column(nullable = false)
    private String name; // 계정명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(length = 50)
    private String description; // 설명

    @Column(length = 20)
    private String phoneNumber; // 전화번호 (01012345678)

    @Column(nullable = false)
    private Boolean notificationEnabled = true; // 알림 수신 여부

    @Builder
    public Member(String username, String password, String name, Role role, String description,
                  String phoneNumber, Boolean notificationEnabled) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.notificationEnabled = notificationEnabled != null ? notificationEnabled : true;
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OutlierLog> outlierLogs = new ArrayList<>();

    public void update(String password, String name, String description, String phoneNumber, Boolean notificationEnabled) {
        this.password = password;
        this.name = name;
        this.description = description;
        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }
        if (notificationEnabled != null) {
            this.notificationEnabled = notificationEnabled;
        }
    }
}
