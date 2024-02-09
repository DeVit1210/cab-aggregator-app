package com.modsen.payment.mapper;

import com.modsen.payment.dto.response.StripeCustomerResponse;
import com.modsen.payment.model.StripeCustomer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StripeCustomerMapper {
    StripeCustomerResponse toStripeCustomerResponse(StripeCustomer customer);
}
