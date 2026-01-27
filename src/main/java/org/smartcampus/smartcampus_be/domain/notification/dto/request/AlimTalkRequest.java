package org.smartcampus.smartcampus_be.domain.notification.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AlimTalkRequest {

    @JsonProperty("sender_number")
    private String senderNumber;

    @JsonProperty("send_key")
    private String sendKey;

    @JsonProperty("receiver_info")
    private List<ReceiverInfo> receiverInfo;

    @JsonProperty("test_yn")
    private String testYn;

    @Getter
    @Builder
    public static class ReceiverInfo {

        @JsonProperty("phone_number")
        private String phoneNumber;

        @JsonProperty("msg_type")
        private String msgType;  // KA01: 알림톡 일반, KA02: 알림톡 이미지형

        @JsonProperty("tmpl_cd")
        private String tmplCd;

        @JsonProperty("talk_content")
        private String talkContent;

//        @JsonProperty("use_fail_over")
//        private String useFailOver;  // Y/N

//        @JsonProperty("fail_over_type")
//        private String failOverType;  // MS01: SMS, MS02: LMS

//        @JsonProperty("fail_over_msg_content")
//        private String failOverMsgContent;

        @JsonProperty("talk_btn_link1")
        private ButtonLink talkBtnLink1;

        @JsonProperty("talk_btn_link2")
        private ButtonLink talkBtnLink2;

        @JsonProperty("talk_btn_link3")
        private ButtonLink talkBtnLink3;
    }

    @Getter
    @Builder
    public static class ButtonLink {

        @JsonProperty("name")
        private String name;

        @JsonProperty("type")
        private String type;  // WL: 웹링크, AL: 앱링크

        @JsonProperty("url_mobile")
        private String urlMobile;

        @JsonProperty("url_pc")
        private String urlPc;
    }
}