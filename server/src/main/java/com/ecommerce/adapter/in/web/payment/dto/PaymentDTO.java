package com.ecommerce.adapter.in.web.payment.dto;

import com.ecommerce.adapter.out.persistence.enums.PaymentMethod;
import com.ecommerce.adapter.out.persistence.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private String id;
    private PaymentMethod paymentMethod;
    private String gatewayTransactionId;
    private BigDecimal amount;
    private PaymentStatus status;
    private Instant createdAt;
}
