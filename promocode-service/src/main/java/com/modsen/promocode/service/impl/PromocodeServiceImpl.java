package com.modsen.promocode.service.impl;

import com.modsen.promocode.dto.request.PromocodeRequest;
import com.modsen.promocode.dto.request.UpdateDiscountPercentRequest;
import com.modsen.promocode.dto.response.PromocodeListResponse;
import com.modsen.promocode.dto.response.PromocodeResponse;
import com.modsen.promocode.exception.PromocodeAlreadyExistsException;
import com.modsen.promocode.exception.PromocodeNotFoundException;
import com.modsen.promocode.mapper.PromocodeMapper;
import com.modsen.promocode.model.Promocode;
import com.modsen.promocode.repository.PromocodeRepository;
import com.modsen.promocode.service.PromocodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromocodeServiceImpl implements PromocodeService {
    private final PromocodeRepository promocodeRepository;
    private final PromocodeMapper promocodeMapper;

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
    public Promocode findByName(String promocodeName) {
        return promocodeRepository.findByName(promocodeName)
                .orElseThrow(() -> new PromocodeNotFoundException(promocodeName));
    }

    @Override
    public void deletePromocode(Long promocodeId) {
        Optional<Promocode> promocode = promocodeRepository.findById(promocodeId);
        promocode.ifPresentOrElse(promocodeRepository::delete, () -> {
            throw new PromocodeNotFoundException(promocodeId);
        });
    }

    private void validatePromocodeRequest(PromocodeRequest request) {
        String promocodeName = request.getName();
        if (promocodeRepository.existsByName(promocodeName)) {
            throw new PromocodeAlreadyExistsException(promocodeName);
        }
    }
}
