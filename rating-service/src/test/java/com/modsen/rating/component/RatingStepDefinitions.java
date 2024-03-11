package com.modsen.rating.component;

import com.modsen.rating.constants.TestConstants;
import com.modsen.rating.dto.request.RatingRequest;
import com.modsen.rating.dto.response.AverageRatingResponse;
import com.modsen.rating.dto.response.RatingListResponse;
import com.modsen.rating.dto.response.RatingResponse;
import com.modsen.rating.dto.response.RideResponse;
import com.modsen.rating.enums.RatingValue;
import com.modsen.rating.enums.Role;
import com.modsen.rating.mapper.RatingMapperImpl;
import com.modsen.rating.model.Rating;
import com.modsen.rating.repository.RatingRepository;
import com.modsen.rating.service.feign.DriverServiceClient;
import com.modsen.rating.service.feign.PassengerServiceClient;
import com.modsen.rating.service.feign.RideServiceClient;
import com.modsen.rating.service.impl.RatingServiceImpl;
import com.modsen.rating.utils.TestUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RatingStepDefinitions {
    private final Long ratingId = TestConstants.RATING_ID;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private RatingMapperImpl ratingMapper;
    @Mock
    private DriverServiceClient driverServiceClient;
    @Mock
    private PassengerServiceClient passengerServiceClient;
    @Mock
    private RideServiceClient rideServiceClient;
    @InjectMocks
    private RatingServiceImpl ratingService;
    @Value("${rating.average.default}")
    private double defaultAverageRating;
    private RatingListResponse ratingListResponse;
    private RatingResponse ratingResponse;
    private AverageRatingResponse averageRatingResponse;
    private RatingRequest ratingRequest;
    private Rating rating;

    public RatingStepDefinitions() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("Rating exists in the database")
    public void ratingExistsInTheDatabase() {
        rating = TestUtils.defaultRating(Role.PASSENGER);
    }

    @When("Business logic for retrieving rating from database is invoked")
    public void businessLogicForRetrievingRatingFromDatabaseIsInvoked() {
        when(ratingRepository.findById(anyLong()))
                .thenReturn(Optional.of(rating));
        when(ratingMapper.toRatingResponse(any(Rating.class)))
                .thenCallRealMethod();

        ratingResponse = ratingService.getRatingById(ratingId);
    }

    @Then("Rating response should be present and contain rating")
    public void ratingResponseShouldBePresentAndContainRating() {
        assertThat(ratingResponse)
                .isNotNull()
                .extracting(RatingResponse::ratedPersonId)
                .isEqualTo(rating.getRatedPersonId());
    }

    @And("Methods needed to find rating by id should be called")
    public void methodsNeededToFindRatingByIdShouldBeCalled() {
        verify(ratingRepository).findById(ratingId);
        verify(ratingMapper).toRatingResponse(rating);
    }

    @Given("{int} ratings for person exists in the database")
    public void ratingsForPersonExistsInTheDatabase(int ratingQuantity) {
        List<Rating> ratingList = Collections.nCopies(ratingQuantity, TestUtils.defaultRating(Role.PASSENGER));

        when(ratingRepository.findAllByRoleAndRatedPersonId(any(), anyLong()))
                .thenReturn(ratingList);
    }

    @When("App user search for all his ratings")
    public void businessLogicForRetrievingRatingsForPersonIsInvoked() {
        when(ratingMapper.toRatingListResponse(anyList()))
                .thenCallRealMethod();

        ratingListResponse = ratingService.getAllRatings(TestConstants.RATED_PERSON_ID, Role.PASSENGER);
    }

    @Then("Rating list should has the size of {int}")
    public void ratingListShouldHasTheSizeOf(int ratingQuantity) {
        assertThat(ratingListResponse.ratingList())
                .hasSize(ratingQuantity);
    }

    @And("Methods needed to get all ratings for person should be called")
    public void methodsNeededToGetAllRatingsForPersonShouldBeCalled() {
        verify(ratingRepository).findAllByRoleAndRatedPersonId(Role.PASSENGER, TestConstants.RATED_PERSON_ID);
        verify(ratingMapper).toRatingListResponse(anyList());
    }

    @Given("No ratings for person exists in the database")
    public void noRatingsForPersonExistsInTheDatabase() {
        when(ratingRepository.findAllByRoleAndRatedPersonId(any(), anyLong()))
                .thenReturn(Collections.emptyList());
    }

    @Then("Empty rating list should be returned")
    public void emptyRatingListShouldBeReturned() {
        assertThat(ratingListResponse.ratingList())
                .isEmpty();
    }

    @Given("Rating with values for person exist into database")
    public void ratingWithValuesAndForPersonExistIntoDatabase(List<String> ratingValueNameList) {
        List<RatingValue> ratingValues = ratingValueNameList.stream()
                .map(RatingValue::valueOf)
                .toList();
        List<Rating> ratingList = TestUtils.ratingForRoleAndRatingValues(Role.PASSENGER, ratingValues);

        when(ratingRepository.findAllByRoleAndRatedPersonId(any(), anyLong()))
                .thenReturn(ratingList);
    }

    @When("App user search for his average rating")
    public void businessLogicForRetrievingAverageRatingIsInvoked() {
        averageRatingResponse = ratingService.getAverageRating(TestConstants.RATED_PERSON_ID, Role.PASSENGER);
    }

    @Then("The average rating with rating quantity of {int} and value of {double} should be returned")
    public void theAverageRatingWithRatingQuantityOfAndValueOfShouldBeReturned(int expectedSize, double expectedAverageRating) {
        assertEquals(expectedSize, averageRatingResponse.ratesQuantity());
        assertEquals(expectedAverageRating, averageRatingResponse.averageRating());
    }

    @Given("No rating for ride for {string} exist")
    public void noRatingForRideExist(String roleName) {
        ratingRequest = TestUtils.ratingRequestForRole(Role.valueOf(roleName));
    }

    @When("App user creates a rating for {string} with rating value of {string}")
    public void businessLogicForCreatingARatingIsInvoked(String roleName, String ratingValueName) {
        rating = TestUtils.ratingForRoleAndRatingValue(
                Role.valueOf(roleName),
                RatingValue.valueOf(ratingValueName)
        );
        ratingRequest.setRatingValue(ratingValueName);
        RideResponse rideResponse = RideResponse.builder()
                .id(TestConstants.RIDE_ID)
                .driverId(TestConstants.DRIVER_ID)
                .passengerId(TestConstants.PASSENGER_ID)
                .build();

        when(rideServiceClient.findRideById(anyLong()))
                .thenReturn(rideResponse);
        when(ratingMapper.toRating(any(RatingRequest.class)))
                .thenReturn(rating);
        when(ratingRepository.save(any(Rating.class)))
                .thenReturn(rating);
        when(ratingMapper.toRatingResponse(any(Rating.class)))
                .thenCallRealMethod();

        ratingResponse = ratingService.createRating(ratingRequest);
    }

    @Then("Rating response should contain created rating for {string} with rating value of {int}")
    public void ratingResponseShouldContainCreatedRatingRWithRatingValueOf(String roleName, int ratingValue) {
        assertThat(ratingResponse.role())
                .isEqualTo(Role.valueOf(roleName));
        assertThat(ratingResponse.ratingValue())
                .isEqualTo(ratingValue);
    }

    @And("Methods needed to create rating should be called")
    public void methodsNeededToCreateRatingShouldBeCalled() {
        verify(rideServiceClient).findRideById(anyLong());
        verify(ratingMapper).toRating(ratingRequest);
        verify(ratingRepository).save(rating);
        verify(ratingMapper).toRatingResponse(rating);
    }


    @Given("Rating for ride with rating value of {string} exists into database")
    public void ratingForRideWithRatingValueOfExistsIntoDatabase(String currentRatingValue) {
        rating = TestUtils.ratingForRoleAndRatingValue(Role.PASSENGER, RatingValue.valueOf(currentRatingValue));

        when(ratingRepository.findById(anyLong()))
                .thenReturn(Optional.of(rating));
    }

    @When("App user updates one of his previous ratings with new rating value of {string}")
    public void businessLogicForUpdatingRatingWithNewRatingValueOfIsInvoked(String newRatingValue) {
        when(ratingRepository.save(any()))
                .thenReturn(rating);
        when(ratingMapper.toRatingResponse(any()))
                .thenCallRealMethod();

        ratingResponse = ratingService.updateRating(ratingId, RatingValue.valueOf(newRatingValue));
    }

    @Then("Rating response should contain updated rating with rating value of {int}")
    public void ratingResponseShouldContainUpdatedRatingWithRatingValueOf(int expectedRatingValue) {
        assertThat(ratingResponse)
                .isNotNull()
                .extracting(RatingResponse::ratingValue)
                .isEqualTo(expectedRatingValue);
    }

    @And("Methods needed to update rating value should be called")
    public void methodsNeededToUpdateRatingValueShouldBeCalled() {
        verify(ratingRepository).findById(ratingId);
        verify(ratingRepository).save(rating);
        verify(ratingMapper).toRatingResponse(rating);
    }
}
