package org.smartcampus.smartcampus_be.domain.actiontoken.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;
import org.smartcampus.smartcampus_be.global.common.domain.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "action_token")
public class ActionToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String token;  // UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outlier_log_id", nullable = false)
    private OutlierLog outlierLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 알림톡 수신자

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean used = false;

    private LocalDateTime usedAt;

    @Builder
    public ActionToken(String token, OutlierLog outlierLog, Member member, LocalDateTime expiresAt) {
        this.token = token;
        this.outlierLog = outlierLog;
        this.member = member;
        this.expiresAt = expiresAt;
        this.used = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void markAsUsed() {
        this.used = true;
        this.usedAt = LocalDateTime.now();
    }
}
