package com.modsen.promocode.component;


import com.modsen.promocode.constants.TestConstants;
import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.response.AppliedPromocodeResponse;
import com.modsen.promocode.enums.ApplianceStatus;
import com.modsen.promocode.enums.Role;
import com.modsen.promocode.mapper.AppliedPromocodeMapperImpl;
import com.modsen.promocode.model.AppliedPromocode;
import com.modsen.promocode.model.Promocode;
import com.modsen.promocode.repository.AppliedPromocodeRepository;
import com.modsen.promocode.service.PromocodeService;
import com.modsen.promocode.service.feign.RideServiceClient;
import com.modsen.promocode.service.impl.AppliedPromocodeServiceImpl;
import com.modsen.promocode.utils.TestUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PromocodeStepDefinitions {
    @Mock
    private PromocodeService promocodeService;
    @Mock
    private AppliedPromocodeRepository appliedPromocodeRepository;
    @Mock
    private AppliedPromocodeMapperImpl appliedPromocodeMapper;
    @Mock
    private RideServiceClient rideServiceClient;
    @InjectMocks
    private AppliedPromocodeServiceImpl appliedPromocodeService;

    private ApplyPromocodeRequest applyPromocodeRequest;
    private AppliedPromocodeResponse appliedPromocodeResponse;
    private AppliedPromocode appliedPromocode;


    public PromocodeStepDefinitions() {
        MockitoAnnotations.openMocks(this);
    }

    @Given("Valid apply promocode request")
    public void givenValidApplyPromocodeRequest() {
        applyPromocodeRequest = TestUtils.defaultApplyPromocodeRequest();
    }

    @When("Business logic for promocode appliance is invoked")
    public void businessLogicForPromocodeApplianceIsInvoked() {
        Promocode promocode = TestUtils.defaultPromocode();
        appliedPromocode = TestUtils.appliedPromocodeWithStatus(ApplianceStatus.NOT_CONFIRMED);

        when(promocodeService.findByName(anyString()))
                .thenReturn(promocode);
        when(rideServiceClient.findAllRidesForPerson(anyLong(), anyString()))
                .thenReturn(TestUtils.emptyRideListResponse());
        when(appliedPromocodeMapper.toAppliedPromocode(any(Promocode.class), any(ApplyPromocodeRequest.class)))
                .thenReturn(appliedPromocode);
        when(appliedPromocodeRepository.save(any(AppliedPromocode.class)))
                .thenReturn(appliedPromocode);
        when(appliedPromocodeMapper.toAppliedPromocodeResponse(any(Promocode.class), any(AppliedPromocode.class)))
                .thenCallRealMethod();

        appliedPromocodeResponse = appliedPromocodeService.applyPromocode(applyPromocodeRequest);
    }

    @And("Methods needed to apply promocode were called")
    public void methodsNeededToApplyPromocodeWereCalled() {
        verify(promocodeService).findByName(applyPromocodeRequest.getPromocodeName());
        verify(rideServiceClient).findAllRidesForPerson(applyPromocodeRequest.getPassengerId(), Role.PASSENGER.name());
        verify(appliedPromocodeMapper).toAppliedPromocodeResponse(any(), any());
        verify(appliedPromocodeRepository).save(appliedPromocode);
        verify(appliedPromocodeMapper).toAppliedPromocodeResponse(any(), any());
    }


    @Then("AppliedPromocodeResponse should be present and have the {string} status")
    public void appliedPromocodeResponseShouldBePresentAndHaveStatus(String applianceStatusName) {
        assertThat(appliedPromocodeResponse)
                .isNotNull()
                .extracting(AppliedPromocodeResponse::applianceStatus)
                .isEqualTo(ApplianceStatus.valueOf(applianceStatusName));
    }

    @Given("Valid passenger id")
    public void validPassengerId() {
        AppliedPromocode appliedPromocode = TestUtils.appliedPromocodeWithStatus(ApplianceStatus.NOT_CONFIRMED);

        when(appliedPromocodeRepository.findByPassengerIdAndApplianceStatus(anyLong(), any()))
                .thenReturn(Optional.of(appliedPromocode));
    }

    @When("Business logic for not confirmed promocode retrieval is invoked")
    public void businessLogicForNotConfirmedPromocodeRetrievalIsInvoked() {
        when(appliedPromocodeMapper.toAppliedPromocodeResponse(any(Promocode.class), any(AppliedPromocode.class)))
                .thenCallRealMethod();

        appliedPromocodeResponse = appliedPromocodeService.findNotConfirmedAppliedPromocode(TestConstants.PASSENGER_ID);
    }

    @And("Methods needed to find not confirmed promocode were invoked")
    public void methodsNeededToFindNotConfirmedPromocodeWereInvoked() {
        verify(appliedPromocodeRepository)
                .findByPassengerIdAndApplianceStatus(TestConstants.PASSENGER_ID, ApplianceStatus.NOT_CONFIRMED);
        verify(appliedPromocodeMapper).toAppliedPromocodeResponse(any(), any());
    }


    @Given("Valid promocode id")
    public void validPromocodeId() {
        appliedPromocode = TestUtils.defaultAppliedPromocode();

        when(appliedPromocodeRepository.findById(anyLong()))
                .thenReturn(Optional.of(appliedPromocode));
    }

    @When("Business logic for promocode appliance confirming is invoked")
    public void businessLogicForPromocodeApplianceConfirmingIsInvoked() {
        when(appliedPromocodeRepository.save(any()))
                .thenReturn(appliedPromocode);
        when(appliedPromocodeMapper.toAppliedPromocodeResponse(any(), any()))
                .thenCallRealMethod();

        appliedPromocodeResponse = appliedPromocodeService.confirmAppliedPromocode(TestConstants.PROMOCODE_ID);
    }

    @And("Methods needed to confirm promocode were invoked")
    public void methodsNeededToConfirmPromocodeWereInvoked() {
        verify(appliedPromocodeRepository).findById(TestConstants.PROMOCODE_ID);
        verify(appliedPromocodeRepository).save(appliedPromocode);
        verify(appliedPromocodeMapper).toAppliedPromocodeResponse(any(), any());
    }
}
