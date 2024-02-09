package com.modsen.ride.dto.request;

import com.modsen.ride.constants.PageConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageSettingRequest {
    private int number = PageConstants.NUMBER;
    private int size = PageConstants.SIZE;
    private String sortField = PageConstants.SORT_FIELD;
}
