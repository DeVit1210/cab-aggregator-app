package com.modsen.payment.mapper;

import com.modsen.payment.dto.request.CreditCardRequest;
import com.modsen.payment.dto.response.CreditCardResponse;
import com.modsen.payment.model.CreditCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CreditCardMapper {
    @Mapping(target = "isDefault", source = "default")
    CreditCard toCreditCard(CreditCardRequest request);

    CreditCardResponse toCreditCardResponse(CreditCard creditCard);

    List<CreditCardResponse> toCreditCardListResponse(List<CreditCard> creditCardList);
}
