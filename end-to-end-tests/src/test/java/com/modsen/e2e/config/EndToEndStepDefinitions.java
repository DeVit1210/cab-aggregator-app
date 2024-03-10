package com.modsen.e2e.config;

import com.modsen.e2e.client.DriverServiceClient;
import com.modsen.e2e.client.PaymentServiceClient;
import com.modsen.e2e.client.PromocodeServiceClient;
import com.modsen.e2e.client.RatingServiceClient;
import com.modsen.e2e.client.RideCostServiceClient;
import com.modsen.e2e.client.RideOperationsServiceClient;
import com.modsen.e2e.client.RideServiceClient;
import com.modsen.e2e.constants.TestConstants;
import com.modsen.e2e.dto.request.FinishRideRequest;
import com.modsen.e2e.dto.request.RatingRequest;
import com.modsen.e2e.dto.request.RideCostRequest;
import com.modsen.e2e.dto.request.RideRequest;
import com.modsen.e2e.dto.response.AppliedPromocodeResponse;
import com.modsen.e2e.dto.response.DriverAccountResponse;
import com.modsen.e2e.dto.response.DriverResponse;
import com.modsen.e2e.dto.response.PaymentResponse;
import com.modsen.e2e.dto.response.RatingListResponse;
import com.modsen.e2e.dto.response.RatingResponse;
import com.modsen.e2e.dto.response.RideCostResponse;
import com.modsen.e2e.dto.response.RideResponse;
import com.modsen.e2e.dto.response.ShortRideResponse;
import com.modsen.e2e.enums.ApplianceStatus;
import com.modsen.e2e.enums.DriverStatus;
import com.modsen.e2e.enums.RideStatus;
import com.modsen.e2e.enums.Role;
import com.modsen.e2e.utils.TestUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.awaitility.Awaitility;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class EndToEndStepDefinitions {
    private final RideServiceClient rideServiceClient;
    private final DriverServiceClient driverServiceClient;
    private final RideOperationsServiceClient rideOperationsServiceClient;
    private final RideCostServiceClient rideCostServiceClient;
    private final PromocodeServiceClient promocodeServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final RatingServiceClient ratingServiceClient;

    private RideCostRequest rideCostRequest;
    private RideRequest rideRequest;
    private FinishRideRequest finishRideRequest;
    private RatingRequest ratingRequest;

    private RideCostResponse rideCostResponse;
    private RideResponse rideResponse;
    private ShortRideResponse shortRideResponse;
    private RatingResponse ratingResponse;

    private Long appliedPromocodeId;
    private Long previousRideDriverId;
    private Long currentRideDriverId;
    private BigDecimal driverBalance;
    private int driverRatesQuantity;

    @Given("Valid request to calculate ride cost")
    public void validRequestToCalculateRideCost() {
        rideCostRequest = TestUtils.defaultCalculateRideCostRequest();
    }

    @And("Valid promocode is entered for passenger")
    public void validPromocodeIsEnteredForPassenger() {
        Long passengerId = TestConstants.PASSENGER_ID;
        AppliedPromocodeResponse promocodeResponse = promocodeServiceClient.findNotConfirmedPromocode(passengerId);

        appliedPromocodeId = promocodeResponse.id();
    }

    @When("Passenger invokes a method to calculate ride cost")
    public void passengerInvokesAMethodToCalculateRideCost() {
        rideCostResponse = rideCostServiceClient.calculateRideCost(rideCostRequest);
    }

    @Then("Total ride cost should be lower than base cost")
    public void totalRideCostShouldBeLowerThanBaseCost() {
        assertThat(rideCostResponse.discountedCost())
                .isLessThan(rideCostResponse.rideCost());
    }

    @Given("Valid request to create a ride")
    public void validRequestToCreateRideForPassenger() {
        rideRequest = TestUtils.defaultRideRequest();
    }

    @When("Passenger invokes method to create a ride")
    public void passengerInvokesMethodToCreateRide() {
        rideResponse = rideServiceClient.createRide(rideRequest);
    }

    @Then("Ride should be created and have a status of {string}")
    public void rideShouldBeCreatedForPassengerWithIdAndHaveAStatusOf(String expectedRideStatus) {
        assertThat(rideResponse.rideStatus())
                .isEqualTo(RideStatus.valueOf(expectedRideStatus));
    }

    @And("In range of {int} seconds ride status should change to the {string} status")
    public void rideStatusShouldChangeToTheStatus(int maxDelay, String newRideStatus) {
        Awaitility.await()
                .atMost(Duration.ofSeconds(maxDelay))
                .until(rideStatusShouldBeChangedCondition(newRideStatus));
    }

    @And("Status of driver assigned to the ride should be {string}")
    public void statusOfDriverAssignedToTheRideShouldBe(String expectedDriverStatus) {
        currentRideDriverId = rideResponse.driverId();
        Awaitility.await()
                .atMost(Duration.ofSeconds(3))
                .until(driverStatusShouldBeChangedCondition(currentRideDriverId, expectedDriverStatus));
    }

    @Given("Available ride for driver with id {long}")
    public void availableRideForDriver(Long driverId) {
        shortRideResponse = rideServiceClient.findAvailableRideForDriver(driverId);
        rideResponse = rideServiceClient.findRideById(shortRideResponse.id());
        currentRideDriverId = rideResponse.driverId();
    }

    @When("Driver invokes method to dismiss the ride")
    public void driverInvokesMethodToDismissTheRide() {
        previousRideDriverId = shortRideResponse.driverId();
        rideResponse = rideOperationsServiceClient.dismissRide(shortRideResponse.id());
    }

    @Then("Ride status should change to the {string} status")
    public void rideStatusShouldChangeToTheStatus(String newExpectedRideStatus) {
        assertThat(rideResponse.rideStatus())
                .isEqualTo(RideStatus.valueOf(newExpectedRideStatus));
    }

    @And("Previous driver status should be set to {string}")
    public void previousDriverStatusShouldBeSetTo(String newExpectedDriverStatus) {
        Awaitility
                .await()
                .atMost(Duration.ofSeconds(3))
                .until(driverStatusShouldBeChangedCondition(previousRideDriverId, newExpectedDriverStatus));
    }

    @And("Another driver should be assigned to the ride")
    public void anotherDriverShouldBeAssignedToTheRide() {
        assertThat(rideResponse.driverId())
                .isNotEqualTo(previousRideDriverId);
    }

    @When("Driver invokes method to accept the ride")
    public void driverInvokesMethodToAcceptTheRide() {
        rideResponse = rideOperationsServiceClient.acceptRide(rideResponse.id());
    }

    @And("Passenger should have confirmed ride")
    public void passengerShouldHaveConfirmedRide() {
        Long passengerId = TestConstants.PASSENGER_ID;
        ShortRideResponse confirmedRideForPassenger = rideServiceClient.findConfirmedRideForPassenger(passengerId);

        assertThat(confirmedRideForPassenger.rideStatus())
                .isIn(RideStatus.getConfirmedRideStatusList());
    }

    @When("Driver invokes method to start the ride")
    public void driverInvokesMethodToStartTheRide() {
        rideResponse = rideOperationsServiceClient.startRide(rideResponse.id());
    }

    @Given("Valid request to finish the ride")
    public void validRequestToFinishTheRide() {
        finishRideRequest = TestUtils.defaultFinishRideRequest();
        DriverAccountResponse driverAccount = paymentServiceClient.findAccountById(rideResponse.driverId());
        driverBalance = driverAccount.amount();
    }

    @When("Driver invokes method to finish the ride")
    public void driverInvokesMethodToFinishTheRide() {
        rideResponse = rideOperationsServiceClient.finishRide(finishRideRequest);
    }

    @And("Ride should be paid")
    public void rideShouldBePaidByPassengerWithId() {
        PaymentResponse payment = paymentServiceClient.findPaymentByRid(rideResponse.id());

        assertThat(payment).isNotNull();
    }

    @And("Promocode application should be confirmed")
    public void promocodeApplicationShouldBeConfirmed() {
        AppliedPromocodeResponse appliedPromocode =
                promocodeServiceClient.findAppliedPromocodeById(appliedPromocodeId);

        assertThat(appliedPromocode.passengerId())
                .isEqualTo(rideResponse.passengerId());
        assertThat(appliedPromocode.applianceStatus())
                .isEqualTo(ApplianceStatus.CONFIRMED);
    }

    @And("Balance on driver's account should increase")
    public void balanceOnDriverSAccountShouldIncrease() {
        DriverAccountResponse driverAccount = paymentServiceClient.findAccountById(rideResponse.driverId());
        BigDecimal updatedDriverBalance = driverAccount.amount();

        assertThat(updatedDriverBalance)
                .isGreaterThan(driverBalance);
    }

    @Given("Valid request from passenger to rate driver")
    public void validRequestFromPassengerToRateDriver() {
        Long driverId = rideResponse.driverId();
        ratingRequest = TestUtils.ratingRequestForDriverAndRide(
                driverId,
                rideResponse.id()
        );
        RatingListResponse allDriverRatings = ratingServiceClient.getAllRatingsForPerson(driverId, Role.DRIVER.name());
        driverRatesQuantity = allDriverRatings.ratingList().size();
    }

    @When("Passenger invokes method to rate a driver")
    public void passengerInvokesMethodToRateADriver() {
        ratingResponse = ratingServiceClient.createRating(ratingRequest);
    }

    @Then("Rating should be created and driver rating count should increase")
    public void ratingShouldBeCreatedAndDriverRatingCountShouldIncrease() {
        Long driverId = rideResponse.driverId();
        RatingListResponse allDriverRatings = ratingServiceClient.getAllRatingsForPerson(driverId, Role.DRIVER.name());
        int newDriverRatesQuantity = allDriverRatings.ratingList().size();

        assertThat(ratingResponse)
                .isNotNull();
        assertThat(newDriverRatesQuantity)
                .isGreaterThan(driverRatesQuantity);
    }

    private Callable<Boolean> rideStatusShouldBeChangedCondition(String newRideStatus) {
        return () -> {
            rideResponse = rideServiceClient.findRideById(rideResponse.id());

            return rideResponse.rideStatus()
                    .equals(RideStatus.valueOf(newRideStatus));
        };
    }

    private Callable<Boolean> driverStatusShouldBeChangedCondition(Long driverId, String expectedNewDriverStatus) {
        return () -> {
            DriverResponse driverResponse = driverServiceClient.findDriverById(driverId);

            return driverResponse.driverStatus()
                    .equals(DriverStatus.valueOf(expectedNewDriverStatus));
        };
    }
}
