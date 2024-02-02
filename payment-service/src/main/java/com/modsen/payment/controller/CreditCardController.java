package com.modsen.payment.controller;


import com.modsen.payment.constants.ControllerMappings;
import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.response.CreditCardListResponse;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.enums.Role;
import com.modsen.payment.service.CreditCardService;
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
@RequestMapping(ControllerMappings.CREDIT_CARD_CONTROLLER)
@RequiredArgsConstructor
public class CreditCardController {
    private final CreditCardService creditCardService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardResponse createCreditCard(@RequestBody CreditCardRequest request) {
        return creditCardService.createCreditCard(request);
    }

    @GetMapping
    public CreditCardListResponse findAllByIdAndRole(@RequestParam Role role, @RequestParam Long cardHolderId) {
        return creditCardService.findAllCreditCards(cardHolderId, role);
    }

    @GetMapping("/{cardId}")
    public CreditCardResponse findCreditCardById(@PathVariable Long cardId) {
        return creditCardService.findCardById(cardId);
    }

    @PutMapping("/default/{cardId}")
    public CreditCardResponse changeDefaultCreditCard(@PathVariable Long cardId) {
        return creditCardService.setDefaultCreditCard(cardId);
    }
}
