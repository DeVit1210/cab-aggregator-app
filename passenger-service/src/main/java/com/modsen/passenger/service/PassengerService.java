package com.modsen.passenger.service;

import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.PassengerListResponse;
import com.modsen.passenger.dto.response.PassengerResponse;

public interface PassengerService {
    PassengerResponse findPassengerById(Long passengerId);

    void deletePassenger(Long passengerId);

    PassengerResponse savePassenger(PassengerRequest request);

    PassengerResponse updatePassenger(Long passengerId, PassengerRequest request);

    PassengerListResponse findAllPassengers();

}
