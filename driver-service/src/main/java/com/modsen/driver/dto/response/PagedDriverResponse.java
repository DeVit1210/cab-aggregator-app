package com.modsen.driver.dto.response;

import com.modsen.driver.model.Driver;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedDriverResponse {
    private List<Driver> content;
    private int pageNumber;
    private int pageSize;
    private int totalPageCount;
    private boolean hasPrevious;
    private boolean hasNext;
}
