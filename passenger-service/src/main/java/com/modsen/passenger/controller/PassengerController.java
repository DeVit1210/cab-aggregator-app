package com.modsen.passenger.controller;

import com.modsen.passenger.constants.ServiceMappings;
import com.modsen.passenger.dto.request.PageSettingRequest;
import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.PagedPassengerResponse;
import com.modsen.passenger.dto.response.PassengerListResponse;
import com.modsen.passenger.dto.response.PassengerResponse;
import com.modsen.passenger.service.PassengerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ServiceMappings.PASSENGER_CONTROLLER)
@RequiredArgsConstructor
public class PassengerController {
    private final PassengerService passengerService;

    @GetMapping
    public PassengerListResponse findAllPassengers() {
        return passengerService.findAllPassengers();
    }

    @GetMapping("/page")
    public PagedPassengerResponse findPassengers(PageSettingRequest request) {
        return passengerService.findPassengers(request);
    }

    @GetMapping("/{id}")
    public PassengerResponse findPassengerById(@PathVariable Long id) {
        return passengerService.findPassengerById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponse createPassenger(@Valid @RequestBody PassengerRequest request) {
        return passengerService.savePassenger(request);
    }

    @PutMapping("/{id}")
    public PassengerResponse updatePassenger(@PathVariable Long id, @Valid @RequestBody PassengerRequest request) {
        return passengerService.updatePassenger(id, request);
    }

    @PutMapping("/email/{email}")
    public PassengerResponse updatePassengerByEmail(@PathVariable String email,
                                                    @Valid @RequestBody PassengerRequest request) {
        return passengerService.updatePassengerByEmail(email, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePassenger(@PathVariable Long id) {
        passengerService.deletePassenger(id);
    }
}
