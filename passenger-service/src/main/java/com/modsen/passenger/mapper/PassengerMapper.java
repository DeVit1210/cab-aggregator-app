package com.modsen.passenger.mapper;

import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.PassengerResponse;
import com.modsen.passenger.model.Passenger;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PassengerMapper {
    PassengerResponse toPassengerResponse(Passenger passenger);

    Passenger toPassenger(PassengerRequest request);

    List<PassengerResponse> toPassengerListResponse(List<Passenger> passengerList);
}
