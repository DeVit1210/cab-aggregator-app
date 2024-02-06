package com.modsen.ride.dto.request;

import com.modsen.ride.constants.PageConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageSettingRequest {
    private int number = PageConstants.SIZE;
    private int size = PageConstants.NUMBER;
    private String sortField = PageConstants.SORT_FIELD;
}
