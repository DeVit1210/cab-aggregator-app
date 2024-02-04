package com.modsen.promocode.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppliedPromocodeResponse {
    private Long id;
    private Long passengerId;
    private Long promocodeId;
    private String promocodeName;
    private int discountPercent;
}
