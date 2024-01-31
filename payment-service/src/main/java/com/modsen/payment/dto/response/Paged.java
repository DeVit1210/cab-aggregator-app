package com.modsen.payment.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Paged<T> {
    private List<T> content;
    private int pageNumber;
    private int totalPageQuantity;
    private int pageSize;
    private boolean hasPrevious;
    private boolean hasNext;
}
