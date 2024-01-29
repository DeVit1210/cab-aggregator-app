package com.modsen.passenger.service.impl;

import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.PagedPassengerResponse;
import com.modsen.passenger.dto.response.PassengerListResponse;
import com.modsen.passenger.dto.response.PassengerResponse;
import com.modsen.passenger.exception.PassengerNotFoundException;
import com.modsen.passenger.mapper.PassengerMapper;
import com.modsen.passenger.model.Passenger;
import com.modsen.passenger.repository.PassengerRepository;
import com.modsen.passenger.service.PassengerService;
import com.modsen.passenger.utils.PageRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        passenger.ifPresentOrElse(passengerRepository::delete, () -> {
            throw new PassengerNotFoundException(passengerId);
        });
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

        return doUpdatePassenger(passenger, request);
    }

    @Override
    public PassengerResponse updatePassengerByEmail(String email, PassengerRequest request) {
        Passenger passenger = passengerRepository.findByEmail(email)
                .orElseThrow(() -> new PassengerNotFoundException(email));

        return doUpdatePassenger(passenger, request);
    }

    @Override
    public PassengerListResponse findAllPassengers() {
        List<Passenger> passengers = passengerRepository.findAll();
        List<PassengerResponse> passengerResponses = passengerMapper.toPassengerListResponse(passengers);

        return new PassengerListResponse(passengerResponses);
    }

    @Override
    public PagedPassengerResponse findPassengers(int pageNumber, int pageSize, String sortField) {
        PageRequest pageRequest = PageRequestUtils.makePageRequest(pageNumber, pageSize, sortField);
        Page<Passenger> passengerPage = passengerRepository.findAll(pageRequest);
        PageRequestUtils.validatePageResponse(pageRequest, passengerPage);

        return passengerMapper.toPagedPassengerResponse(passengerPage);
    }

    private PassengerResponse doUpdatePassenger(Passenger passenger, PassengerRequest request) {
        passenger.setFirstName(request.getFirstName());
        passenger.setLastName(request.getLastName());
        passenger.setEmail(request.getEmail());
        passenger.setPhoneNumber(request.getPhoneNumber());
        Passenger updatedPassenger = passengerRepository.save(passenger);

        return passengerMapper.toPassengerResponse(updatedPassenger);
    }
}
