package org.smartcampus.smartcampus_be.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smartcampus.smartcampus_be.domain.actiontoken.service.ActionTokenService;
import org.smartcampus.smartcampus_be.domain.member.entity.Member;
import org.smartcampus.smartcampus_be.domain.member.repository.MemberRepository;
import org.smartcampus.smartcampus_be.domain.notification.dto.request.AlimTalkRequest;
import org.smartcampus.smartcampus_be.domain.notification.dto.response.AlimTalkResponse;
import org.smartcampus.smartcampus_be.domain.outlier.entity.ActionStatus;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLevel;
import org.smartcampus.smartcampus_be.domain.outlier.entity.OutlierLog;
import org.smartcampus.smartcampus_be.domain.sensor.entity.SensorData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final RestClient restClient;
    private final MemberRepository memberRepository;
    private final ActionTokenService actionTokenService;

    @Value("${ustra.alimtalk.api-url}")
    private String apiUrl;

    @Value("${ustra.alimtalk.api-key}")
    private String apiKey;

    @Value("${ustra.alimtalk.serv-no}")
    private String servNo;

    @Value("${ustra.alimtalk.sender-number}")
    private String senderNumber;

    @Value("${ustra.alimtalk.send-key}")
    private String sendKey;

    @Value("${ustra.alimtalk.template-code}")
    private String templateCode;

    @Value("${ustra.alimtalk.enabled:false}")
    private Boolean enabled;

//    @Value("${ustra.alimtalk.receiver-numbers}")
//    private List<String> receiverNumbers;

    @Value("${ustra.alimtalk.test-mode:true}")
    private Boolean testMode;

    /**
     * 이상치 감지 시 알림톡 발송
     */
    public void sendOutlierAlert(OutlierLog outlierLog) {
        if (!enabled) {
            log.info("알림톡 발송이 비활성화되어 있습니다.");
            return;
        }

        // 알림 발송 대상 레벨 확인 (DANGER, EMERGENCY만 발송)
        if (!shouldSendNotification(outlierLog.getLevel())) {
            log.debug("알림 발송 대상이 아닌 레벨입니다: {}", outlierLog.getLevel());
            return;
        }

        try {
            // DB에서 수신자 조회
            List<Member> recipients = getRecipients();
            if (recipients.isEmpty()) {
                log.warn("알림 수신자가 없습니다. (DB에 전화번호가 등록된 관리자가 없음)");
                return;
            }

            AlimTalkRequest request = buildRequest(recipients, outlierLog);

            // 디버깅용 로그 (배포 시 제거 또는 debug 레벨로 변경)
            log.info("알림톡 API 호출 - URL: {}", apiUrl);
            log.info("인증 정보 - apiKey: {}..., servNo: {}",
                    apiKey != null && apiKey.length() > 10 ? apiKey.substring(0, 10) : "null",
                    servNo);

            AlimTalkResponse response = restClient.post()
                    .uri(apiUrl)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("x-ustra-api-key", apiKey)
                    .header("x-ustra-serv-no", servNo)
                    .body(request)
                    .retrieve()
                    .body(AlimTalkResponse.class);

            if (response != null && response.isSuccess()) {
                log.info("알림톡 발송 성공 - API MSG ID: {}", response.getApiMsgId());
            } else {
                log.error("알림톡 발송 실패 - result_code: {}, error_msg: {}",
                        response != null ? response.getResultCode() : "null",
                        response != null ? response.getErrorMsg() : "null");
            }
        } catch (Exception e) {
            log.error("알림톡 발송 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * DB에서 알림 수신자 조회
     */
    private List<Member> getRecipients() {
        return memberRepository.findAll().stream()
                .filter(member -> member.getNotificationEnabled() != null && member.getNotificationEnabled())
                .filter(member -> member.getPhoneNumber() != null && !member.getPhoneNumber().isEmpty())
                .toList();
    }

    /**
     * 알림 발송 대상 레벨인지 확인
     */
    private boolean shouldSendNotification(OutlierLevel level) {
        // DANGER, EMERGENCY 레벨만 알림 발송
        return level == OutlierLevel.DANGER || level == OutlierLevel.EMERGENCY;
    }

    /**
     * 알림톡 메시지 내용 생성
     */
    private String buildAlertMessage(OutlierLog outlierLog, Member member) {
        SensorData sensorData = outlierLog.getSensorData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return String.format(
                "[이상치 %s단계 감지]\n\n" +
                        "%s 관리자님, %s에서 %s의 %s 이상 수치가 감지되었습니다. 발생 시간: %s\n\n" +
                        "신속한 확인 및 조치 부탁드립니다.\n\n" +
                        "본 메시지는 사전에 등록된 시설 안전 관리자에게 발송되는 모니터링 알림입니다.",
                outlierLog.getLevel().getDescription(),
                member.getName(),
                sensorData.getSensor().getRoom().getRoomNumber(),
                sensorData.getDataType().getDisplayName(),
                outlierLog.getValue() + sensorData.getDataType().getUnit(),
                sensorData.getCreatedAt().format(formatter)
        );
    }

    /**
     * AlimTalkRequest 생성
     */
    private AlimTalkRequest buildRequest(List<Member> members, OutlierLog outlierLog) {
        List<AlimTalkRequest.ReceiverInfo> receiverInfoList = new ArrayList<>();

        for (Member member : members) {
            String message = buildAlertMessage(outlierLog, member);

            // 각 수신자별 토큰 생성
            String token = actionTokenService.createToken(outlierLog, member);

            // 3개의 조치 버튼 링크 생성
            AlimTalkRequest.ButtonLink btnPlanned = buildButtonLink(
                    "조치예정",
                    actionTokenService.buildActionUrl(token, ActionStatus.PLANNED)
            );
            AlimTalkRequest.ButtonLink btnInProgress = buildButtonLink(
                    "조치중",
                    actionTokenService.buildActionUrl(token, ActionStatus.IN_PROGRESS)
            );
            AlimTalkRequest.ButtonLink btnCompleted = buildButtonLink(
                    "조치완료",
                    actionTokenService.buildActionUrl(token, ActionStatus.COMPLETED)
            );

            AlimTalkRequest.ReceiverInfo receiverInfo = AlimTalkRequest.ReceiverInfo.builder()
                    .phoneNumber(member.getPhoneNumber())
                    .msgType("KA01")  // 알림톡 일반
                    .tmplCd(templateCode)
                    .talkContent(message)
                    .talkBtnLink1(btnPlanned)
                    .talkBtnLink2(btnInProgress)
                    .talkBtnLink3(btnCompleted)
//                    .useFailOver("N")  // 실패 시 문자 전환
//                    .failOverType("MS02")  // LMS로 전환
//                    .failOverMsgContent(message)  // 동일 내용으로 전환
                    .build();

            receiverInfoList.add(receiverInfo);
        }

        return AlimTalkRequest.builder()
                .senderNumber(senderNumber)
                .sendKey(sendKey)
                .receiverInfo(receiverInfoList)
                .testYn(testMode ? "Y" : "N")
                .build();
    }

    /**
     * 버튼 링크 생성
     */
    private AlimTalkRequest.ButtonLink buildButtonLink(String name, String url) {
        return AlimTalkRequest.ButtonLink.builder()
                .name(name)
                .type("WL")  // 웹링크
                .urlMobile(url)
                .urlPc(url)
                .build();
    }
}