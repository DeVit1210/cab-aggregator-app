package com.modsen.passenger.mapper;

import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.PagedPassengerResponse;
import com.modsen.passenger.dto.response.PassengerResponse;
import com.modsen.passenger.model.Passenger;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PassengerMapper {
    PassengerResponse toPassengerResponse(Passenger passenger);

    Passenger toPassenger(PassengerRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePassenger(PassengerRequest request, @MappingTarget Passenger passenger);

    List<PassengerResponse> toPassengerListResponse(List<Passenger> passengerList);

    default PagedPassengerResponse toPagedPassengerResponse(Page<Passenger> passengerPage) {
        return PagedPassengerResponse.builder()
                .content(toPassengerListResponse(passengerPage.getContent()))
                .pageNumber(passengerPage.getNumber() + 1)
                .pageSize(passengerPage.getSize())
                .totalPages(passengerPage.getTotalPages())
                .hasPrevious(passengerPage.hasPrevious())
                .hasNext(passengerPage.hasNext())
                .build();
    }
}
