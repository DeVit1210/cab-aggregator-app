package com.modsen.payment.mapper;

import com.modsen.payment.dto.request.PaymentRequest;
import com.modsen.payment.dto.response.Paged;
import com.modsen.payment.dto.response.PaymentResponse;
import com.modsen.payment.model.Payment;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toPayment(PaymentRequest request);

    PaymentResponse toPaymentResponse(Payment payment);

    List<PaymentResponse> toPaymentResponseList(List<Payment> paymentList);

    default Paged<PaymentResponse> toPagedPaymentResponse(Page<Payment> paymentPage) {
        return Paged.<PaymentResponse>builder()
                .content(toPaymentResponseList(paymentPage.getContent()))
                .pageNumber(paymentPage.getNumber() + 1)
                .pageSize(paymentPage.getSize())
                .totalPageQuantity(paymentPage.getTotalPages())
                .hasPrevious(paymentPage.hasPrevious())
                .hasNext(paymentPage.hasNext())
                .build();
    }
}
