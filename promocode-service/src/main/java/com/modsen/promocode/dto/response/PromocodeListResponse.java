package com.modsen.promocode.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PromocodeListResponse {
    private List<PromocodeResponse> promocodes;
    private int quantity;

    public static PromocodeListResponse of(List<PromocodeResponse> promocodes) {
        return new PromocodeListResponse(promocodes, promocodes.size());
    }
}
