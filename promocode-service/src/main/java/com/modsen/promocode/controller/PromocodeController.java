package com.modsen.promocode.controller;

import com.modsen.promocode.constants.ControllerMappings;
import com.modsen.promocode.dto.request.PromocodeRequest;
import com.modsen.promocode.dto.request.UpdateDiscountPercentRequest;
import com.modsen.promocode.dto.response.PromocodeListResponse;
import com.modsen.promocode.dto.response.PromocodeResponse;
import com.modsen.promocode.service.PromocodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ControllerMappings.PROMOCODE_CONTROLLER)
@RequiredArgsConstructor
public class PromocodeController {
    private final PromocodeService promocodeService;

    @GetMapping
    public PromocodeListResponse findAllPromocodes() {
        return promocodeService.findAllPromocodes();
    }

    @GetMapping("/{promocodeId}")
    public PromocodeResponse findPromocodeById(@PathVariable Long promocodeId) {
        return promocodeService.findPromocodeById(promocodeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromocodeResponse createPromocode(@Valid @RequestBody PromocodeRequest request) {
        return promocodeService.createPromocode(request);
    }

    @PatchMapping
    public PromocodeResponse updatePromocodeDiscountPercent(@Valid @RequestBody UpdateDiscountPercentRequest request) {
        return promocodeService.updatePromocode(request);
    }

    @DeleteMapping("/{promocodeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePromocode(@PathVariable Long promocodeId) {
        promocodeService.deletePromocode(promocodeId);
    }
}
