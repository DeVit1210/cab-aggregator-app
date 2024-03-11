package com.modsen.ride.component;

import com.modsen.ride.constants.TestConstants;
import com.modsen.ride.dto.request.ChangeDriverStatusRequest;
import com.modsen.ride.dto.request.FinishRideRequest;
import com.modsen.ride.dto.request.PaymentRequest;
import com.modsen.ride.dto.response.AppliedPromocodeResponse;
import com.modsen.ride.dto.response.RideResponse;
import com.modsen.ride.enums.DriverStatus;
import com.modsen.ride.enums.RideStatus;
import com.modsen.ride.kafka.producer.DriverStatusRequestProducer;
import com.modsen.ride.kafka.producer.RideRequestProducer;
import com.modsen.ride.mapper.RideOperationsMapper;
import com.modsen.ride.model.Ride;
import com.modsen.ride.service.RideService;
import com.modsen.ride.service.feign.PaymentServiceClient;
import com.modsen.ride.service.feign.PromocodeServiceClient;
import com.modsen.ride.service.impl.RideOperationsServiceImpl;
import com.modsen.ride.utils.TestUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RideOperationsStepDefinitions {
    private final Long rideId = TestConstants.RIDE_ID;
    @Mock
    private RideService rideService;
    @Mock
    private RideOperationsMapper rideOperationsMapper;
    @Mock
    private DriverStatusRequestProducer driverStatusRequestProducer;
    @Mock
    private RideRequestProducer rideRequestProducer;
    @Mock
    private PaymentServiceClient paymentServiceClient;
    @Mock
    private PromocodeServiceClient promocodeServiceClient;
    @InjectMocks
    private RideOperationsServiceImpl rideOperationsService;

    private Ride ride;
    private RideResponse rideResponse;
    private ChangeDriverStatusRequest changeDriverStatusRequest;
    private FinishRideRequest finishRideRequest;

    public RideOperationsStepDefinitions() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("Valid current ride status of {string}")
    public void validCurrentRideStatusOf(String currentRideStatus) {
        ride = TestUtils.rideWithStatus(RideStatus.valueOf(currentRideStatus));
    }

    @When("Driver accepts the ride")
    public void businessLogicForAcceptingRideIsInvoked() {
        changeDriverStatusRequest = TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.ON_WAY_TO_PASSENGER);
        rideResponse = TestUtils.defaultRideResponse();

        setupMocks();

        rideResponse = rideOperationsService.acceptRide(rideId);
    }

    @Then("Ride response should be present and contain a ride with status {string}")
    public void rideResponseShouldBePresentAndContainARideWithStatus(String expectedRideStatusName) {
        assertThat(ride)
                .isNotNull()
                .extracting(Ride::getRideStatus)
                .isEqualTo(RideStatus.valueOf(expectedRideStatusName));
    }

    @And("Request to change driver status to {string} is sent")
    public void requestToChangeDriverStatusToIsSent(String newDriverStatus) {
        assertThat(changeDriverStatusRequest.getDriverStatus())
                .isEqualTo(DriverStatus.valueOf(newDriverStatus));
        verify(driverStatusRequestProducer).changeDriverStatus(changeDriverStatusRequest);
    }

    @When("Driver dismisses the ride")
    public void businessLogicForDismissingRideIsInvoked() {
        changeDriverStatusRequest = TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.AVAILABLE);
        rideResponse = TestUtils.defaultRideResponse();

        setupMocks();
        doNothing().when(rideRequestProducer)
                .sendRequestForDriver(any());

        rideResponse = rideOperationsService.dismissRide(rideId);
    }

    @And("Request to find another driver for a ride was sent")
    public void requestToFindAnotherDriverForARideWasSent() {
        verify(rideRequestProducer).sendRequestForDriver(any());
    }

    @When("Driver notifies passenger about waiting")
    public void businessLogicForNotifyingPassengerAboutDriverWaitingIsInvoked() {
        changeDriverStatusRequest = TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.WAITING_FOR_PASSENGER);
        rideResponse = TestUtils.defaultRideResponse();

        setupMocks();

        rideResponse = rideOperationsService.notifyPassengerAboutWaiting(rideId);
    }

    @Given("Valid current ride status one of")
    public void validCurrentRideStatusOneOf(List<String> expectedRideStatusNames) {
        RideStatus rideStatus = RideStatus.valueOf(expectedRideStatusNames.get(0));
        ride = TestUtils.rideWithStatus(rideStatus);
    }

    @When("Passenger cancel the ride")
    public void businessLogicForCancelingRideIsInvoked() {
        changeDriverStatusRequest = TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.AVAILABLE);
        rideResponse = TestUtils.defaultRideResponse();

        setupMocks();

        rideResponse = rideOperationsService.cancelRide(rideId);
    }

    @When("Driver starts the ride")
    public void businessLogicForRideStartingIsInvoked() {
        changeDriverStatusRequest = TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.ON_TRIP);
        rideResponse = TestUtils.defaultRideResponse();

        setupMocks();

        rideResponse = rideOperationsService.startRide(rideId);
    }

    @And("Valid request to finish ride")
    public void validRequestToFinishRide() {
        finishRideRequest = TestUtils.defaultFinishRideRequest();
    }

    @When("Driver finishes the ride")
    public void businessLogicForRideFinishingIsInvoked() {
        changeDriverStatusRequest = TestUtils.changeDriverStatusRequestWithStatus(DriverStatus.AVAILABLE);
        rideResponse = TestUtils.defaultRideResponse();
        PaymentRequest paymentRequest = TestUtils.defaultPaymentRequest();
        AppliedPromocodeResponse appliedPromocodeResponse = TestUtils.defaultAppliedPromocodeResponse();

        setupMocks();
        when(rideOperationsMapper.toPaymentRequest(any(), any()))
                .thenReturn(paymentRequest);
        when(promocodeServiceClient.findNotConfirmedPromocode(anyLong()))
                .thenReturn(appliedPromocodeResponse);

        rideResponse = rideOperationsService.finishRide(finishRideRequest);
    }

    @Then("Payment processed")
    public void paymentProcessed() {
        verify(paymentServiceClient).createPayment(any());
    }

    @And("Promocode appliance attempted")
    public void promocodeApplianceAttempted() {
        verify(promocodeServiceClient).findNotConfirmedPromocode(TestConstants.PASSENGER_ID);
        verify(promocodeServiceClient).confirmPromocodeAppliance(anyLong());
    }

    private void setupMocks() {
        when(rideService.findRideById(anyLong()))
                .thenReturn(ride);
        doNothing().when(driverStatusRequestProducer)
                .changeDriverStatus(any());
        when(rideService.saveRide(any()))
                .thenReturn(rideResponse);
    }

}
