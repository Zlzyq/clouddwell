package com.cloud.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPayMessageDTO {
    private String orderNo;
    private Long userId;
    private String traceId;
}
