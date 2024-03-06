package com.modsen.driver.utils;

import com.modsen.driver.constants.TestConstants;
import com.modsen.driver.dto.request.ChangeDriverStatusRequest;
import com.modsen.driver.dto.request.DriverRequest;
import com.modsen.driver.dto.response.AverageRatingListResponse;
import com.modsen.driver.dto.response.AverageRatingResponse;
import com.modsen.driver.dto.response.DriverAccountResponse;
import com.modsen.driver.enums.DriverStatus;
import com.modsen.driver.enums.Role;
import com.modsen.driver.model.Driver;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class TestUtils {
    public static Driver defaultDriver() {
        return driverWithStatus(DriverStatus.OFFLINE);
    }

    public static Driver driverWithStatus(DriverStatus driverStatus) {
        return Driver.builder()
                .id(TestConstants.DRIVER_ID)
                .email(TestConstants.DRIVER_EMAIL)
                .phoneNumber(TestConstants.DRIVER_PHONE_NUMBER)
                .firstName(TestConstants.DRIVER_FIRST_NAME)
                .lastName(TestConstants.DRIVER_SECOND_NAME)
                .licenceNumber(TestConstants.DRIVER_LICENCE_NUMBER)
                .driverStatus(driverStatus)
                .build();
    }

    public static DriverRequest defaultDriverRequest() {
        return driverRequestWithEmail(TestConstants.DRIVER_EMAIL);
    }

    public static DriverRequest driverRequestWithEmail(String email) {
        return DriverRequest.builder()
                .email(email)
                .phoneNumber(TestConstants.DRIVER_PHONE_NUMBER)
                .firstName(TestConstants.DRIVER_FIRST_NAME)
                .lastName(TestConstants.DRIVER_SECOND_NAME)
                .licenceNumber(TestConstants.DRIVER_LICENCE_NUMBER)
                .build();
    }

    public static DriverAccountResponse emptyDriverAccountResponse(Long driverId) {
        return new DriverAccountResponse(driverId, BigDecimal.ZERO);
    }


    public static AverageRatingListResponse emptyAverageRatingListResponse(List<AverageRatingResponse> averageRatingResponses) {
        return new AverageRatingListResponse(averageRatingResponses, Role.DRIVER, averageRatingResponses.size());
    }

    public static ChangeDriverStatusRequest changeDriverStatusRequestWithStatus(DriverStatus driverStatus) {
        return ChangeDriverStatusRequest.builder()
                .driverId(TestConstants.DRIVER_ID)
                .driverStatus(driverStatus)
                .build();
    }
}
