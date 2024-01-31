package com.modsen.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageSettingsRequest {
    private int number;
    private int size;
    private String sortField;
}
