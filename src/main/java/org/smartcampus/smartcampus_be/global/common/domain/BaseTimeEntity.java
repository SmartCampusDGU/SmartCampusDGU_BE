package org.smartcampus.smartcampus_be.global.common.domain;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate // 엔티티 생성시 자동으로 값 설정
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티 값 변경시 자동으로 값 갱신
    private LocalDateTime updatedAt;

}
