package com.modsen.promocode.service.impl;

import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.response.AppliedPromocodeResponse;
import com.modsen.promocode.dto.response.RideListResponse;
import com.modsen.promocode.enums.ApplianceStatus;
import com.modsen.promocode.enums.Role;
import com.modsen.promocode.exception.InvalidRideAmountForUsingPromocodeException;
import com.modsen.promocode.exception.PromocodeAlreadyAppliedException;
import com.modsen.promocode.exception.PromocodeMissingForPassengerException;
import com.modsen.promocode.exception.PromocodeNotFoundException;
import com.modsen.promocode.mapper.AppliedPromocodeMapper;
import com.modsen.promocode.model.AppliedPromocode;
import com.modsen.promocode.model.Promocode;
import com.modsen.promocode.repository.AppliedPromocodeRepository;
import com.modsen.promocode.service.AppliedPromocodeService;
import com.modsen.promocode.service.PromocodeService;
import com.modsen.promocode.service.feign.RideServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AppliedPromocodeServiceImpl implements AppliedPromocodeService {
    private final PromocodeService promocodeService;
    private final AppliedPromocodeRepository appliedPromocodeRepository;
    private final AppliedPromocodeMapper appliedPromocodeMapper;
    private final RideServiceClient rideServiceClient;

    @Override
    @Transactional
    public AppliedPromocodeResponse applyPromocode(ApplyPromocodeRequest request) {
        Promocode promocode = promocodeService.findByName(request.getPromocodeName());
        validateAppliedPromocodeRequest(promocode, request);
        AppliedPromocode appliedPromocode = appliedPromocodeMapper.toAppliedPromocode(promocode, request);
        AppliedPromocode savedAppliedPromocode = appliedPromocodeRepository.save(appliedPromocode);
        if (!isPromocodeStillValid(promocode)) {
            promocodeService.deletePromocode(promocode.getId());
        }

        return appliedPromocodeMapper.toAppliedPromocodeResponse(promocode, savedAppliedPromocode);
    }

    @Override
    public AppliedPromocodeResponse findNotConfirmedAppliedPromocode(Long passengerId) {
        AppliedPromocode appliedPromocode = appliedPromocodeRepository
                .findByPassengerIdAndApplianceStatus(passengerId, ApplianceStatus.NOT_CONFIRMED)
                .orElseThrow(() -> new PromocodeMissingForPassengerException(passengerId));
        Promocode promocode = appliedPromocode.getPromocode();

        return appliedPromocodeMapper.toAppliedPromocodeResponse(promocode, appliedPromocode);
    }

    @Override
    public AppliedPromocodeResponse confirmAppliedPromocode(Long appliedPromocodeId) {
        AppliedPromocode appliedPromocode = appliedPromocodeRepository.findById(appliedPromocodeId)
                .orElseThrow(() -> new PromocodeNotFoundException(appliedPromocodeId));
        Promocode promocode = appliedPromocode.getPromocode();
        if (appliedPromocode.getApplianceStatus().equals(ApplianceStatus.CONFIRMED)) {
            throw new PromocodeAlreadyAppliedException(promocode.getName(), appliedPromocode.getPassengerId());
        }
        appliedPromocode.setApplianceStatus(ApplianceStatus.CONFIRMED);
        appliedPromocodeRepository.save(appliedPromocode);

        return appliedPromocodeMapper.toAppliedPromocodeResponse(promocode, appliedPromocode);
    }

    private boolean isPromocodeStillValid(Promocode promocode) {
        LocalDate endDate = promocode.getEndDate();
        int maxUsageCount = promocode.getMaxUsageCount();
        int usageCount = appliedPromocodeRepository.countAllByPromocode(promocode);

        return usageCount < maxUsageCount && !LocalDate.now().isAfter(endDate);
    }

    private void validateAppliedPromocodeRequest(Promocode actualPromocode, ApplyPromocodeRequest request) {
        Long passengerId = request.getPassengerId();
        String promocodeName = request.getPromocodeName();
        if (appliedPromocodeRepository.existsByPromocodeAndPassengerId(actualPromocode, passengerId)) {
            throw new PromocodeAlreadyAppliedException(promocodeName, passengerId);
        }

        RideListResponse passengerRides = rideServiceClient.findAllRidesForPerson(passengerId, Role.PASSENGER.name());
        int rideQuantity = passengerRides.quantity();
        int minRidesAmount = actualPromocode.getMinRidesAmount();
        if (rideQuantity < minRidesAmount) {
            throw new InvalidRideAmountForUsingPromocodeException(promocodeName, minRidesAmount, rideQuantity);
        }
    }
}