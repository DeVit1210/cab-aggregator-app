package com.modsen.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stripe_customers")
public class StripeCustomer {
    @Id
    private Long id;
    @Column(name = "stripe_customer_id")
    private String stripeCustomerId;
}
