package com.modsen.passenger.service.impl;

import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.PassengerListResponse;
import com.modsen.passenger.dto.response.PassengerResponse;
import com.modsen.passenger.exception.PassengerNotFoundException;
import com.modsen.passenger.mapper.PassengerMapper;
import com.modsen.passenger.model.Passenger;
import com.modsen.passenger.repository.PassengerRepository;
import com.modsen.passenger.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PassengerServiceImpl implements PassengerService {
    private final PassengerRepository passengerRepository;
    private final PassengerMapper passengerMapper;

    @Override
    public PassengerResponse findPassengerById(Long passengerId) {
        return passengerRepository.findById(passengerId)
                .map(passengerMapper::toPassengerResponse)
                .orElseThrow(() -> new PassengerNotFoundException(passengerId));
    }

    @Override
    public void deletePassenger(Long passengerId) {
        Optional<Passenger> passenger = passengerRepository.findById(passengerId);
        passenger.ifPresent(passengerRepository::delete);
    }

    @Override
    public PassengerResponse savePassenger(PassengerRequest request) {
        Passenger passenger = passengerMapper.toPassenger(request);
        Passenger savedPassenger = passengerRepository.save(passenger);

        return passengerMapper.toPassengerResponse(savedPassenger);
    }

    @Override
    public PassengerResponse updatePassenger(Long passengerId, PassengerRequest request) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new PassengerNotFoundException(passengerId));
        passenger.setFirstName(request.getFirstName());
        passenger.setSecondName(request.getSecondName());
        passenger.setEmail(request.getEmail());
        passenger.setPhoneNumber(request.getPhoneNumber());
        Passenger updatedPassenger = passengerRepository.save(passenger);

        return passengerMapper.toPassengerResponse(updatedPassenger);
    }

    @Override
    public PassengerListResponse findAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAll();
        List<PassengerResponse> passengerResponses = passengerMapper.toPassengerListResponse(passengers);

        return new PassengerListResponse(passengerResponses);
    }
}
