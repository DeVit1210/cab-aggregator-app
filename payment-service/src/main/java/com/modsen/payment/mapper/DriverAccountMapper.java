package com.modsen.payment.mapper;

import com.modsen.payment.dto.response.DriverAccountResponse;
import com.modsen.payment.model.DriverAccount;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DriverAccountMapper {
    DriverAccountResponse toDriverAccountResponse(DriverAccount driverAccount);
}
