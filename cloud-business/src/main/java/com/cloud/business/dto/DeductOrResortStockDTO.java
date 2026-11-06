package com.cloud.business.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeductOrResortStockDTO {
    private String productCode;
    private Long quantity;
}
