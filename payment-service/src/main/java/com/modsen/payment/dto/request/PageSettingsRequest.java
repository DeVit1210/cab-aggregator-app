package com.modsen.payment.dto.request;

import com.modsen.payment.constants.PageConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageSettingsRequest {
    private int number = PageConstants.NUMBER;
    private int size = PageConstants.SIZE;
    private String sortField = PageConstants.SORT_FIELD;
}
