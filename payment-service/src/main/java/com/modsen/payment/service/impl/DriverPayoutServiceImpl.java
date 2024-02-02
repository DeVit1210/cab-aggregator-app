package com.modsen.payment.service.impl;

import com.modsen.payment.dto.request.DriverPayoutRequest;
import com.modsen.payment.dto.request.PageSettingsRequest;
import com.modsen.payment.dto.response.DriverPayoutListResponse;
import com.modsen.payment.dto.response.DriverPayoutResponse;
import com.modsen.payment.dto.response.Paged;
import com.modsen.payment.exception.IncufficientAccountBalanceException;
import com.modsen.payment.mapper.DriverPayoutMapper;
import com.modsen.payment.model.DriverAccount;
import com.modsen.payment.model.DriverPayout;
import com.modsen.payment.repository.DriverPayoutRepository;
import com.modsen.payment.service.DriverAccountService;
import com.modsen.payment.service.DriverPayoutService;
import com.modsen.payment.utils.PageRequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverPayoutServiceImpl implements DriverPayoutService {
    private final DriverPayoutRepository driverPayoutRepository;
    private final DriverPayoutMapper driverPayoutMapper;
    private final DriverAccountService driverAccountService;

    @Override
    @Transactional
    public DriverPayoutResponse createPayout(DriverPayoutRequest request) {
        DriverAccount driverAccount = driverAccountService.findAccountByDriverId(request.getDriverId());
        BigDecimal accountBalance = driverAccount.getAmount();
        BigDecimal amountToWithdraw = request.getAmount();
        if(amountToWithdraw.compareTo(accountBalance) >= 0) {
            throw new IncufficientAccountBalanceException(amountToWithdraw);
        }

        DriverPayout driverPayout = DriverPayout.builder()
                .account(driverAccount)
                .withdrawAmount(amountToWithdraw)
                .creditCardId(request.getCreditCardId())
                .build();
        BigDecimal leftoverAmount = accountBalance.subtract(amountToWithdraw);
        driverPayoutRepository.save(driverPayout);

        return driverPayoutMapper.toDriverPayoutResponse(driverPayout, leftoverAmount);
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
}
