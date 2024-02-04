package com.modsen.promocode.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageSettingRequest {
    private int size;
    private int number;
    private String sortField;
}
