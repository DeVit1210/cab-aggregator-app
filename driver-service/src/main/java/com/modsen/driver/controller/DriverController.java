package com.modsen.driver.controller;

import com.modsen.driver.constants.ControllerMappings;
import com.modsen.driver.constants.PageConstants;
import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.response.DriverListResponse;
import com.modsen.driver.dto.response.DriverResponse;
import com.modsen.driver.dto.response.PagedDriverResponse;
import com.modsen.driver.service.DriverService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerMappings.DRIVER_CONTROLLER)
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public DriverListResponse findAllDrivers() {
        return driverService.findAllDrivers();
    }

    @GetMapping("/page")
    public PagedDriverResponse findDrivers(@RequestParam(defaultValue = PageConstants.NUMBER) int number,
                                           @RequestParam(defaultValue = PageConstants.SIZE) int size,
                                           @RequestParam(defaultValue = PageConstants.SORT_FIELD) String sortField) {
        return driverService.findDrivers(number, size, sortField);
    }

    @GetMapping("/{id}")
    public DriverResponse findDriverById(@PathVariable Long id) {
        return driverService.findDriverById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse createDriver(@Valid @RequestBody DriverRequest request) {
        return driverService.createDriver(request);
    }

    @PutMapping("/{id}")
    public DriverResponse updateDriver(@PathVariable Long id, @Valid @RequestBody DriverRequest request) {
        return driverService.updateDriver(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
    }
}
