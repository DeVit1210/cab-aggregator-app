package com.modsen.passenger.dto.response;

import com.modsen.passenger.model.Passenger;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedPassengerResponse {
    private List<Passenger> content;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private boolean hasPrevious;
    private boolean hasNext;
}
