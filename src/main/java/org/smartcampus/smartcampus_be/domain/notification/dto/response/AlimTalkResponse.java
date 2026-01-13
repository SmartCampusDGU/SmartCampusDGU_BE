package org.smartcampus.smartcampus_be.domain.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlimTalkResponse {

    @JsonProperty("result_code")
    private Integer resultCode;

    @JsonProperty("api_msg_id")
    private String apiMsgId;

    @JsonProperty("error_msg")
    private String errorMsg;

    public boolean isSuccess() {
        return resultCode != null && resultCode == 0;
    }
}