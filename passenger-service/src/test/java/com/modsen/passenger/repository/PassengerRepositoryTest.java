package com.modsen.passenger.repository;

import com.modsen.passenger.constants.TestConstants;
import com.modsen.passenger.model.Passenger;
import com.modsen.passenger.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PassengerRepositoryTest {
    @Autowired
    private PassengerRepository passengerRepository;
    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passenger = TestUtils.defaultPassenger();
    }

    @Test
    void existsByEmail_UserExists_ReturnTrue() {
        passengerRepository.save(passenger);
        boolean existsByEmail = passengerRepository.existsByEmail(TestConstants.PASSENGER_EMAIL);
        assertTrue(existsByEmail);
    }

    @Test
    void existsByEmail_UserDoesNotExist_ReturnFalse() {
        boolean existsByEmail = passengerRepository.existsByEmail(TestConstants.PASSENGER_EMAIL);
        assertFalse(existsByEmail);
    }

    @Test
    void existsByPhoneNumber_UserExists_ReturnTrue() {
        passengerRepository.save(passenger);
        boolean existsByPhoneNumber = passengerRepository.existsByPhoneNumber(TestConstants.PASSENGER_PHONE_NUMBER);
        assertTrue(existsByPhoneNumber);
    }

    @Test
    void existsByPhoneNumber_UserDoesNotExist_ReturnFalse() {
        boolean existsByPhoneNumber = passengerRepository.existsByPhoneNumber(TestConstants.PASSENGER_PHONE_NUMBER);
        assertFalse(existsByPhoneNumber);
    }

    @AfterEach
    void tearDown() {
        passengerRepository.delete(passenger);
    }
}