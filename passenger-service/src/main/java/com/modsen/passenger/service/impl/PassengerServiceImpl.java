package com.modsen.passenger.service.impl;

import com.modsen.passenger.dto.request.PageSettingRequest;
import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.AverageRatingResponse;
import com.modsen.passenger.dto.response.PagedPassengerResponse;
import com.modsen.passenger.dto.response.PassengerListResponse;
import com.modsen.passenger.dto.response.PassengerResponse;
import com.modsen.passenger.enums.Role;
import com.modsen.passenger.exception.PassengerNotFoundException;
import com.modsen.passenger.mapper.PassengerMapper;
import com.modsen.passenger.model.Passenger;
import com.modsen.passenger.repository.PassengerRepository;
import com.modsen.passenger.service.PassengerService;
import com.modsen.passenger.service.feign.RatingServiceClient;
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
    private final RatingServiceClient ratingServiceClient;

    @Override
    public PassengerResponse findPassengerById(Long passengerId) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new PassengerNotFoundException(passengerId));
        AverageRatingResponse averageRating = ratingServiceClient.findAverageRating(passengerId, Role.PASSENGER.name());

        return passengerMapper.toPassengerResponse(passenger, averageRating);
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
        AverageRatingResponse averageRating = AverageRatingResponse.empty(savedPassenger.getId());

        return passengerMapper.toPassengerResponse(savedPassenger, averageRating);
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
        List<AverageRatingResponse> allAverageRatings = ratingServiceClient
                .findAllAverageRatings(Role.PASSENGER.name())
                .averageRatingResponses();
        List<PassengerResponse> passengerResponses =
                passengerMapper.toPassengerListResponse(passengers, allAverageRatings);

        return PassengerListResponse.of(passengerResponses);
    }

    @Override
    public PagedPassengerResponse findPassengers(PageSettingRequest request) {
        PageRequest pageRequest = PageRequestUtils.makePageRequest(request);
        Page<Passenger> passengerPage = passengerRepository.findAll(pageRequest);
        PageRequestUtils.validatePageResponse(pageRequest, passengerPage);

        return passengerMapper.toPagedPassengerResponse(passengerPage);
    }

    private PassengerResponse doUpdatePassenger(Passenger passenger, PassengerRequest request) {
        passengerMapper.updatePassenger(request, passenger);
        passengerRepository.save(passenger);
        AverageRatingResponse averageRating =
                ratingServiceClient.findAverageRating(passenger.getId(), Role.PASSENGER.name());

        return passengerMapper.toPassengerResponse(passenger, averageRating);
    }
}
