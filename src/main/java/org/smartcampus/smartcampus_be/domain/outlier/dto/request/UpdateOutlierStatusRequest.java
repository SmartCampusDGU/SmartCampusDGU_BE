package org.smartcampus.smartcampus_be.domain.outlier.dto.request;

import lombok.Getter;
import org.smartcampus.smartcampus_be.domain.outlier.entity.ActionStatus;
import org.smartcampus.smartcampus_be.domain.outlier.entity.CheckStatus;

@Getter
public class UpdateOutlierStatusRequest {
    private CheckStatus checkStatus;
    private ActionStatus actionStatus;
}