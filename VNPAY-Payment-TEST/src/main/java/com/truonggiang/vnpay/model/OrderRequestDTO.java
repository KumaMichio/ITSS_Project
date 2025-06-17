package com.truonggiang.vnpay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {

    private Long amount;
    private String orderInfo;
    private String bankCode;
    private String language;
}
