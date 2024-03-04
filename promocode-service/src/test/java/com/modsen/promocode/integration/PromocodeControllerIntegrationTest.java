package com.modsen.promocode.integration;

import com.modsen.promocode.constants.MessageTemplates;
import com.modsen.promocode.constants.TestConstants;
import com.modsen.promocode.dto.request.PromocodeRequest;
import com.modsen.promocode.dto.request.UpdateDiscountPercentRequest;
import com.modsen.promocode.dto.response.PromocodeResponse;
import com.modsen.promocode.model.Promocode;
import com.modsen.promocode.repository.PromocodeRepository;
import com.modsen.promocode.utils.RestAssuredUtils;
import com.modsen.promocode.utils.TestUtils;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PromocodeControllerIntegrationTest extends BaseTestContainer {
    @Autowired
    private PromocodeRepository promocodeRepository;

    @AfterEach
    void tearDown() {
        promocodeRepository.deleteAll();
    }

    @Test
    @Sql("classpath:insert-promocodes-data.sql")
    void findAllPromocodes_Success() {
        RestAssuredUtils.findAllPromocodesResponse()
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.PROMOCODES_FIELD, iterableWithSize(3))
                .body(TestConstants.FieldNames.QUANTITY_FIELD, equalTo(3));
    }

    @Test
    @Sql("classpath:insert-promocodes-data.sql")
    void findPromocodeById_PromocodeExists_ShouldReturnPromocode() {
        Promocode expectedPromocode = TestUtils.defaultPromocode();
        Long promocodeId = expectedPromocode.getId();

        RestAssuredUtils.findPromocodeByIdResponse(promocodeId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.ID_FIELD, equalTo(promocodeId.intValue()))
                .body(TestConstants.FieldNames.PROMOCODE_NAME_FIELD, equalTo(expectedPromocode.getName()))
                .body(TestConstants.FieldNames.DISCOUNT_PERCENT_FIELD, equalTo(expectedPromocode.getDiscountPercent()));
    }

    @Test
    void findPromocodeById_PromocodeDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long promocodeId = TestConstants.PROMOCODE_ID;
        String expectedExceptionMessage = String.format(
                MessageTemplates.PROMOCODE_NOT_FOUND_BY_ID.getValue(),
                promocodeId
        );
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response findPromocodeByIdResponse = RestAssuredUtils.findPromocodeByIdResponse(promocodeId);

        extractApiExceptionInfoAndAssert(findPromocodeByIdResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    void createPromocode_ValidPromocodeRequest_ShouldReturnCreatedPromocode() {
        PromocodeRequest promocodeRequest = TestUtils.defaultPromocodeRequest();
        Promocode expectedPromocode = TestUtils.defaultPromocode();

        PromocodeResponse promocodeResponse = RestAssuredUtils.createPromocodeResponse(promocodeRequest)
                .then()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .as(PromocodeResponse.class);

        Optional<Promocode> createdPromocode = promocodeRepository.findById(promocodeResponse.id());
        assertTrue(createdPromocode.isPresent());
        assertEquals(expectedPromocode.getName(), createdPromocode.get().getName());
        assertEquals(expectedPromocode.getDiscountPercent(), createdPromocode.get().getDiscountPercent());
    }

    @Test
    @Sql("classpath:insert-promocodes-data.sql")
    void createPromocode_PromocodeAlreadyExists_ShouldReturnApiExceptionInfo() {
        PromocodeRequest promocodeRequest = TestUtils.defaultPromocodeRequest();
        String expectedExceptionMessage = String.format(
                MessageTemplates.PROMOCODE_ALREADY_EXISTS.getValue(),
                promocodeRequest.getName()
        );
        HttpStatus expectedHttpStatus = HttpStatus.CONFLICT;

        Response createPromocodeResponse = RestAssuredUtils.createPromocodeResponse(promocodeRequest);

        extractApiExceptionInfoAndAssert(createPromocodeResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-promocodes-data.sql")
    void updatePromocodeDiscountPercent_ValidPromocodeId_ShouldReturnUpdatedPromocode() {
        UpdateDiscountPercentRequest request = TestUtils.defaultUpdateDiscountPercentRequest();

        RestAssuredUtils.updatePromocodeDiscountPercentResponse(request)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK.value())
                .body(TestConstants.FieldNames.DISCOUNT_PERCENT_FIELD, equalTo(request.getDiscountPercent()))
                .body(TestConstants.FieldNames.ID_FIELD, equalTo(request.getPromocodeId().intValue()));
    }

    @Test
    void updatePromocodeDiscountPercent_PromocodeDoesNotExist_ShouldReturnApiExceptionInfo() {
        UpdateDiscountPercentRequest request = TestUtils.defaultUpdateDiscountPercentRequest();
        String expectedExceptionMessage = String.format(
                MessageTemplates.PROMOCODE_NOT_FOUND_BY_ID.getValue(),
                request.getPromocodeId()
        );
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response updateDiscountPercentResponse = RestAssuredUtils.updatePromocodeDiscountPercentResponse(request);

        extractApiExceptionInfoAndAssert(updateDiscountPercentResponse, expectedHttpStatus, expectedExceptionMessage);
    }

    @Test
    @Sql("classpath:insert-promocodes-data.sql")
    void deletePromocode_PromocodeExists_ShouldDeletePromocode() {
        Long promocodeId = TestConstants.PROMOCODE_ID;

        RestAssuredUtils.deletePromocodeResponse(promocodeId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        assertThat(promocodeRepository.findById(promocodeId))
                .isEmpty();
    }

    @Test
    void deletePromocode_PromocodeDoesNotExist_ShouldReturnApiExceptionInfo() {
        Long promocodeId = TestConstants.PROMOCODE_ID;
        String expectedExceptionMessage = String.format(
                MessageTemplates.PROMOCODE_NOT_FOUND_BY_ID.getValue(),
                promocodeId
        );
        HttpStatus expectedHttpStatus = HttpStatus.NOT_FOUND;

        Response deletePromocodeResponse = RestAssuredUtils.deletePromocodeResponse(promocodeId);

        extractApiExceptionInfoAndAssert(deletePromocodeResponse, expectedHttpStatus, expectedExceptionMessage);
    }
}
