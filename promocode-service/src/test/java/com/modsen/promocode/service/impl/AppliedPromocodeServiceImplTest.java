package com.modsen.promocode.service.impl;

import com.modsen.promocode.constants.MessageTemplates;
import com.modsen.promocode.constants.TestConstants;
import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.response.AppliedPromocodeResponse;
import com.modsen.promocode.dto.response.RideListResponse;
import com.modsen.promocode.enums.ApplianceStatus;
import com.modsen.promocode.enums.Role;
import com.modsen.promocode.exception.InvalidRideAmountForUsingPromocodeException;
import com.modsen.promocode.exception.PromocodeAlreadyAppliedException;
import com.modsen.promocode.exception.PromocodeMissingForPassengerException;
import com.modsen.promocode.exception.PromocodeNotFoundException;
import com.modsen.promocode.mapper.AppliedPromocodeMapperImpl;
import com.modsen.promocode.model.AppliedPromocode;
import com.modsen.promocode.model.Promocode;
import com.modsen.promocode.repository.AppliedPromocodeRepository;
import com.modsen.promocode.service.PromocodeService;
import com.modsen.promocode.service.feign.RideServiceClient;
import com.modsen.promocode.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppliedPromocodeServiceImplTest {
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

    @Test
    void applyPromocode_ValidApplyPromocodeRequest_ShouldReturnAppliedPromocode() {
        ApplyPromocodeRequest applyPromocodeRequest = TestUtils.defaultApplyPromocodeRequest();
        Promocode promocode = TestUtils.defaultPromocode();
        RideListResponse emptyRideListResponse = TestUtils.emptyRideListResponse();
        AppliedPromocode appliedPromocode = TestUtils.defaultAppliedPromocode();

        when(promocodeService.findByName(anyString()))
                .thenReturn(promocode);
        when(rideServiceClient.findAllRidesForPerson(anyLong(), anyString()))
                .thenReturn(emptyRideListResponse);
        when(appliedPromocodeMapper.toAppliedPromocode(any(Promocode.class), any(ApplyPromocodeRequest.class)))
                .thenReturn(appliedPromocode);
        when(appliedPromocodeRepository.save(any(AppliedPromocode.class)))
                .thenReturn(appliedPromocode);
        when(appliedPromocodeMapper.toAppliedPromocodeResponse(any(Promocode.class), any(AppliedPromocode.class)))
                .thenCallRealMethod();

        AppliedPromocodeResponse actualAppliedPromocode = appliedPromocodeService.applyPromocode(applyPromocodeRequest);

        assertNotNull(actualAppliedPromocode);
        verify(promocodeService).findByName(applyPromocodeRequest.getPromocodeName());
        verify(rideServiceClient).findAllRidesForPerson(applyPromocodeRequest.getPassengerId(), Role.PASSENGER.name());
        verify(appliedPromocodeMapper).toAppliedPromocode(promocode, applyPromocodeRequest);
        verify(appliedPromocodeRepository).save(appliedPromocode);
        verify(appliedPromocodeMapper).toAppliedPromocodeResponse(promocode, appliedPromocode);
    }

    @Test
    void applyPromocode_InvalidMinRidesAmount_ThrowInvalidRideAmountForUsingPromocodeException() {
        ApplyPromocodeRequest applyPromocodeRequest = TestUtils.defaultApplyPromocodeRequest();
        Promocode promocode = TestUtils.promocodeWithMinRidesAmount(10);
        RideListResponse emptyRideListResponse = TestUtils.emptyRideListResponse();
        String exceptionMessage = String.format(
                MessageTemplates.INVALID_RIDE_AMOUNT_FOR_PROMOCODE.getValue(),
                applyPromocodeRequest.getPromocodeName(),
                promocode.getMinRidesAmount(),
                emptyRideListResponse.quantity()
        );

        when(promocodeService.findByName(anyString()))
                .thenReturn(promocode);
        when(rideServiceClient.findAllRidesForPerson(anyLong(), anyString()))
                .thenReturn(emptyRideListResponse);

        assertThatThrownBy(() -> appliedPromocodeService.applyPromocode(applyPromocodeRequest))
                .isInstanceOf(InvalidRideAmountForUsingPromocodeException.class)
                .hasMessage(exceptionMessage);
        verify(promocodeService).findByName(applyPromocodeRequest.getPromocodeName());
        verify(rideServiceClient).findAllRidesForPerson(applyPromocodeRequest.getPassengerId(), Role.PASSENGER.name());
        verify(appliedPromocodeRepository, never()).save(any());
    }

    @Test
    void applyPromocode_DuplicatePromocodeUsageAttempt_ThrowPromocodeAlreadyAppliedException() {
        ApplyPromocodeRequest applyPromocodeRequest = TestUtils.defaultApplyPromocodeRequest();
        Promocode promocode = TestUtils.defaultPromocode();
        String exceptionMessage = String.format(
                MessageTemplates.PROMOCODE_ALREADY_APPLIED.getValue(),
                applyPromocodeRequest.getPromocodeName(),
                applyPromocodeRequest.getPassengerId()
        );

        when(promocodeService.findByName(anyString()))
                .thenReturn(promocode);
        when(appliedPromocodeRepository.existsByPromocodeAndPassengerId(any(Promocode.class), anyLong()))
                .thenReturn(true);

        assertThatThrownBy(() -> appliedPromocodeService.applyPromocode(applyPromocodeRequest))
                .isInstanceOf(PromocodeAlreadyAppliedException.class)
                .hasMessage(exceptionMessage);
        verify(promocodeService).findByName(applyPromocodeRequest.getPromocodeName());
        verify(appliedPromocodeRepository, never()).save(any());
    }

    @Test
    void findNotConfirmedAppliedPromocode_PromocodeExists_ShouldReturnPromocode() {
        Long passengerId = TestConstants.PASSENGER_ID;
        AppliedPromocode appliedPromocode = TestUtils.defaultAppliedPromocode();

        when(appliedPromocodeRepository.findByPassengerIdAndApplianceStatus(anyLong(), any()))
                .thenReturn(Optional.of(appliedPromocode));
        when(appliedPromocodeMapper.toAppliedPromocodeResponse(any(Promocode.class), any(AppliedPromocode.class)))
                .thenCallRealMethod();

        AppliedPromocodeResponse actualAppliedPromocode =
                appliedPromocodeService.findNotConfirmedAppliedPromocode(passengerId);

        assertNotNull(actualAppliedPromocode);
        verify(appliedPromocodeRepository).findByPassengerIdAndApplianceStatus(passengerId, ApplianceStatus.NOT_CONFIRMED);
        verify(appliedPromocodeMapper).toAppliedPromocodeResponse(appliedPromocode.getPromocode(), appliedPromocode);
    }

    @Test
    void findNotConfirmedAppliedPromocode_PromocodeDoesNotExist_ThrowPromocodeMissingForPassengerException() {
        Long passengerId = TestConstants.PASSENGER_ID;
        String exceptionMessage = String.format(MessageTemplates.NO_PROMOCODE_FOR_PASSENGER.getValue(), passengerId);

        when(appliedPromocodeRepository.findByPassengerIdAndApplianceStatus(anyLong(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appliedPromocodeService.findNotConfirmedAppliedPromocode(passengerId))
                .isInstanceOf(PromocodeMissingForPassengerException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    void confirmAppliedPromocode_ValidAppliedPromocodeId_ShouldReturnConfirmedPromocode() {
        AppliedPromocode appliedPromocode = TestUtils.defaultAppliedPromocode();

        when(appliedPromocodeRepository.findById(anyLong()))
                .thenReturn(Optional.of(appliedPromocode));
        when(appliedPromocodeRepository.save(any(AppliedPromocode.class)))
                .thenReturn(appliedPromocode);
        when(appliedPromocodeMapper.toAppliedPromocodeResponse(any(Promocode.class), any(AppliedPromocode.class)))
                .thenCallRealMethod();

        AppliedPromocodeResponse actualAppliedPromocode =
                appliedPromocodeService.confirmAppliedPromocode(appliedPromocode.getId());

        assertNotNull(actualAppliedPromocode);
        assertEquals(ApplianceStatus.CONFIRMED, appliedPromocode.getApplianceStatus());
        verify(appliedPromocodeRepository).findById(appliedPromocode.getId());
        verify(appliedPromocodeRepository).save(appliedPromocode);
        verify(appliedPromocodeMapper).toAppliedPromocodeResponse(appliedPromocode.getPromocode(), appliedPromocode);
    }

    @Test
    void confirmAppliedPromocode_AppliedPromocodeDoesNotExist_ThrowPromocodeNotFoundException() {
        Long appliedPromocodeId = TestConstants.PROMOCODE_ID;
        String exceptionMessage = String.format(MessageTemplates.PROMOCODE_NOT_FOUND_BY_ID.getValue(), appliedPromocodeId);

        when(appliedPromocodeRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appliedPromocodeService.confirmAppliedPromocode(appliedPromocodeId))
                .isInstanceOf(PromocodeNotFoundException.class)
                .hasMessage(exceptionMessage);
        verify(appliedPromocodeRepository, never()).save(any());
    }

    @Test
    void confirmAppliedPromocode_PromocodeAlreadyApplied_ThrowPromocodeAlreadyAppliedException() {
        AppliedPromocode appliedPromocode = TestUtils.defaultAppliedPromocode();
        appliedPromocode.setApplianceStatus(ApplianceStatus.CONFIRMED);
        String exceptionMessage = String.format(
                MessageTemplates.PROMOCODE_ALREADY_APPLIED.getValue(),
                appliedPromocode.getPromocode().getName(),
                appliedPromocode.getPassengerId()
        );

        when(appliedPromocodeRepository.findById(anyLong()))
                .thenReturn(Optional.of(appliedPromocode));

        assertThatThrownBy(() -> appliedPromocodeService.confirmAppliedPromocode(appliedPromocode.getId()))
                .isInstanceOf(PromocodeAlreadyAppliedException.class)
                .hasMessage(exceptionMessage);
        verify(appliedPromocodeRepository, never()).save(any());
    }
}