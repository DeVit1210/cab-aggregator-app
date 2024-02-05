package com.modsen.promocode.service.impl;

import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.request.PromocodeRequest;
import com.modsen.promocode.dto.request.UpdateDiscountPercentRequest;
import com.modsen.promocode.dto.response.AppliedPromocodeResponse;
import com.modsen.promocode.dto.response.PromocodeListResponse;
import com.modsen.promocode.dto.response.PromocodeResponse;
import com.modsen.promocode.exception.ExpiredPromocodeException;
import com.modsen.promocode.exception.PromocodeAlreadyAppliedException;
import com.modsen.promocode.exception.PromocodeAlreadyExists;
import com.modsen.promocode.exception.PromocodeNotFoundException;
import com.modsen.promocode.mapper.AppliedPromocodeMapper;
import com.modsen.promocode.mapper.PromocodeMapper;
import com.modsen.promocode.model.AppliedPromocode;
import com.modsen.promocode.model.Promocode;
import com.modsen.promocode.repository.AppliedPromocodeRepository;
import com.modsen.promocode.repository.PromocodeRepository;
import com.modsen.promocode.service.PromocodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromocodeServiceImpl implements PromocodeService {
    private final PromocodeRepository promocodeRepository;
    private final AppliedPromocodeRepository appliedPromocodeRepository;
    private final PromocodeMapper promocodeMapper;
    private final AppliedPromocodeMapper appliedPromocodeMapper;

    @Override
    public PromocodeListResponse findAllPromocodes() {
        List<Promocode> promocodeList = promocodeRepository.findAll();
        List<PromocodeResponse> promocodeResponseList = promocodeMapper.toPromocodeListResponse(promocodeList);

        return PromocodeListResponse.of(promocodeResponseList);
    }

    @Override
    public PromocodeResponse findPromocodeById(Long promocodeId) {
        return promocodeRepository.findById(promocodeId)
                .map(promocodeMapper::toPromocodeResponse)
                .orElseThrow(() -> new PromocodeNotFoundException(promocodeId));
    }

    @Override
    public PromocodeResponse createPromocode(PromocodeRequest request) {
        validatePromocodeRequest(request);
        Promocode promocode = promocodeMapper.toPromocode(request);
        Promocode savedPromocode = promocodeRepository.save(promocode);

        return promocodeMapper.toPromocodeResponse(savedPromocode);
    }

    @Override
    public PromocodeResponse updatePromocode(UpdateDiscountPercentRequest request) {
        Long promocodeId = request.getPromocodeId();
        Promocode promocode = promocodeRepository.findById(promocodeId)
                .orElseThrow(() -> new PromocodeNotFoundException(promocodeId));
        promocode.setDiscountPercent(request.getDiscountPercent());
        Promocode updatedPromocode = promocodeRepository.save(promocode);

        return promocodeMapper.toPromocodeResponse(updatedPromocode);
    }

    @Override
    @Transactional
    public AppliedPromocodeResponse applyPromocode(ApplyPromocodeRequest request) {
        Promocode promocode = findByName(request.getPromocodeName());

        validateRequestedPromocode(promocode);
        validateAppliedPromocodeRequest(promocode, request);

        AppliedPromocode appliedPromocode = appliedPromocodeMapper.toAppliedPromocode(promocode, request);
        AppliedPromocode savedAppliedPromocode = appliedPromocodeRepository.save(appliedPromocode);

        return appliedPromocodeMapper.toAppliedPromocodeResponse(promocode, savedAppliedPromocode);
    }

    @Override
    public void deletePromocode(Long promocodeId) {
        Optional<Promocode> promocode = promocodeRepository.findById(promocodeId);
        promocode.ifPresentOrElse(promocodeRepository::delete, () -> {
            throw new PromocodeNotFoundException(promocodeId);
        });
    }

    private Promocode findByName(String promocodeName) {
        return promocodeRepository.findByName(promocodeName)
                .orElseThrow(() -> new PromocodeNotFoundException(promocodeName));
    }

    private void validateAppliedPromocodeRequest(Promocode actualPromocode, ApplyPromocodeRequest request) {
        Long passengerId = request.getPassengerId();
        String promocodeName = request.getPromocodeName();
        if (appliedPromocodeRepository.existsByPromocodeAndPassengerId(actualPromocode, passengerId)) {
            throw new PromocodeAlreadyAppliedException(promocodeName, passengerId);
        }
    }

    private void validatePromocodeRequest(PromocodeRequest request) {
        String promocodeName = request.getName();
        if (promocodeRepository.existsByName(promocodeName)) {
            throw new PromocodeAlreadyExists(promocodeName);
        }
    }

    private void validateRequestedPromocode(Promocode promocode) {
        LocalDate endDate = promocode.getEndDate();
        int maxUsageCount = promocode.getMaxUsageCount();
        int usageCount = appliedPromocodeRepository.countAllByPromocode(promocode);
        if (usageCount >= maxUsageCount || LocalDate.now().isAfter(endDate)) {
            deletePromocode(promocode.getId());
            throw new ExpiredPromocodeException(promocode.getName());
        }
    }
}
