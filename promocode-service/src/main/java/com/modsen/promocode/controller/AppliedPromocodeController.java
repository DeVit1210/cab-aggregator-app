package com.modsen.promocode.controller;

import com.modsen.promocode.constants.ServiceMappings;
import com.modsen.promocode.dto.request.ApplyPromocodeRequest;
import com.modsen.promocode.dto.response.AppliedPromocodeResponse;
import com.modsen.promocode.service.AppliedPromocodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ServiceMappings.APPLIED_PROMOCODE_CONTROLLER)
@RequiredArgsConstructor
public class AppliedPromocodeController {
    private final AppliedPromocodeService appliedPromocodeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppliedPromocodeResponse applyPromocode(@Valid @RequestBody ApplyPromocodeRequest request) {
        return appliedPromocodeService.applyPromocode(request);
    }

    @PutMapping("/{promocodeId}")
    public AppliedPromocodeResponse confirmPromocodeAppliance(@PathVariable Long promocodeId) {
        return appliedPromocodeService.confirmAppliedPromocode(promocodeId);
    }

    @GetMapping("not-confirmed")
    public AppliedPromocodeResponse findNotConfirmedPromocode(@RequestParam Long passengerId) {
        return appliedPromocodeService.findNotConfirmedAppliedPromocode(passengerId);
    }

    @GetMapping("/{appliedPromocodeId}")
    public AppliedPromocodeResponse findAppliedPromocodeById(@PathVariable Long appliedPromocodeId) {
        return appliedPromocodeService.findAppliedPromocodeById(appliedPromocodeId);
    }
}
