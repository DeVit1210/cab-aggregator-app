package com.modsen.driver.dto.response;

import com.modsen.driver.enums.DriverStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private DriverStatus status;
}
