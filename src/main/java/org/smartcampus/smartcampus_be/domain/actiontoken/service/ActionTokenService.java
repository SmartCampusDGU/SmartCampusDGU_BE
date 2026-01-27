package org.smartcampus.smartcampus_be.domain.actiontoken.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smartcampus.smartcampus_be.domain.actiontoken.dto.response.ActionResponse;
import org.smartcampus.smartcampus_be.domain.actiontoken.entity.ActionToken;
import org.smartcampus.smartcampus_be.domain.actiontoken.repository.ActionTokenRepository;
import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.outlier.entity.ActionStatus;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;
import org.smartcampus.smartcampus_be.domain.outlier.repository.OutlierLogRepository;
import org.smartcampus.smartcampus_be.global.exception.CustomException;
import org.smartcampus.smartcampus_be.global.exception.ErrorType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionTokenService {

    private final ActionTokenRepository actionTokenRepository;
    private final OutlierLogRepository outlierLogRepository;

    @Value("${action-token.expiration-hours:24}")
    private int expirationHours;

    @Value("${action-token.base-url:https://smart-campus-dgu.kro.kr}")
    private String baseUrl;

    /**
     * 토큰 생성
     * 동일한 (OutlierLog, Member) 조합에 대해 사용되지 않은 토큰이 있으면 해당 토큰 반환
     * 없으면 새로 생성
     */
    @Transactional
    public String createToken(OutlierLog outlierLog, Member member) {
        // 기존에 사용되지 않은 토큰이 있는지 확인
        return actionTokenRepository.findByOutlierLogAndMember(outlierLog, member)
                .filter(token -> !token.getUsed() && !token.isExpired())
                .map(ActionToken::getToken)
                .orElseGet(() -> createNewToken(outlierLog, member));
    }

    private String createNewToken(OutlierLog outlierLog, Member member) {
        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(expirationHours);

        ActionToken actionToken = ActionToken.builder()
                .token(tokenValue)
                .outlierLog(outlierLog)
                .member(member)
                .expiresAt(expiresAt)
                .build();

        actionTokenRepository.save(actionToken);
        log.info("새 액션 토큰 생성 - outlierLogId: {}, memberId: {}, token: {}",
                outlierLog.getId(), member.getId(), tokenValue);

        return tokenValue;
    }

    /**
     * 토큰 검증 및 조치자 등록
     */
    @Transactional
    public ActionResponse validateAndUseToken(String token, ActionStatus status) {
        ActionToken actionToken = actionTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(ErrorType.TOKEN_NOT_FOUND));

        // 만료 확인
        if (actionToken.isExpired()) {
            throw new CustomException(ErrorType.TOKEN_EXPIRED);
        }

        // 이미 사용된 토큰인지 확인
        if (actionToken.getUsed()) {
            throw new CustomException(ErrorType.TOKEN_ALREADY_USED);
        }

        // 토큰 사용 처리
        actionToken.markAsUsed();

        // OutlierLog에 조치자 등록 및 상태 업데이트
        OutlierLog outlierLog = actionToken.getOutlierLog();
        Member member = actionToken.getMember();

        outlierLog.assignMember(member);
        outlierLog.updateActionStatus(status);

        // COMPLETED인 경우 완료 시간 기록
        if (status == ActionStatus.COMPLETED) {
            outlierLog.markAsCompleted();
        }

        outlierLogRepository.save(outlierLog);

        log.info("조치자 등록 완료 - outlierLogId: {}, memberId: {}, memberName: {}, status: {}",
                outlierLog.getId(), member.getId(), member.getName(), status);

        return ActionResponse.of(outlierLog, member, status);
    }

    /**
     * 조치 링크 URL 생성
     */
    public String buildActionUrl(String token, ActionStatus status) {
        return String.format("%s/api/action/%s?status=%s", baseUrl, token, status.name());
    }
}
