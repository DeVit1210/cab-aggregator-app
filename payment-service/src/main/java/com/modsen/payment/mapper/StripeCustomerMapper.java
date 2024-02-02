package com.modsen.payment.mapper;

import com.modsen.payment.dto.response.StripeCustomerResponse;
import com.modsen.payment.model.StripeCustomer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StripeCustomerMapper {
    StripeCustomerResponse toStripeCustomerResponse(StripeCustomer customer);
}
