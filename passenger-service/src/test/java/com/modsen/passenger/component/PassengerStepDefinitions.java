package com.modsen.passenger.component;

import com.modsen.passenger.constants.TestConstants;
import com.modsen.passenger.dto.request.PassengerRequest;
import com.modsen.passenger.dto.response.AverageRatingListResponse;
import com.modsen.passenger.dto.response.AverageRatingResponse;
import com.modsen.passenger.dto.response.PassengerListResponse;
import com.modsen.passenger.dto.response.PassengerResponse;
import com.modsen.passenger.enums.Role;
import com.modsen.passenger.mapper.PassengerMapperImpl;
import com.modsen.passenger.model.Passenger;
import com.modsen.passenger.repository.PassengerRepository;
import com.modsen.passenger.service.feign.RatingServiceClient;
import com.modsen.passenger.service.impl.PassengerServiceImpl;
import com.modsen.passenger.utils.TestUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PassengerStepDefinitions {
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private PassengerMapperImpl passengerMapper;
    @Mock
    private RatingServiceClient ratingServiceClient;
    @InjectMocks
    private PassengerServiceImpl passengerService;

    private PassengerListResponse passengerListResponse;
    private PassengerResponse passengerResponse;
    private PassengerRequest passengerRequest;
    private Long passengerId = TestConstants.PASSENGER_ID;

    public PassengerStepDefinitions() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("{int} drivers in the database")
    public void driversInTheDatabase(Integer passengersQuantity) {
        when(passengerRepository.findAll())
                .thenReturn(Collections.nCopies(passengersQuantity, TestUtils.defaultPassenger()));
    }

    @When("The business logic to get all drivers is invoked")
    public void theBusinessLogicToGetAllDriversIsInvoked() {
        when(ratingServiceClient.findAllAverageRatings(any()))
                .thenReturn(AverageRatingListResponse.empty());
        when(passengerMapper.toPassengerListResponse(anyList(), anyList()))
                .thenCallRealMethod();

        passengerListResponse = passengerService.findAllPassengers();
    }

    @Then("The response should contain {int} drivers")
    public void theResponseShouldContainDrivers(Integer passengersQuantity) {
        assertThat(passengerListResponse.getPassengers())
                .hasSize(passengersQuantity);
        verify(passengerRepository).findAll();
        verify(passengerMapper).toPassengerListResponse(anyList(), anyList());
        verify(ratingServiceClient).findAllAverageRatings(Role.PASSENGER.name());
    }

    @Given("Passenger exists in database")
    public void passengerExistsInDatabase() {
        Passenger passenger = TestUtils.defaultPassenger();

        when(passengerRepository.findById(anyLong()))
                .thenReturn(Optional.of(passenger));
    }

    @When("The business logic to get passenger by id is invoked")
    public void theBusinessLogicToGetPassengerByIdIsInvoked() {
        when(ratingServiceClient.findAverageRating(anyLong(), anyString()))
                .thenReturn(AverageRatingResponse.empty(passengerId));
        when(passengerMapper.toPassengerResponse(any(), any()))
                .thenCallRealMethod();

        passengerResponse = passengerService.findPassengerById(passengerId);
    }

    @Then("The response should be present and contain found driver")
    public void theResponseShouldBePresentAndContainDriver() {
        assertThat(passengerResponse)
                .isNotNull()
                .extracting(PassengerResponse::id)
                .isEqualTo(passengerId);
        verify(passengerRepository).findById(passengerId);
        verify(passengerMapper).toPassengerResponse(any(), any());
        verify(ratingServiceClient).findAverageRating(passengerId, Role.PASSENGER.name());
    }

    @Given("Passenger does not exist in database")
    public void passengerDoesNotExistInDatabase() {
        when(passengerRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
    }

    @Given("Valid passenger request send to the service")
    public void validPassengerRequestSendToTheService() {
        passengerRequest = TestUtils.defaultPassengerRequest();
    }

    @When("The business logic to create passenger is invoked")
    public void theBusinessLogicToCreatePassengerIsInvoked() {
        Passenger passenger = TestUtils.defaultPassenger();

        when(passengerMapper.toPassenger(any()))
                .thenReturn(passenger);
        when(passengerRepository.save(any()))
                .thenReturn(passenger);
        when(passengerMapper.toPassengerResponse(any(), any()))
                .thenCallRealMethod();

        passengerResponse = passengerService.savePassenger(passengerRequest);
    }

    @Then("The response should be present and contain created driver")
    public void theResponseShouldBePresentAndContainCreatedDriver() {
        assertThat(passengerResponse)
                .isNotNull()
                .extracting(PassengerResponse::id)
                .isEqualTo(passengerId);
        verify(passengerMapper).toPassenger(passengerRequest);
        verify(passengerRepository).save(any());
        verify(passengerMapper).toPassengerResponse(any(), any());
    }

    @Given("Valid passenger update request and id send to the service")
    public void validPassengerUpdateRequestAndIdSendToTheService() {
        passengerRequest = TestUtils.passengerRequestWithEmail(TestConstants.PASSENGER_UPDATED_EMAIL);
    }

    @When("The business logic to update passenger by id is invoked")
    public void theBusinessLogicToUpdatePassengerByIdIsInvoked() {
        Passenger passenger = TestUtils.defaultPassenger();

        when(passengerRepository.findById(anyLong()))
                .thenReturn(Optional.of(passenger));
        doCallRealMethod().when(passengerMapper)
                .updatePassenger(any(), any());
        when(passengerRepository.save(any()))
                .thenReturn(passenger);
        when(ratingServiceClient.findAverageRating(anyLong(), anyString()))
                .thenReturn(AverageRatingResponse.empty(passengerId));
        when(passengerMapper.toPassengerResponse(any(), any()))
                .thenCallRealMethod();

        passengerResponse = passengerService.updatePassenger(passengerId, passengerRequest);
    }

    @Then("The response should be present and contain updated driver")
    public void theResponseShouldBePresentAndContainUpdatedDriver() {
        assertThat(passengerResponse)
                .isNotNull()
                .extracting(PassengerResponse::email)
                .isEqualTo(TestConstants.PASSENGER_UPDATED_EMAIL);
        verify(passengerMapper).updatePassenger(any(), any());
        verify(passengerRepository).save(any());
        verify(ratingServiceClient).findAverageRating(passengerId, Role.PASSENGER.name());
        verify(passengerMapper).toPassengerResponse(any(), any());
    }

    @When("The business logic to delete passenger by id is invoked")
    public void theBusinessLogicToDeletePassengerByIdIsInvoked() {
        doNothing().when(passengerRepository)
                .delete(any());

        passengerService.deletePassenger(passengerId);
    }

    @Then("The passenger should be deleted from database")
    public void thePassengerShouldBeDeletedFromDatabase() {
        verify(passengerRepository).findById(passengerId);
        verify(passengerRepository).delete(any());
    }
}
