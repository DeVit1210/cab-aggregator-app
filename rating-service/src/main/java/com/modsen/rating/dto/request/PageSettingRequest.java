package com.modsen.rating.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageSettingRequest {
    private int number;
    private int size;
    private String sortField;
}
