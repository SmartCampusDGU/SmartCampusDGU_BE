package org.smartcampus.smartcampus_be.domain.actiontoken.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.outlier.entity.ActionStatus;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;

import java.time.LocalDateTime;

@Getter
@Builder
public class ActionResponse {

    private Long outlierLogId;
    private String memberName;
    private String actionStatus;
    private String actionStatusDescription;
    private LocalDateTime updatedAt;

    public static ActionResponse of(OutlierLog outlierLog, Member member, ActionStatus status) {
        return ActionResponse.builder()
                .outlierLogId(outlierLog.getId())
                .memberName(member.getName())
                .actionStatus(status.name())
                .actionStatusDescription(status.getDescription())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
