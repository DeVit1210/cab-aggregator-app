package com.modsen.driver.component;

import com.modsen.driver.constants.TestConstants;
import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.FindDriverRequest;
import com.modsen.driver.dto.request.UpdateRideDriverRequest;
import com.modsen.driver.dto.response.DriverAvailabilityResponse;
import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.kafka.producer.RideResponseProducer;
import com.modsen.driver.mapper.RideResponseMapper;
import com.modsen.driver.model.Driver;
import com.modsen.driver.repository.DriverRepository;
import com.modsen.driver.service.DriverWithSuggestedRideService;
import com.modsen.driver.service.impl.DriverServiceImpl;
import com.modsen.driver.utils.TestUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DriverServiceStepDefinitions {
    private final Long driverId = TestConstants.DRIVER_ID;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private DriverWithSuggestedRideService suggestedRideService;
    @Mock
    private RideResponseMapper rideResponseMapper;
    @Mock
    private RideResponseProducer rideResponseProducer;
    @InjectMocks
    private DriverServiceImpl driverService;

    private DriverAvailabilityResponse availabilityResponse;
    private ChangeDriverStatusRequest changeDriverStatusRequest;
    private UpdateRideDriverRequest updateRideDriverRequest;
    private Driver driver;

    public DriverServiceStepDefinitions() {
        MockitoAnnotations.openMocks(this);
    }


    @Given("Total driver count is {long} and available driver count is {long}")
    public void givenTotalDriverCountAndAvailableDriverCount(long totalCount, long availableCount) {
        when(driverRepository.count())
                .thenReturn(totalCount);
        when(driverRepository.countAllByDriverStatus(any(DriverStatus.class)))
                .thenReturn(availableCount);
    }

    @When("The business logic for retrieving driver availability is invoked")
    public void theBusinessLogicForRetrievingDriverAvailabilityIsInvoked() {
        availabilityResponse = driverService.getDriverAvailability();
    }

    @Then("The response should contain {long} available drivers and {long} total drivers")
    public void theResponseShouldContainAvailableDriversAndTotalDrivers(long expectedAvailableCount,
                                                                        long expectedTotalCount) {
        assertThat(availabilityResponse.availableDriverCount())
                .isEqualTo(expectedAvailableCount);
        assertThat(availabilityResponse.totalDriverCount())
                .isEqualTo(expectedTotalCount);
    }

    @Given("Valid request to change driver status to {string}")
    public void validRequestToChangeDriverStatusTo(String newDriverStatusName) {
        changeDriverStatusRequest = ChangeDriverStatusRequest.builder()
                .driverStatus(DriverStatus.valueOf(newDriverStatusName))
                .driverId(driverId)
                .build();
    }

    @When("The business logic for changing driver status from {string} is invoked")
    public void theBusinessLogicForChangingDriverStatusFromIsInvoked(String oldDriverStatusName) {
        driver = TestUtils.driverWithStatus(DriverStatus.valueOf(oldDriverStatusName));
        when(driverRepository.findById(anyLong()))
                .thenReturn(Optional.of(driver));

        driverService.handleChangeDriverStatusRequest(changeDriverStatusRequest);
    }

    @Then("The driver should be with the new status of {string}")
    public void theDriverShouldBeWithTheNewStatusOf(String expectedNewDriverStatus) {
        assertThat(driver.getDriverStatus())
                .isEqualTo(DriverStatus.valueOf(expectedNewDriverStatus));
        verify(driverRepository).findById(driverId);
    }

    @Given("Available driver for a ride exists")
    public void availableDriverForARideExists() {
        updateRideDriverRequest = UpdateRideDriverRequest.builder()
                .driverId(driverId)
                .rideId(TestConstants.RIDE_ID)
                .isDriverAvailable(true)
                .build();
    }

    @When("The business logic for finding a driver for a ride is invoked")
    public void theBusinessLogicForFindingADriverForARideIsInvoked() {
        FindDriverRequest findDriverRequest = new FindDriverRequest(TestConstants.RIDE_ID);
        driver = TestUtils.defaultDriver();

        when(suggestedRideService.getDriverIdList(any()))
                .thenReturn(Collections.emptyList());
        when(driverRepository.findFirstByDriverStatus(any()))
                .thenReturn(Optional.of(driver));
        doNothing().when(suggestedRideService)
                .save(any(), anyLong());
        when(rideResponseMapper.toResponseWithDriver(any(FindDriverRequest.class), anyLong()))
                .thenReturn(updateRideDriverRequest);
        doNothing().when(rideResponseProducer)
                .sendUpdateRideRequest(any());

        driverService.handleFindDriverRequest(findDriverRequest);
    }

    @When("The business logic for unsuccessful finding a driver for a ride is invoked")
    public void theBusinessLogicForUnsuccessfulFindingADriverForARideIsInvoked() {
        FindDriverRequest findDriverRequest = new FindDriverRequest(TestConstants.RIDE_ID);
        driver = TestUtils.defaultDriver();

        when(suggestedRideService.getDriverIdList(any()))
                .thenReturn(Collections.emptyList());
        when(driverRepository.findFirstByDriverStatus(any()))
                .thenReturn(Optional.empty());
        when(rideResponseMapper.toResponseWithoutDriver(any()))
                .thenReturn(updateRideDriverRequest);
        doNothing().when(rideResponseProducer)
                .sendUpdateRideRequest(any());

        driverService.handleFindDriverRequest(findDriverRequest);
    }

    @Then("The ride response should be produced with isAvailable is true")
    public void theRideResponseShouldBeProducedWithIsAvailableIsTrue() {
        assertThat(driver.getDriverStatus())
                .isEqualTo(DriverStatus.HAS_UNCONFIRMED_RIDE);
        assertThat(updateRideDriverRequest.isDriverAvailable())
                .isTrue();
    }

    @And("Methods needed to successfully handle request to find driver for a ride were called")
    public void methodsNeededToSuccessfullyHandleRequestToFindDriverForARideWereCalled() {
        verify(suggestedRideService).getDriverIdList(any());
        verify(driverRepository).findFirstByDriverStatus(DriverStatus.AVAILABLE);
        verify(suggestedRideService).save(driver, TestConstants.RIDE_ID);
        verify(rideResponseMapper).toResponseWithDriver(any(), anyLong());
        verify(rideResponseMapper, never()).toResponseWithoutDriver(any());
        verify(rideResponseProducer).sendUpdateRideRequest(updateRideDriverRequest);
    }

    @Given("No available drivers for a ride")
    public void noAvailableDriversForARide() {
        updateRideDriverRequest = UpdateRideDriverRequest.builder()
                .driverId(null)
                .rideId(TestConstants.RIDE_ID)
                .isDriverAvailable(false)
                .build();
    }

    @Then("The ride response should be produced with isAvailable is false")
    public void theRideResponseShouldBeProducedWithIsAvailableIsFalse() {
        assertThat(updateRideDriverRequest.isDriverAvailable())
                .isFalse();
    }

    @And("Methods needed to unsuccessfully handle request to find driver for a ride were called")
    public void methodsNeededToUnsuccessfullyHandleRequestToFindDriverForARideWereCalled() {
        verify(suggestedRideService).getDriverIdList(any());
        verify(driverRepository).findFirstByDriverStatus(DriverStatus.AVAILABLE);
        verify(rideResponseMapper).toResponseWithoutDriver(any());
        verify(rideResponseMapper, never()).toResponseWithDriver(any(), anyLong());
        verify(rideResponseProducer).sendUpdateRideRequest(updateRideDriverRequest);
    }

    @And("Methods needed to change driver status from api endpoint were called")
    public void methodsNeededToChangeDriverStatusFromApiEndpointWereCalled() {
        verify(driverRepository).findById(driverId);
        verify(driverRepository).save(driver);
    }
}
