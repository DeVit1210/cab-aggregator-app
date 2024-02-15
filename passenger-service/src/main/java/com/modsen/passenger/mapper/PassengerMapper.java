package com.modsen.passenger.mapper;

import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.AverageRatingResponse;
import com.modsen.passenger.dto.response.PagedPassengerResponse;
import com.modsen.passenger.dto.response.PassengerResponse;
import com.modsen.passenger.dto.response.ShortPassengerResponse;
import com.modsen.passenger.model.Passenger;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.IntStream;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PassengerMapper {
    Passenger toPassenger(PassengerRequest request);
    @Mapping(target = "id", source = "passenger.id")
    PassengerResponse toPassengerResponse(Passenger passenger, AverageRatingResponse averageRating);

    ShortPassengerResponse toShortPassengerResponse(Passenger passenger);

    List<ShortPassengerResponse> toShortPassengerListResponse(List<Passenger> passengerList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePassenger(PassengerRequest request, @MappingTarget Passenger passenger);

    default List<PassengerResponse> toPassengerListResponse(List<Passenger> passengerList,
                                                            List<AverageRatingResponse> averageRatingList) {
        return IntStream.range(0, passengerList.size())
                .mapToObj(value -> toPassengerResponse(passengerList.get(value), averageRatingList.get(value)))
                .toList();
    }

    default PagedPassengerResponse toPagedPassengerResponse(Page<Passenger> passengerPage) {
        return PagedPassengerResponse.builder()
                .content(toShortPassengerListResponse(passengerPage.getContent()))
                .pageNumber(passengerPage.getNumber() + 1)
                .pageSize(passengerPage.getSize())
                .totalPages(passengerPage.getTotalPages())
                .hasPrevious(passengerPage.hasPrevious())
                .hasNext(passengerPage.hasNext())
                .build();
    }
}
