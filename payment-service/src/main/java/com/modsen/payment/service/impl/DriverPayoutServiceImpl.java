package com.modsen.payment.service.impl;

import com.modsen.payment.dto.request.DriverPayoutRequest;
import com.modsen.payment.dto.request.PageSettingsRequest;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.dto.response.DriverPayoutListResponse;
import com.modsen.payment.dto.response.DriverPayoutResponse;
import com.modsen.payment.dto.response.Paged;
import com.modsen.payment.enums.Role;
import com.modsen.payment.exception.InvalidCreditCardHolderException;
import com.modsen.payment.mapper.DriverPayoutMapper;
import com.modsen.payment.model.DriverAccount;
import com.modsen.payment.model.DriverPayout;
import com.modsen.payment.repository.DriverPayoutRepository;
import com.modsen.payment.service.CreditCardService;
import com.modsen.payment.service.DriverAccountService;
import com.modsen.payment.service.DriverPayoutService;
import com.modsen.payment.utils.PageRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverPayoutServiceImpl implements DriverPayoutService {
    private final DriverPayoutRepository driverPayoutRepository;
    private final DriverPayoutMapper driverPayoutMapper;
    private final DriverAccountService driverAccountService;
    private final CreditCardService creditCardService;

    @Override
    @Transactional
    public DriverPayoutResponse createPayout(DriverPayoutRequest request) {
        validateDriverPayoutRequest(request);

        DriverAccount updatedAccount = driverAccountService.withdraw(request.getDriverId(), request.getAmount());
        DriverPayout driverPayout = DriverPayout.builder()
                .account(updatedAccount)
                .withdrawAmount(request.getAmount())
                .creditCardId(request.getCreditCardId())
                .build();
        driverPayoutRepository.save(driverPayout);

        return driverPayoutMapper.toDriverPayoutResponse(driverPayout, updatedAccount.getAmount());
    }

    @Override
    public DriverPayoutListResponse getAllPayoutsForDriver(Long driverId) {
        DriverAccount driverAccount = driverAccountService.findAccountByDriverId(driverId);
        List<DriverPayout> payoutList = driverPayoutRepository.findAllByAccount(driverAccount);
        List<DriverPayoutResponse> payoutResponseList = driverPayoutMapper.toDriverPayoutListResponse(payoutList);

        return DriverPayoutListResponse.of(payoutResponseList);
    }

    @Override
    public Paged<DriverPayoutResponse> getAllPayouts(PageSettingsRequest request) {
        PageRequest pageRequest = PageRequestUtils.pageRequestForEntity(request, DriverPayout.class);
        Page<DriverPayout> payoutPage = driverPayoutRepository.findAll(pageRequest);
        PageRequestUtils.validatePageResponse(pageRequest, payoutPage);

        return driverPayoutMapper.toPagedDriverPayoutResponse(payoutPage);
    }

    private void validateDriverPayoutRequest(DriverPayoutRequest request) {
        Long creditCardId = request.getCreditCardId();
        CreditCardResponse creditCard = creditCardService.findCardById(creditCardId);
        if (!creditCard.getRole().equals(Role.DRIVER)) {
            throw new InvalidCreditCardHolderException(creditCardId, request.getDriverId(), Role.DRIVER);
        }
        if (!creditCard.getCardHolderId().equals(request.getDriverId())) {
            throw new InvalidCreditCardHolderException(creditCardId, request.getDriverId(), Role.DRIVER);
        }
    }
}
