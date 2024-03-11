package com.modsen.ride.component;

import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.RideRequest;
import com.modsen.ride.dto.request.UpdateRideDriverRequest;
import com.modsen.ride.dto.response.RideListResponse;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.dto.response.ShortRideResponse;
import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.enums.Role;
import com.modsen.ride.kafka.producer.RideRequestProducer;
import com.modsen.ride.mapper.RideMapperImpl;
import com.modsen.ride.model.Ride;
import com.modsen.ride.repository.RideRepository;
import com.modsen.ride.service.feign.PassengerServiceClient;
import com.modsen.ride.service.feign.PaymentServiceClient;
import com.modsen.ride.service.impl.RideServiceImpl;
import com.modsen.ride.utils.TestUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RideServiceStepDefinitions {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private RideMapperImpl rideMapper;
    @Mock
    private RideRequestProducer rideRequestProducer;
    @Mock
    private PassengerServiceClient passengerServiceClient;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @InjectMocks
    private RideServiceImpl rideService;

    private Ride ride;
    private RideListResponse rideListResponse;
    private ShortRideResponse shortRideResponse;
    private RideResponse rideResponse;
    private RideRequest rideRequest;
    private UpdateRideDriverRequest updateRideDriverRequest;

    public RideServiceStepDefinitions() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("{int} finished rides exists for person")
    public void finishedRidesExistsForPerson(int ridesQuantity) {
        List<Ride> rideList = Collections.nCopies(ridesQuantity, TestUtils.defaultRide());
        List<RideResponse> rideListResponse = Collections.nCopies(3, TestUtils.defaultRideResponse());

        when(rideRepository.findAllByDriverIdAndRideStatus(anyLong(), any()))
                .thenReturn(rideList);
        when(rideRepository.findAllByPassengerIdAndRideStatus(anyLong(), any()))
                .thenReturn(rideList);
        when(rideMapper.toRideListResponse(anyList()))
                .thenReturn(rideListResponse);
    }

    @When("{string} search for his ride history")
    public void businessLogicToRetrieveAllRidesForPersonIsInvoked(String roleName) {
        rideListResponse = rideService.findAllRidesForPerson(TestConstants.DRIVER_ID, Role.valueOf(roleName));
    }

    @Then("Ride list response should contain {int} rides")
    public void rideListResponseShouldContainRides(int expectedRidesQuantity) {
        assertThat(rideListResponse.getRides())
                .hasSize(expectedRidesQuantity);
    }

    @Given("No finished rides for person")
    public void noFinishedRidesForPerson() {
        when(rideRepository.findAllByDriverIdAndRideStatus(anyLong(), any()))
                .thenReturn(Collections.emptyList());
        when(rideRepository.findAllByPassengerIdAndRideStatus(anyLong(), any()))
                .thenReturn(Collections.emptyList());
    }

    @And("Methods needed to retrieve all rides for driver were called")
    public void methodsNeededToRetrieveAllRidesForDriverWereCalled() {
        verify(rideRepository).findAllByDriverIdAndRideStatus(anyLong(), any());
        verify(rideRepository, never()).findAllByPassengerIdAndRideStatus(anyLong(), any());
    }

    @Given("Available ride for driver exists")
    public void availableRideForDriverExists() {
        ride = TestUtils.rideWithStatus(RideStatus.WAITING_FOR_DRIVER_CONFIRMATION);
    }

    @When("Driver search for any available ride")
    public void businessLogicToFindAvailableRideForDriverIsInvoked() {
        when(rideRepository.findFirstByDriverIdAndRideStatus(anyLong(), any(RideStatus.class)))
                .thenReturn(Optional.of(ride));
        when(rideMapper.toShortRideResponse(any()))
                .thenCallRealMethod();

        shortRideResponse = rideService.findAvailableRideForDriver(TestConstants.DRIVER_ID);
    }

    @Then("Short ride response should be present and contain ride with status {string}")
    public void shortRideResponseShouldBePresentAndContainRideWithStatus(String rideStatusName) {
        assertThat(shortRideResponse)
                .isNotNull()
                .extracting(ShortRideResponse::rideStatus)
                .isEqualTo(RideStatus.valueOf(rideStatusName));
        verify(rideRepository).findFirstByDriverIdAndRideStatus(
                TestConstants.DRIVER_ID,
                RideStatus.WAITING_FOR_DRIVER_CONFIRMATION
        );
    }

    @And("Methods needed to retrieve all rides for passenger were called")
    public void methodsNeededToRetrieveAllRidesForPassengerWereCalled() {
        verify(rideRepository).findAllByPassengerIdAndRideStatus(anyLong(), any());
        verify(rideRepository, never()).findAllByDriverIdAndRideStatus(anyLong(), any());
    }

    @Given("Confirmed ride for passenger exists")
    public void confirmedRideForPassengerExists() {
        ride = TestUtils.rideWithStatus(RideStatus.ACTIVE);
    }

    @When("Passenger search for the confirmed ride")
    public void businessLogicToFindConfirmedRideForPassengerExists() {
        when(rideRepository.findFirstByPassengerIdAndRideStatusIn(anyLong(), anyList()))
                .thenReturn(Optional.of(ride));
        when(rideMapper.toShortRideResponse(any()))
                .thenCallRealMethod();

        shortRideResponse = rideService.findConfirmedRideForPassenger(TestConstants.PASSENGER_ID);
    }

    @Then("Ride response should be present and contain ride with status one of")
    public void rideResponseShouldBePresentAndContainRideWithStatusOneOf(List<String> expectedRideStatusNames) {
        List<RideStatus> expectedRideStatuses = expectedRideStatusNames.stream()
                .map(RideStatus::valueOf)
                .toList();

        assertThat(shortRideResponse)
                .isNotNull()
                .extracting(ShortRideResponse::rideStatus)
                .isIn(expectedRideStatuses);
        verify(rideRepository).findFirstByPassengerIdAndRideStatusIn(
                TestConstants.PASSENGER_ID,
                RideStatus.getConfirmedRideStatusList()
        );
    }

    @Given("Valid request to create a ride")
    public void validRequestToCreateARide() {
        rideRequest = TestUtils.defaultRideRequest();
    }

    @When("Passenger creates ride")
    public void businessLogicForRideCreatingIsInvoked() {
        ride = TestUtils.rideWithStatus(RideStatus.WITHOUT_DRIVER);

        when(rideMapper.toRide(any()))
                .thenReturn(ride);
        when(rideRepository.save(any()))
                .thenReturn(ride);
        doNothing().when(rideRequestProducer)
                .sendRequestForDriver(any());
        when(rideMapper.toRideResponse(any()))
                .thenCallRealMethod();

        rideResponse = rideService.createRide(rideRequest);
    }

    @Then("Ride response should be present and contain ride with status {string}")
    public void rideResponseShouldBePresentAndContainRideWithStatus(String rideStatusName) {
        assertThat(rideResponse)
                .isNotNull()
                .extracting(RideResponse::rideStatus)
                .isEqualTo(RideStatus.valueOf(rideStatusName));
    }

    @And("Methods needed to create ride were called")
    public void methodsNeededToCreateRideWereCalled() {
        verify(rideMapper).toRide(rideRequest);
        verify(rideRepository).save(ride);
        verify(rideRequestProducer).sendRequestForDriver(any());
        verify(rideMapper).toRideResponse(ride);
    }

    @Given("Valid request for driver updating and isAvailable is true")
    public void validRequestForDriverUpdatingAndIsAvailableIsTrue() {
        updateRideDriverRequest = TestUtils.updateRideDriverRequestWithDriver();
    }

    @When("Service updating driver for a ride")
    public void businessLogicForUpdatingDriverForARideIsInvoked() {
        ride = TestUtils.rideWithStatus(RideStatus.WITHOUT_DRIVER);

        when(rideRepository.findById(anyLong()))
                .thenReturn(Optional.of(ride));
        when(rideRepository.save(any()))
                .thenReturn(ride);
        when(rideMapper.toRideResponse(any()))
                .thenCallRealMethod();
        doNothing().when(rideRequestProducer)
                .sendRequestForDriver(any());

        rideService.handleUpdateDriver(updateRideDriverRequest);
    }

    @And("Methods needed to handle request with existing driver were called")
    public void methodsNeededToHandleRequestWithExistingDriverWereCalled() {
        verify(rideRepository).findById(TestConstants.RIDE_ID);
        verify(rideRepository).save(ride);
        verify(rideMapper).toRideResponse(ride);
    }

    @Given("Valid request for driver updating and isAvailable is false")
    public void validRequestForDriverUpdatingAndIsAvailableIsFalse() {
        updateRideDriverRequest = TestUtils.updateRideDriverRequestWithoutDriver();
    }

    @And("Another request to find a driver for a ride was sent")
    public void anotherRequestToFindADriverForARideWasSent() {
        verify(rideRequestProducer).sendRequestForDriver(any());
        verify(rideRepository, never()).findById(anyLong());
        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Then("Ride should have status {string}")
    public void rideShouldHaveStatus(String expectedRideStatus) {
        assertThat(ride.getRideStatus())
                .isEqualTo(RideStatus.valueOf(expectedRideStatus));
    }
}
